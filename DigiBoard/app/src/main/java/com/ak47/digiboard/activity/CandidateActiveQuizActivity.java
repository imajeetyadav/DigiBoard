package com.ak47.digiboard.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ak47.digiboard.R;
import com.ak47.digiboard.common.FCM;
import com.ak47.digiboard.model.QuestionListModel;
import com.ak47.digiboard.model.fcm.Data;
import com.ak47.digiboard.model.fcm.FirebaseCloudMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    /*
          - Active Quiz
    */

public class CandidateActiveQuizActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ActiveQuiz";
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    private int cheatingCount = 0; //pending
    private Button endQuizButton, skipQuestionButton;
    private ActivityManager activityManager;
    private NotificationManager mNotificationManager;
    private List<QuestionListModel> questionList;
    private String examinerId, quizId, duration;
    private DatabaseReference adminRef, userRef;
    private String notificationChannelIdQuizAlert = "1000";
    private TextView question;
    private Button optionA, optionB, optionC, optionD, questionNumberButton;
    private Button quizDurationButton;
    private int result = 0;
    private int currentQuestion = 0;
    private boolean isSubmitted = false;
    private String mServerKey;
    private String examinerToken, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_active_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.quiz_mode);

        basicSetup();
        Intent intent = getIntent();
        if (intent != null) {
            questionList = intent.getParcelableArrayListExtra("questionList");
            examinerId = intent.getStringExtra("examinerId");
            quizId = intent.getStringExtra("quizId");
            duration = intent.getStringExtra("duration");
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            adminRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(examinerId);
            startLock();
            getServerKey();
            startQuizDialog(quizId, userRef);
        } else {
            LoadingQuizError();
        }
    }

    private void startQuizDialog(String quizId, DatabaseReference userRef) {
        new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Alert")
                .setMessage("Once You click on start button timer will start automatically.")
                .setCancelable(false)
                .setPositiveButton("Start",
                        (dialog, id) -> {
                            //set Quiz Attempt true and attempt time
                            userRef.child("MyQuizLists").child(quizId).child("isAttempted").setValue(true);
                            userRef.child("MyQuizLists").child(quizId).child("quizStartTime").setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime()));
                            getExaminerTokenAndNameOfCandidate();
                            loadFirstQuestion();
                            startTimer();
                        }).show();
    }

    private void startTimer() {
        new CountDownTimer(Integer.parseInt(duration) * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long min = (millisUntilFinished / 1000) / 60;
                long sec = (millisUntilFinished / 1000) % 60;
                quizDurationButton.setText(String.format("Left : %02d:%02d min", min, sec));
            }

            @Override
            public void onFinish() {
                Toast.makeText(CandidateActiveQuizActivity.this, "Done!", Toast.LENGTH_LONG).show();
                quizDurationButton.setText("Time Up");
                normalSubmitQuiz();
            }
        }.start();
    }

    private void loadFirstQuestion() {
        questionNumberButton.setVisibility(View.VISIBLE);
        quizDurationButton.setVisibility(View.VISIBLE);

        endQuizButton.setVisibility(View.VISIBLE);
        skipQuestionButton.setVisibility(View.VISIBLE);
        optionA.setVisibility(View.VISIBLE);
        optionB.setVisibility(View.VISIBLE);
        optionC.setVisibility(View.VISIBLE);
        optionD.setVisibility(View.VISIBLE);
        endQuizButton.setVisibility(View.VISIBLE);
        skipQuestionButton.setVisibility(View.VISIBLE);

        questionNumberButton.setText(MessageFormat.format("{0}/{1}", currentQuestion + 1, questionList.size()));
        question.setText(questionList.get(currentQuestion).getQuestion());
        optionA.setText(questionList.get(currentQuestion).getOptionA());
        optionB.setText(questionList.get(currentQuestion).getOptionB());
        optionC.setText(questionList.get(currentQuestion).getOptionC());
        optionD.setText(questionList.get(currentQuestion).getOptionD());
    }

    private void checkAnswer(int i) {
        if (questionList.get(currentQuestion).getAnswer() == i) {
            result++;
        }

        currentQuestion++;
        if (currentQuestion < questionList.size()) {
            questionNumberButton.setText(MessageFormat.format("{0}/{1}", currentQuestion + 1, questionList.size()));
            question.setText(questionList.get(currentQuestion).getQuestion());
            optionA.setText(questionList.get(currentQuestion).getOptionA());
            optionB.setText(questionList.get(currentQuestion).getOptionB());
            optionC.setText(questionList.get(currentQuestion).getOptionC());
            optionD.setText(questionList.get(currentQuestion).getOptionD());
        } else {
            questionNumberButton.setVisibility(View.INVISIBLE);
            quizDurationButton.setVisibility(View.INVISIBLE);
            question.setText(R.string.thank_you);
            skipQuestionButton.setVisibility(View.INVISIBLE);
            optionA.setVisibility(View.INVISIBLE);
            optionB.setVisibility(View.INVISIBLE);
            optionC.setVisibility(View.INVISIBLE);
            optionD.setVisibility(View.INVISIBLE);
            endQuizButton.setVisibility(View.INVISIBLE);
            skipQuestionButton.setVisibility(View.INVISIBLE);
            normalSubmitQuiz();
        }
    }

    // apply full screen mode, keep Screen On and dnd activate
    // all findViewById and click listener
    private void basicSetup() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);

        endQuizButton = findViewById(R.id.submitQuiz);
        skipQuestionButton = findViewById(R.id.skipQuestion);
        question = findViewById(R.id.question);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        questionNumberButton = findViewById(R.id.questionNumber);
        quizDurationButton = findViewById(R.id.quizDuration);

        endQuizButton.setOnClickListener(this);
        skipQuestionButton.setOnClickListener(this);
        optionA.setOnClickListener(this);
        optionB.setOnClickListener(this);
        optionC.setOnClickListener(this);
        optionD.setOnClickListener(this);

    }

    private void getExaminerTokenAndNameOfCandidate() {
        adminRef.child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotAdmin) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {
                        sendMessageToExaminer(String.valueOf(dataSnapshotAdmin.getValue()), String.valueOf(dataSnapshotUser.child("name").getValue()));
                        String newPushValue = adminRef.child("Notifications").push().getKey();
                        adminRef.child("Notifications").child(quizId + dataSnapshotUser.child("name").getValue()).child("type").setValue("New Attempt");
                        adminRef.child("Notifications").child(quizId + dataSnapshotUser.child("name").getValue()).child("title").setValue(dataSnapshotUser.child("name").getValue());
                        adminRef.child("Notifications").child(quizId + dataSnapshotUser.child("name").getValue()).child("message").setValue(dataSnapshotUser.child("email").getValue());
                        adminRef.child("Notifications").child(quizId + dataSnapshotUser.child("name").getValue()).child("notificationTime").setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled() : Error :" + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled() : Error :" + databaseError.getMessage());
            }
        });
    }

    // normal quiz Submit
    private void normalSubmitQuiz() {
        float finalResult = ((float) result / (float) questionList.size()) * 100;
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Quiz Submitted")
                .setMessage("Your score : " + finalResult)
                .setCancelable(false)
                .setPositiveButton("Ok",
                        (dialog, id) -> {
                            userRef.child("MyQuizLists").child(quizId).child("quizEndTime").setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime()));
                            userRef.child("MyQuizLists").child(quizId).child("result").setValue(finalResult + "%");
                            isSubmitted = true;
                            showNotification();
                            sendToMainActivity();
                            showSystemUI();
                        }).show();
    }

    // quiz force Submit
    private void forceSubmitQuiz() {
        new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("You want to submit Quiz?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            sendToMainActivity();
                            showSystemUI();
                        }).setNeutralButton("No", null).show();
    }

    public boolean isAppInLockTaskMode() {
        ActivityManager activityManager;

        activityManager = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getLockTaskModeState()
                != ActivityManager.LOCK_TASK_MODE_NONE;
    }

    //  set Lock
    private void startLock() {
        if (!isAppInLockTaskMode()) {
            startLockTask();
        }
    }

    // show submit notification to candidate
    private void showNotification() {

        Intent mainActivityIntent = new Intent(getApplicationContext(), CandidateMainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                300,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, notificationChannelIdQuizAlert)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mainActivityPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your Quiz Submitted")
                ).setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, notification);
    }

    // show error when intent will null
    private void LoadingQuizError() {
        new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Try Again Later")
                .setMessage("Due to some Reason quiz failed to load")
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> sendToMainActivity()).show();

    }

    private void failedLockWarning() {
        new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Lock failed")
                .setMessage("If lock failed more then certain Limit. It is assumed that you are  doing cheating and quiz will automatically submit")
                .setCancelable(false)
                .setPositiveButton("Got it",
                        (dialog, id) -> {
                            if (!isAppInLockTaskMode()) {
                                startLockTask();
                            }
                        }).show();
    }

    // send fcm
    private void sendMessageToExaminer(String examinerToken, String userName) {
        Log.d(TAG, "sendMessageToCandidate: sending message to Examiner");
        Log.d(TAG, "server key" + mServerKey);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        FCM fcmAPI = retrofit.create(FCM.class);

        //attach the headers
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "key=" + mServerKey);


        //send the message to tokens
        Log.d(TAG, "sendMessageToExaminer: sending to token: " + examinerToken);
        Data data = new Data();
        data.setTitle("Quiz Attempted");
        data.setMessage("by " + userName);
        data.setData_type(getString(R.string.data_type_quiz_attempt));
        FirebaseCloudMessage firebaseCloudMessage = new FirebaseCloudMessage();
        firebaseCloudMessage.setData(data);
        firebaseCloudMessage.setTo(examinerToken);

        Call<ResponseBody> call = fcmAPI.send(headers, firebaseCloudMessage);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable throwable) {
                Log.e(TAG, "onFailure: Unable to send the message." + throwable.getMessage());
                Toast.makeText(CandidateActiveQuizActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void closingWarning() {
        new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("You want to submit Quiz ?")
                .setMessage("If Yes then screen will be Unlock ")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> sendToMainActivity()).setNeutralButton("No", null).show();
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

    public void sendToMainActivity() {
        stopLockTask();
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        Intent mainIntent = new Intent(CandidateActiveQuizActivity.this, CandidateMainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void getServerKey() {
        Log.d(TAG, " getServerKey: retrieving server key");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("AppConfig");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, " onDataChange: got the server key");
                mServerKey = dataSnapshot.child("server_key").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitQuiz:
                normalSubmitQuiz();
                break;
            case R.id.skipQuestion:
                checkAnswer(0);
                break;
            case R.id.optionA:
                checkAnswer(1);
                break;
            case R.id.optionB:
                checkAnswer(2);
                break;
            case R.id.optionC:
                checkAnswer(3);
                break;
            case R.id.optionD:
                checkAnswer(4);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isAppInLockTaskMode()) {
            closingWarning();
        } else {
            startLock();
        }
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isAppInLockTaskMode() && currentQuestion >= questionList.size()) {
            cheatingCount++;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isSubmitted) {
            forceSubmitQuiz();
        }
    }

}
