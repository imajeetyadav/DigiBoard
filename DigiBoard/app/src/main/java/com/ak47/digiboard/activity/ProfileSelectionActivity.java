package com.ak47.digiboard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ak47.digiboard.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.victor.loading.rotate.RotateLoading;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/*
    #done
     Select Profile (candidate/examiner) then add Data

*/
public class ProfileSelectionActivity extends AppCompatActivity {

    private static final String TAG = "ProfileSelection";
    Button candidateButton, examinerButton;
    //creating a DatabaseReference
    private DatabaseReference rootRef;
    // Loading Animation
    private RotateLoading rotateLoading;
    String newToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_selection);


        // SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        changeStatusBarColor();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        rotateLoading = findViewById(R.id.loginLoading);
        candidateButton = findViewById(R.id.candidateButton);
        examinerButton = findViewById(R.id.examinerButton);

        final HashMap<String, String> profileMap = new HashMap<>();
        assert user != null;
        profileMap.put("name", user.getDisplayName());
        profileMap.put("email", user.getEmail());
        profileMap.put("profilePic", user.getPhotoUrl().toString());
        profileMap.put("createdDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        profileMap.put("token", newToken);

        candidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    candidateButton.setVisibility(View.GONE);
                    examinerButton.setVisibility(View.GONE);
                    rotateLoading.start();
                    initFCMNewToken("users");
                    rootRef.child("users").child(user.getUid()).setValue(profileMap);
                    editor.putInt("initial_setup", 1);
                    editor.apply();
                    rotateLoading.stop();
                    sendUserToStudentMainActivity();
                } catch (Exception e) {
                    Toast.makeText(ProfileSelectionActivity.this, "Sorry Error occur. Try Again Later ", Toast.LENGTH_LONG).show();
                }
            }
        });
        examinerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    candidateButton.setVisibility(View.GONE);
                    examinerButton.setVisibility(View.GONE);
                    rotateLoading.start();
                    initFCMNewToken("AdminUsers");
                    profileMap.put("credit", "10");
                    rootRef.child("AdminUsers").child(user.getUid()).setValue(profileMap);
                    editor.putInt("initial_setup", 2);
                    editor.apply();
                    rotateLoading.stop();
                    sendUserToTeacherMainActivity();
                } catch (Exception e) {
                    Toast.makeText(ProfileSelectionActivity.this, "Sorry Error occur. Try Again Later ", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void initFCMNewToken(final String rootNodeName) {
        // Firebase messaging Token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(ProfileSelectionActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.d(TAG, "Messaging token: " + newToken);
                rootRef.child(rootNodeName)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("token").setValue(newToken);
            }
        });

    }


    private void sendUserToStudentMainActivity() {
        Intent mainActivityIntent = new Intent(ProfileSelectionActivity.this, CandidateMainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    private void sendUserToTeacherMainActivity() {
        Intent mainActivityIntent = new Intent(ProfileSelectionActivity.this, ExaminerMainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if the user is not logged in
        //opening the login activity
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
