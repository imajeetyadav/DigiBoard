package com.ak47.digiboard.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
    show Quiz Information and  Publish button
 */
public class ExaminerQuizInfoActivity extends AppCompatActivity {
    private static final String TAG = "Quiz Info Activity";
    private String quizName;
    private String key;
    private DatabaseReference quizRef;
    private TextView quizNameTextView, quizDescriptionTextView, noOfQuestion;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_quiz_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.quiz_info);
        changeSetting();
        quizNameTextView = findViewById(R.id.quizName);
        quizDescriptionTextView = findViewById(R.id.quizDescription);
        noOfQuestion = findViewById(R.id.quizTotalQuestion);

        Intent intent = getIntent();
        if (intent != null) {
            quizName = intent.getStringExtra("QuizName");
            quizNameTextView.setText("Quiz Name : " + quizName);
            quizDescriptionTextView.setText("Quiz Description : " + intent.getStringExtra("quizDescription"));
        } else {
            finish();
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("quizName").getValue().equals(quizName)) {
                        key = ds.getKey();
                        noOfQuestion.setText("Questions : " + (int) ds.child("questionList").getChildrenCount());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error : " + databaseError.getMessage());
                new AlertDialog.Builder(ExaminerQuizInfoActivity.this, R.style.AlertDialogStyle)
                        .setMessage("Error Occur")
                        .show();
            }
        });
    }

    private void changeSetting() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

}
