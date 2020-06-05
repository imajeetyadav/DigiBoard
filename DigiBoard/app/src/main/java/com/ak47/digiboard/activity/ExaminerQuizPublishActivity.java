package com.ak47.digiboard.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.ak47.digiboard.common.FCM;
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
import java.util.Locale;

import in.shadowfax.proswipebutton.ProSwipeButton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    #Done
    show Quiz Information , Publish button and Send Notification
 */
public class ExaminerQuizPublishActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Quiz Info Activity";
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    private String quizName, quizDescription;
    private Button selectListButton;
    private String key;
    private int creditCount;
    private DatabaseReference quizRef, quizCandidateRef, creditRef;
    private TextView quizNameTextView, quizDescriptionTextView, noOfQuestion;
    private EditText startTime, endTime, quizDate, duration;
    private ProSwipeButton publishSwipeButton;
    private String candidateListName = "";
    private String userId;
    private String mServerKey;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_quiz_publish);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.quiz_info);
        changeSetting();


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        quizNameTextView = findViewById(R.id.quizName);
        quizDescriptionTextView = findViewById(R.id.quizDescription);
        noOfQuestion = findViewById(R.id.quizTotalQuestion);
        startTime = findViewById(R.id.startTimeId);
        endTime = findViewById(R.id.endTimeId);
        quizDate = findViewById(R.id.quizDateId);
        duration = findViewById(R.id.durationId);
        publishSwipeButton = findViewById(R.id.publishButton);
        selectListButton = findViewById(R.id.selectCandidateList);

        // credit reference
        creditRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("credit");

        // getting server key  from  server
        getServerKey();

        publishSwipeButton.setOnSwipeListener(() -> {
            // user has swiped the btn. Perform your async operation now
            new Handler().postDelayed(() -> {
                // task success! show TICK icon in ProSwipeButton
                publishQuiz(key);
            }, 2000);
        });

        //disable virtual keyboard for input
        startTime.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);
        quizDate.setInputType(InputType.TYPE_NULL);


        //set clickListener
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        quizDate.setOnClickListener(this);
        selectListButton.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            quizName = intent.getStringExtra("QuizName");
            quizNameTextView.setText("Quiz Name : " + quizName);
            quizDescription = intent.getStringExtra("quizDescription");
            quizDescriptionTextView.setText("Quiz Description : " + quizDescription);
        } else {
            finish();
        }

        // To Get Quiz key
        quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("quizName").getValue().equals(quizName)) {
                        key = ds.getKey();
                        noOfQuestion.setText("Questions : " + (int) ds.child("questionList").getChildrenCount());
                        selectListButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error : " + databaseError.getMessage());
                new AlertDialog.Builder(ExaminerQuizPublishActivity.this, R.style.AlertDialogStyle)
                        .setMessage("Error Occur")
                        .show();
            }
        });

        // To Get credit Count
        creditRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Get Credit Value");
                creditCount = Integer.parseInt(dataSnapshot.getValue().toString());
                if (creditCount <= 0) {
                    showCreditAlert();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void publishQuiz(final String key) {

        String startTime = this.startTime.getText().toString();
        String endTime = this.endTime.getText().toString();
        String quizDate = this.quizDate.getText().toString();
        String duration = this.duration.getText().toString();
        String candidateListName = this.selectListButton.getText().toString();
        boolean check = validation(startTime, endTime, quizDate, duration, candidateListName);

        if (check) {
            quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists").child(key);
            quizCandidateRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists").child(candidateListName);
            quizCandidateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (creditCount < dataSnapshot.getChildrenCount()) {
                        publishSwipeButton.showResultIcon(false); // false if task failed
                        showCreditAlert();
                    } else {
                        String publishTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());
                        for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                            quizRef.child("quizCandidate").push().child("email").setValue(dataSnapshot.child(String.valueOf(i)).child("email").getValue());
                            // quiz adding in candidate data and  send Push Notification
                            addQuizToCandidate(key, dataSnapshot.child(String.valueOf(i)).child("email").getValue().toString(), startTime, endTime, quizDate, publishTime);
                        }
                        creditRef.setValue(creditCount - dataSnapshot.getChildrenCount());
                        quizRef.child("startTime").setValue(startTime);
                        quizRef.child("endTime").setValue(endTime);
                        quizRef.child("quizDate").setValue(quizDate);
                        quizRef.child("duration").setValue(duration);
                        quizRef.child("publishedTime").setValue(publishTime);
                        quizRef.child("publishInfo").setValue(true);
                        publishSwipeButton.showResultIcon(true); // true if task success
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error " + databaseError.getMessage());
                }
            });
        } else {
            publishSwipeButton.showResultIcon(false); // false if task failed
        }
    }

    private void showCreditAlert() {
        new AlertDialog.Builder(ExaminerQuizPublishActivity.this, R.style.AlertDialogStyle)
                .setTitle("You have insufficient credit to Publish Quiz.")
                .setMessage("Goto Setting to Get some credit")
                .setCancelable(false)
                .setNeutralButton("Got It", (dialog, whichButton) -> finish()).show();
    }

    private void addQuizToCandidate(final String key, final String candidateEmail, String startTime, String endTime, String quizDate, String notificationTime) {

        final DatabaseReference candidateRef = FirebaseDatabase.getInstance().getReference().child("users");
        candidateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("email").getValue().equals(candidateEmail)) {
                        Log.d(TAG, "candidateUid " + ds.getKey());
                        String quizId = candidateRef.child(ds.getKey()).child("MyQuizLists").push().getKey();
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("quizId").setValue(key);
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("examiner").setValue(userId);
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("quizName").setValue(quizName);
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("quizDescription").setValue(quizDescription);
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("isAttempted").setValue(false);
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("quizDate").setValue(quizDate);
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("endTime").setValue(endTime);
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).child("startTime").setValue(startTime);

                        // Add Notification in Candidate
                        candidateRef.child(ds.getKey()).child("Notifications").child(key).child("title").setValue(quizName);
                        candidateRef.child(ds.getKey()).child("Notifications").child(key).child("message").setValue(quizDescription);
                        candidateRef.child(ds.getKey()).child("Notifications").child(key).child("notificationTime").setValue(notificationTime);
                        candidateRef.child(ds.getKey()).child("Notifications").child(key).child("type").setValue("new Quiz");

                        sendMessageToCandidate(quizName, quizDescription, ds.child("token").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error " + databaseError.getMessage());
            }
        });
    }

    private void changeSetting() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTimeId:
                showtimeDialog(1);
                break;

            case R.id.endTimeId:
                showtimeDialog(2);
                break;

            case R.id.quizDateId:
                showDateDialog();
                break;
            case R.id.selectCandidateList:
                selectCandidateList();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityResult Called");
        if (requestCode == 2) {
            // add Candidate To  ArrayList
            if (data != null) {
                candidateListName = data.getStringExtra("listName");
                if (!candidateListName.isEmpty()) {
                    selectListButton.setText(candidateListName);
//                    selectListButton.setClickable(false);
                }
            }
        }
    }

    private boolean validation(String startTime, String endTime, String quizDate, String duration, String candidateListName) {
        if (TextUtils.isEmpty(startTime)) {
            this.startTime.setError("Enter Start Time");
            return false;
        } else if (TextUtils.isEmpty(endTime)) {
            this.endTime.setError("Enter End Time");
            return false;
        } else if (TextUtils.isEmpty(quizDate)) {
            this.quizDate.setError("Enter Quiz Date");
            return false;
        } else if (TextUtils.isEmpty(duration)) {
            this.duration.setError("Enter Quiz Duration");
            return false;
        } else if (duration.length() > 3) {
            this.duration.setError("Allowed Duration between 9 to 99 minute");
            return false;
        } else if (!validateTimeAndDuration(startTime, endTime, duration)) {
            showTimeAndDurationAlertDialog();
            return false;
        } else if (candidateListName.equals("Select Candidate List")) {
            new AlertDialog.Builder(ExaminerQuizPublishActivity.this, R.style.AlertDialogStyle)
                    .setMessage("Please Select Candidate List")
                    .show();
            return false;
        }
        return true;
    }

    private void selectCandidateList() {
        Intent intent = new Intent(ExaminerQuizPublishActivity.this, ExaminerSelectCandidateListActivity.class);
        startActivityForResult(intent, 2);
    }

    private void showDateDialog() {
        SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            quizDate.setText(dateFormatter.format(newDate.getTime()));
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showtimeDialog(final int k) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(ExaminerQuizPublishActivity.this, android.R.style.Theme_Holo_Light_Dialog,
                (view, hourOfDay, minute1) -> {
                    if (hourOfDay < 10 && minute1 < 10) {
                        if (k == 1) {
                            startTime.setText(MessageFormat.format("0{0}:0{1}", hourOfDay, minute1));
                        } else if (k == 2) {
                            endTime.setText(MessageFormat.format("0{0}:0{1}", hourOfDay, minute1));
                        }
                    } else if (hourOfDay < 10) {
                        if (k == 1) {
                            startTime.setText(MessageFormat.format("0{0}:{1}", hourOfDay, minute1));
                        } else if (k == 2) {
                            endTime.setText(MessageFormat.format("0{0}:{1}", hourOfDay, minute1));
                        }
                    } else if (minute1 < 10) {
                        if (k == 1) {
                            startTime.setText(MessageFormat.format("{0}:0{1}", hourOfDay, minute1));
                        } else if (k == 2) {
                            endTime.setText(MessageFormat.format("{0}:0{1}", hourOfDay, minute1));
                        }
                    } else {
                        if (k == 1) {
                            startTime.setText(MessageFormat.format("{0}:{1}", hourOfDay, minute1));
                        } else if (k == 2) {
                            endTime.setText(MessageFormat.format("{0}:{1}", hourOfDay, minute1));
                        }
                    }

                }, hour, minute, true).show();
    }

    private boolean validateTimeAndDuration(String startTime, String endTime, String duration) {
        int start = Integer.parseInt(startTime.replace(":", ""));
        int end = Integer.parseInt(endTime.replace(":", ""));
        if (start == end) {
            return false;
        } else return Integer.parseInt(duration) < (end - start);
    }

    private void showTimeAndDurationAlertDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.showTimeAndDurationDialog)
                .show();
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

    private void sendMessageToCandidate(String title, String message, String token) {
        Log.d(TAG, "sendMessageToCandidate: sending message to selected Candidate");
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
        Log.d(TAG, "sendMessageToCandidate: sending to token: " + token);
        Data data = new Data();
        data.setTitle(title);
        data.setMessage(message);
        data.setData_type(getString(R.string.data_type_quiz_message));
        FirebaseCloudMessage firebaseCloudMessage = new FirebaseCloudMessage();
        firebaseCloudMessage.setData(data);
        firebaseCloudMessage.setTo(token);

        Call<ResponseBody> call = fcmAPI.send(headers, firebaseCloudMessage);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable throwable) {
                Log.e(TAG, "onFailure: Unable to send the message." + throwable.getMessage());
                Toast.makeText(ExaminerQuizPublishActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
