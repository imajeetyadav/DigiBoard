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
import com.ak47.digiboard.common.CheckNoticeAndVersion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

/*
     Examiner Main Activity
         - Card Layout
         - profile pic ,name and email id
  */
public class ExaminerMainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "TeacherMainActivity";
    private FirebaseAuth mAuth;
    private ImageView imageView;
    private TextView textName, textEmail;
    private DatabaseReference rootRef;
    // Loading Animation
    private RotateLoading rotateLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_examiner);
        rotateLoading = findViewById(R.id.mainLoading);

        rotateLoading.start();

        mAuth = FirebaseAuth.getInstance();
        changeStatusBarColor();

        imageView = findViewById(R.id.user_photo);
        textName = findViewById(R.id.user_name);
        textEmail = findViewById(R.id.user_email);

        CardView createQuiz = findViewById(R.id.createQuiz);
        CardView studentList = findViewById(R.id.studentList);
        CardView publishResult = findViewById(R.id.quizList);
        CardView Notification = findViewById(R.id.notification);
        CardView Setting = findViewById(R.id.setting);
        CardView About = findViewById(R.id.about);

        createQuiz.setOnClickListener(this);
        studentList.setOnClickListener(this);
        publishResult.setOnClickListener(this);
        Notification.setOnClickListener(this);
        Setting.setOnClickListener(this);
        About.setOnClickListener(this);

        rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        rootRef.keepSynced(true);



        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String userProfile = String.valueOf(dataSnapshot.child("profilePic").getValue());
                    Picasso.get().load(userProfile).placeholder(R.drawable.ic_profile).into(imageView);
                    textName.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                    textEmail.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                    // Checking Service Status and Message
                    new CheckNoticeAndVersion(ExaminerMainActivity.this);
                } catch (Exception e) {
                    Log.e(TAG, "Profile pic fetch error");
                    Picasso.get().load(R.drawable.ic_profile).into(imageView);
                    textName.setText(getString(R.string.user_name));
                    textEmail.setText(getString(R.string.email));
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
            case R.id.createQuiz:
                startActivity(new Intent(ExaminerMainActivity.this, ExaminerQuizCreateActivity.class));
                break;
            case R.id.studentList:
                startActivity(new Intent(ExaminerMainActivity.this, ExaminerCandidateListsActivity.class));
                break;
            case R.id.quizList:
                startActivity(new Intent(ExaminerMainActivity.this, ExaminerQuizListActivity.class));
                break;
            case R.id.notification:
                startActivity(new Intent(ExaminerMainActivity.this, NotificationActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(ExaminerMainActivity.this, SettingsActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(ExaminerMainActivity.this, AboutActivity.class));
                break;
            default:
                Log.e(TAG, "Invalid Selection");
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

    @Override
    protected void onResume() {
        super.onResume();
        // Checking Service Status and Message
        new CheckNoticeAndVersion(ExaminerMainActivity.this);
    }
}


