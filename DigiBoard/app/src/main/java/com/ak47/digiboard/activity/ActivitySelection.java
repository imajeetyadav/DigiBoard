package com.ak47.digiboard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/*
    #Done
    Role Selection Activity without any Layout
 */

public class ActivitySelection extends AppCompatActivity {
    private static final String TAG = "ActivitySelection";
    Intent intent;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity with No Layout

        Log.d(TAG, "ActivitySelection Started");

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE);
        int initialSetupInt = sharedPreferences.getInt("initial_setup", 0);

        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "control send to LoginActivity");
            intent = new Intent(this, LoginActivity.class);
        } else {
            Log.d(TAG, "Shared Preference Value : " + initialSetupInt);
            switch (initialSetupInt) {
                case 0:
                    intent = new Intent(this, ProfileSelectionActivity.class);
                    break;
                case 1:
                    intent = new Intent(this, CandidateMainActivity.class);
                    break;
                case 2:
                    intent = new Intent(this, ExaminerMainActivity.class);
                    break;
                default:
                    Log.d(TAG, "Shared Preference Value Error");
            }
        }
        startActivity(intent);
        finish();

    }
}
