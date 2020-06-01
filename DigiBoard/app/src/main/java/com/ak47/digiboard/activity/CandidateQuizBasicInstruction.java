package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class CandidateQuizBasicInstruction extends AppCompatActivity {
    private String examinerId, quizId;
    private ProSwipeButton startQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_quiz_basic_instruction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.instructions_title);
        startQuizButton = findViewById(R.id.startQuiz);

        Intent intent = getIntent();
        if (intent != null) {
            examinerId = intent.getStringExtra("examinerId");
            quizId = intent.getStringExtra("quizId");
        } else {
            finish();
        }

        startQuizButton.setOnSwipeListener(() -> {
            // user has swiped the btn. Perform your async operation now
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // task success! show TICK icon in ProSwipeButton
                    sendToQuizActivity(examinerId, quizId);
                }
            }, 2000);
        });

    }

    private void sendToQuizActivity(String examinerId, String quizId) {

    }
}