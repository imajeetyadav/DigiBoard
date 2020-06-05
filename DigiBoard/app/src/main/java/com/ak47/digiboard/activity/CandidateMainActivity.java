package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.common.CheckNotice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;
    /*
        Examiner Main Activity
            - Card Layout
            - profile pic ,name and email id
     */

public class CandidateMainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "StudentMainActivity";
    private FirebaseAuth mAuth;
    private ImageView imageView;
    private TextView textName, textEmail;
    // Loading Animation
    private RotateLoading rotateLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_candidate);

        rotateLoading = findViewById(R.id.mainLoading);

        rotateLoading.start();

        mAuth = FirebaseAuth.getInstance();
        changeStatusBarColor();

        imageView = findViewById(R.id.user_photo);
        textName = findViewById(R.id.user_name);
        textEmail = findViewById(R.id.user_email);

        CardView activeQuiz = findViewById(R.id.activeQuiz);
        CardView quizHistory = findViewById(R.id.history);
        CardView quizResult = findViewById(R.id.result);
        CardView quizNotification = findViewById(R.id.notification);
        CardView quizSetting = findViewById(R.id.setting);
        CardView quizAbout = findViewById(R.id.about);

        activeQuiz.setOnClickListener(this);
        quizHistory.setOnClickListener(this);
        quizResult.setOnClickListener(this);
        quizNotification.setOnClickListener(this);
        quizSetting.setOnClickListener(this);
        quizAbout.setOnClickListener(this);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        rootRef.keepSynced(true);

        // Checking Service Status and Message
        new CheckNotice(CandidateMainActivity.this);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String userProfile = String.valueOf(dataSnapshot.child("profilePic").getValue());
                    Picasso.get().load(userProfile).placeholder(R.drawable.ic_profile).into(imageView);
                    textName.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                    textEmail.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                } catch (Exception e) {
                    Log.e(TAG, "Profile pic fetch error");
                    Picasso.get().load(R.drawable.ic_profile).into(imageView);
                }
                rotateLoading.stop();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Profile Retrieval  - " + databaseError.getMessage());
                rotateLoading.stop();

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activeQuiz:
                startActivity(new Intent(CandidateMainActivity.this, CandidateMyQuizListActivity.class));
                break;
            case R.id.history:
                startActivity(new Intent(CandidateMainActivity.this, CandidateHistoryActivity.class));
                break;
            case R.id.result:
                startActivity(new Intent(CandidateMainActivity.this, CandidateResultActivity.class));
                break;
            case R.id.notification:
                startActivity(new Intent(CandidateMainActivity.this, NotificationActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(CandidateMainActivity.this, SettingsActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(CandidateMainActivity.this, AboutActivity.class));
                break;
            default:
                Log.d(TAG, "Invalid Selection");
                break;
        }

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
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
