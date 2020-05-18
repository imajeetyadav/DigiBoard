package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/*
    #done
     List of Quiz and add new Quiz button
 */


public class ExaminerQuizListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_quiz_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        mTitle.setText(R.string.quiz_list);

        changeStatusBarColor();
        FloatingActionButton addNewQuizButton = findViewById(R.id.addNewQuizButton);
        addNewQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addQuiz = new Intent(ExaminerQuizListActivity.this, ExaminerQuizCreateActivity.class);
                startActivity(addQuiz);
            }
        });


    }

    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}
