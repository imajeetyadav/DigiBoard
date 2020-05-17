package com.ak47.digiboard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ak47.digiboard.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.loading.rotate.RotateLoading;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    //A constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    //Tag for the logs optional
    private static final String TAG = "LoginActivity";

    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    //creating a DatabaseReference
    private DatabaseReference rootRef;

    // Loading Animation
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        rotateLoading = findViewById(R.id.loginLoading);

        changeStatusBarColor();

        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
//                rotateLoading.setVisibility(View.VISIBLE);
                rotateLoading.start();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                rotateLoading.stop();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE);
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            rootRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    assert user != null;
                                    if (dataSnapshot.child("users").hasChild(user.getUid())) {
                                        editor.putInt("initial_setup", 1);
                                        editor.apply();
                                        rotateLoading.stop();
//                                        sendUserToStudentMainActivity();

                                    } else if (dataSnapshot.child("AdminUsers").hasChild(user.getUid())) {
                                        editor.putInt("initial_setup", 2);
                                        editor.apply();
                                        rotateLoading.stop();
//                                        sendUserToTeacherMainActivity();

                                    } else {
                                        editor.putInt("initial_setup", 0);
                                        editor.apply();
                                        rotateLoading.stop();
//                                        sendUserToProfileSelectionActivity();
                                    }
                                    Intent mainTeacherActivityIntent = new Intent(LoginActivity.this, ActivitySelection.class);
                                    mainTeacherActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainTeacherActivityIntent);
                                    finish();




                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "User Profile Input Error Message :-" + databaseError.getMessage());

                                }
                            });

//                            rotateLoading.setVisibility(View.GONE);
                            Log.e(TAG, "User Signed In");
                        } else {
                            rotateLoading.stop();
//                            rotateLoading.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToProfileSelectionActivity() {
        Intent profileSelectionActivityIntent = new Intent(LoginActivity.this, StudentMainActivity.class);
        profileSelectionActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileSelectionActivityIntent);
        finish();
    }


    private void sendUserToStudentMainActivity() {
        Intent mainStudentActivityIntent = new Intent(LoginActivity.this, StudentMainActivity.class);
        mainStudentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainStudentActivityIntent);
        finish();
    }

    private void sendUserToTeacherMainActivity() {
        Intent mainTeacherActivityIntent = new Intent(LoginActivity.this, TeacherMainActivity.class);
        mainTeacherActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainTeacherActivityIntent);
        finish();
    }


    //this method is called on click
    private void signIn() {
        //getting the google signIn intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
}
