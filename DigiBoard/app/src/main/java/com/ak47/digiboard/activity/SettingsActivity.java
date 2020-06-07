package com.ak47.digiboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/*
      -SignOut button
*/
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private FirebaseAuth mAuth;
    private android.app.AlertDialog signOutDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private TextView remainingCreditText;
    private Button getSomeCreditButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.setting);

        remainingCreditText = findViewById(R.id.remainingCreditTextView);
        getSomeCreditButton = findViewById(R.id.getCreditButton);

        SharedPreferences sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE);
        int initialSetupInt = sharedPreferences.getInt("initial_setup", 0);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        if (initialSetupInt == 1) {
            getSomeCreditButton.setVisibility(View.GONE);
            remainingCreditText.setVisibility(View.GONE);

        } else if (initialSetupInt == 2) {
            Query query = rootRef.child("AdminUsers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: getCredit from server");
                    remainingCreditText.setText(getString(R.string.remaining_credit) + " " + dataSnapshot.child("credit").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: Error: " + databaseError.getMessage());
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Button signOut = findViewById(R.id.signOut);
        signOut.setOnClickListener(v -> {
            signOutDialog = signOutAndCacheCleanDialog();
            signOutDialog.show();
        });

        getSomeCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogStyle)
                        .setMessage("Coming Soon")
                        .show();
            }
        });

    }

    private android.app.AlertDialog signOutAndCacheCleanDialog() {
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Alert")
                .setMessage("Are Really want to Sign Out")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                mGoogleSignInClient.signOut().addOnCompleteListener(SettingsActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mAuth.signOut();
                                        sendToLogInActivity();
                                    }
                                });
                            }
                        })
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss
                    }
                });
        return (alertDialogBuilder.create());
    }

    private void sendToLogInActivity() {
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}
