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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

public class TeacherMainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "TeacherMainActivity";
    private FirebaseAuth mAuth;
    private ImageView imageView;
    private TextView textName, textEmail;
    // Loading Animation
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        rotateLoading = findViewById(R.id.mainLoading);

        rotateLoading.start();

        mAuth = FirebaseAuth.getInstance();
        changeStatusBarColor();

        imageView = findViewById(R.id.user_photo);
        textName = findViewById(R.id.user_name);
        textEmail = findViewById(R.id.user_email);

        CardView createQuiz = findViewById(R.id.createQuiz);
        CardView studentList = findViewById(R.id.studentList);
        CardView publishResult = findViewById(R.id.publishResult);
        CardView Notification = findViewById(R.id.notification);
        CardView Setting = findViewById(R.id.setting);
        CardView About = findViewById(R.id.about);

        createQuiz.setOnClickListener(this);
        studentList.setOnClickListener(this);
        publishResult.setOnClickListener(this);
        Notification.setOnClickListener(this);
        Setting.setOnClickListener(this);
        About.setOnClickListener(this);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        rootRef.keepSynced(true);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String userProfile = Objects.requireNonNull(dataSnapshot.child("profilePic").getValue()).toString();
                    Picasso.get().load(userProfile).placeholder(R.drawable.profle_pic).into(imageView);
                } catch (Exception e) {
                    Log.e(TAG, "Profile pic fetch error");
                    Picasso.get().load(R.drawable.profle_pic).into(imageView);
                }
                textName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                textEmail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
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
//                startActivity(new Intent(TeacherMainActivity.this, ActiveQuizActivity.class));
                break;
            case R.id.studentList:
//                startActivity(new Intent(TeacherMainActivity.this, HistoryActivity.class));
                break;
            case R.id.publishResult:
//                startActivity(new Intent(TeacherMainActivity.this, ResultActivity.class));
                break;
            case R.id.notification:
                startActivity(new Intent(TeacherMainActivity.this, NotificationActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(TeacherMainActivity.this, SettingsActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(TeacherMainActivity.this, AboutActivity.class));
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
}


