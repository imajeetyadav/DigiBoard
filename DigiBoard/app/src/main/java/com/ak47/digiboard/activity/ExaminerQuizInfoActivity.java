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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.shadowfax.proswipebutton.ProSwipeButton;

/*
    #done
    show Quiz Information and  Publish button
 */
public class ExaminerQuizInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Quiz Info Activity";
    private String quizName, quizDescription;
    private Button selectListOrShowResult;
    private String key;
    private Boolean publishInfo;
    private DatabaseReference quizRef, quizCandidateRef;
    private TextView quizNameTextView, quizDescriptionTextView, noOfQuestion;
    private EditText startTime, endTime, quizDate, duration;
    private TimePickerDialog picker;
    private String AM_PM = "";
    private int hour, minutes;
    private ProSwipeButton publishSwipeButton;
    private String candidateListName = "";
    private String userId;

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

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        quizNameTextView = findViewById(R.id.quizName);
        quizDescriptionTextView = findViewById(R.id.quizDescription);
        noOfQuestion = findViewById(R.id.quizTotalQuestion);
        startTime = findViewById(R.id.startTimeId);
        endTime = findViewById(R.id.endTimeId);
        quizDate = findViewById(R.id.quizDateId);
        duration = findViewById(R.id.durationId);
        publishSwipeButton = findViewById(R.id.publishButton);
        selectListOrShowResult = findViewById(R.id.selectCandidateList);


        publishSwipeButton.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // task success! show TICK icon in ProSwipeButton
                        publishQuiz(key);
                    }
                }, 2000);
            }
        });

        //disable virtual keyboard for input
        startTime.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);
        quizDate.setInputType(InputType.TYPE_NULL);


        //set clickListener
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        quizDate.setOnClickListener(this);
        selectListOrShowResult.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            quizName = intent.getStringExtra("QuizName");
            quizNameTextView.setText("Quiz Name : " + quizName);
            quizDescription = intent.getStringExtra("quizDescription");
            quizDescriptionTextView.setText("Quiz Description : " + quizDescription);
        } else {
            finish();
        }

        quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("quizName").getValue().equals(quizName)) {
                        key = ds.getKey();
                        noOfQuestion.setText("Questions : " + (int) ds.child("questionList").getChildrenCount());
                        selectListOrShowResult.setVisibility(View.VISIBLE);
                        publishInfo = (Boolean) ds.child("publishInfo").getValue();
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

    private void publishQuiz(final String key) {

        String startTime = this.startTime.getText().toString();
        String endTime = this.endTime.getText().toString();
        String quizDate = this.quizDate.getText().toString();
        String duration = this.duration.getText().toString();
        String candidateListName = this.selectListOrShowResult.getText().toString();
        boolean check = validation(startTime, endTime, quizDate, duration, candidateListName);

        if (check) {
            quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists").child(key);
            quizCandidateRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists").child(candidateListName);
            quizRef.child("startTime").setValue(startTime);
            quizRef.child("endTime").setValue(endTime);
            quizRef.child("quizDate").setValue(quizDate);
            quizRef.child("duration").setValue(duration);
            quizRef.child("publishedTime").setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            quizRef.child("publishInfo").setValue(true);
            quizCandidateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                        quizRef.child("quizCandidate").child(String.valueOf(i)).setValue(dataSnapshot.child(String.valueOf(i)).child("email").getValue());
                        // quiz adding in candidate data and  send Push Notification
                        addQuizToCandidate(key, dataSnapshot.child(String.valueOf(i)).child("email").getValue().toString());
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

    private void addQuizToCandidate(final String key, final String candidateEmail) {
        String token = "";

        final DatabaseReference candidateRef = FirebaseDatabase.getInstance().getReference().child("users");
        candidateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("email").getValue().equals(candidateEmail)) {
                        Log.e(TAG, "candidateUid " + ds.getKey());
                        candidateRef.child(ds.getKey()).child("MyQuizLists").child(key).setValue(userId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error " + databaseError.getMessage());
            }
        });
        publishSwipeButton.showResultIcon(true); // true if task success
        finish();
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
                    selectListOrShowResult.setText(candidateListName);
                    selectListOrShowResult.setClickable(false);
                }
            }
        }
    }

    private boolean validation(String startTime, String endTime, String quizDate, String duration, String candidateListName) {
        // todo: proper validation needed
        boolean check = true;
        if (TextUtils.isEmpty(startTime)) {
            this.startTime.setError("Enter Start Time");
            check = false;
        } else if (TextUtils.isEmpty(endTime)) {
            this.endTime.setError("Enter End Time");
            check = false;
        } else if (TextUtils.isEmpty(quizDate)) {
            this.quizDate.setError("Enter Quiz Date");
            check = false;
        } else if (TextUtils.isEmpty(duration)) {
            this.duration.setError("Enter Quiz Duration");
            check = false;
        } else if (duration.length() > 3) {
            this.duration.setError("Allowed Duration between 9 to 99 minute");
            check = false;
        } else if (candidateListName.equals("Select Candidate List") || (candidateListName.equals("Result"))) {
            new AlertDialog.Builder(ExaminerQuizInfoActivity.this, R.style.AlertDialogStyle)
                    .setMessage("Please Select Candidate List")
                    .show();
            check = false;
        }

        return check;
    }

    private void selectCandidateList() {
        Intent intent = new Intent(ExaminerQuizInfoActivity.this, ExaminerSelectCandidateListActivity.class);
        startActivityForResult(intent, 2);
    }

    private void showDateDialog() {
        final Calendar calender = Calendar.getInstance();
        int day = calender.get(Calendar.DAY_OF_MONTH);
        int month = calender.get(Calendar.MONTH);
        int year = calender.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(ExaminerQuizInfoActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        quizDate.setText(dayOfMonth + " /" + month + " /" + year);

                    }
                }, year, month, day);
        dialog.show();
    }

    private void showtimeDialog(final int k) {
        final Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minutes = cal.get(Calendar.MINUTE);
        picker = new TimePickerDialog(ExaminerQuizInfoActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);

                        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            hourOfDay = hourOfDay - 12;
                            hour = hour - 12;
                        }
                        if (k == 1) {
                            startTime.setText(hourOfDay + " :" + minute + " " + AM_PM);
                        } else if (k == 2) {
                            endTime.setText(hourOfDay + " :" + minute + " " + AM_PM);
                        }

                    }

                }, hour, minutes, false);
        picker.show();
    }

}
