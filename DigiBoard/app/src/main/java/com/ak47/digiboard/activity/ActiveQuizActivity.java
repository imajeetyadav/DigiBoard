package com.ak47.digiboard.activity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ak47.digiboard.R;

public class ActiveQuizActivity extends AppCompatActivity {
    public static int cheatingCount = 0;
    private Button endQuiz;
    private ActivityManager activityManager;
    private NotificationManager mNotificationManager;
       /*
          - Active Quiz

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_quiz);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);

        endQuiz = findViewById(R.id.endQuiz);

        if (startLock()) {
            quizStartWarning();
        }

        endQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalEndQuiz();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!activityManager.isInLockTaskMode()) {
            if (cheatingCount > 2) {
                forceFullyEndQuiz();
            }
            warningMessage();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!activityManager.isInLockTaskMode()) {
            if (cheatingCount > 2) {
                forceFullyEndQuiz();
            }
            warningMessage();
        }
    }

    private boolean startLock() {

        if (!activityManager.isInLockTaskMode()) {
            startLockTask();
        }
        return !activityManager.isInLockTaskMode();
    }

    private void normalEndQuiz() {

        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        startActivity(new Intent(ActiveQuizActivity.this, CandidateMainActivity.class));
        stopLockTask();
    }

    private void quizStartWarning() {
        //Display Dialog
    }

    private void warningMessage() {

        //Display Dialog
        cheatingCount++;
    }

    private void forceFullyEndQuiz() {

        // Auto Submit Quiz Method
        cheatingCount = 0;
    }
}
