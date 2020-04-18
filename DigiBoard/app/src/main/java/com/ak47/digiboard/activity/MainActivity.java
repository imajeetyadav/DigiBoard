package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ak47.digiboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    ImageView imageView;
    TextView textName, textEmail;
    CardView activeQuiz, quizHistory, quizResult, quizNotification, quizSupport, quizAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        imageView = findViewById(R.id.user_photo);
        textName = findViewById(R.id.user_name);
        textEmail = findViewById(R.id.user_email);

        activeQuiz = findViewById(R.id.activeQuiz);
        quizHistory = findViewById(R.id.history);
        quizResult = findViewById(R.id.result);
        quizNotification = findViewById(R.id.notification);
        quizSupport = findViewById(R.id.support);
        quizAbout = findViewById(R.id.about);


        AddClickListeners();

        assert user != null;
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        rootRef.keepSynced(true);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userProfile = Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("profilePic").getValue()).toString());
                textName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                textEmail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());

                Picasso.get().load(userProfile).placeholder(R.drawable.profle_pic).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AddClickListeners() {
        activeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Quiz Card", Toast.LENGTH_LONG).show();
//                 Active Quiz
                finish();
                startActivity(new Intent(MainActivity.this, QuizActivity.class));
            }
        });

        quizHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "History Card", Toast.LENGTH_LONG).show();
//                 Quiz History
            }
        });


        quizResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Result Card", Toast.LENGTH_LONG).show();
//                Quiz  Result
            }
        });

        quizNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Notification Card", Toast.LENGTH_LONG).show();
//                 Quiz Notifications
            }
        });

        quizSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Support Card", Toast.LENGTH_LONG).show();
//                 Quiz Support
            }
        });


        quizAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "About Card", Toast.LENGTH_LONG).show();
                // About
            }
        });
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
