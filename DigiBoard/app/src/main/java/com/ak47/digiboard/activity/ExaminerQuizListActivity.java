package com.ak47.digiboard.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;

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

        changeStatusBarSetting();


    }

    private void changeStatusBarSetting() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}
