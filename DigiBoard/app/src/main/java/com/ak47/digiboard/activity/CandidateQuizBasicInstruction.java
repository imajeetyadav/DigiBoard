package com.ak47.digiboard.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.QuestionListModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import in.shadowfax.proswipebutton.ProSwipeButton;

//  todo: check in picture in picture mode,active calling, internet connection
public class CandidateQuizBasicInstruction extends AppCompatActivity {
    private static final String TAG = "BasicInstruction";
    private String examinerId, quizId, duration;
    private ProSwipeButton startQuizButton;
    private List<QuestionListModel> questionList;
    private DatabaseReference questionRef, examinerRef;
    private ActivityManager activityManager;
    private TextView instruction4, instruction5, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_quiz_basic_instruction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.instructions_title);
        startQuizButton = findViewById(R.id.startQuiz);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        instruction4 = findViewById(R.id.instruction_4);
        instruction5 = findViewById(R.id.instruction_5);
        name = findViewById(R.id.examinerName);
        email = findViewById(R.id.examinerEmail);


        Intent intent = getIntent();
        if (intent != null) {
            examinerId = intent.getStringExtra("examinerId");
            quizId = intent.getStringExtra("quizId");
        } else {
            finish();
        }
        // get Questions
        getQuestion();
        // get Quiz Duration
        getDuration();
        // get Examiner Details
        getExaminerDetails();
        startQuizButton.setOnSwipeListener(() -> {
            new Handler().postDelayed(() -> sendToQuizActivity(), 2000);
        });

    }

    private void getExaminerDetails() {
        examinerRef = FirebaseDatabase.getInstance().getReference();

        Query query = examinerRef.child("AdminUsers")
                .child(examinerId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText((CharSequence) dataSnapshot.child("name").getValue());
                email.setText((CharSequence) dataSnapshot.child("email").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled : Error :" + databaseError.getMessage());
            }
        });

    }

    private void getDuration() {
        questionRef = FirebaseDatabase.getInstance().getReference();
        Query query = questionRef
                .child("AdminUsers")
                .child(examinerId)
                .child("MyQuizLists")
                .child(quizId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                instruction5.setText(MessageFormat.format("5. Quiz duration is {0} minutes.", dataSnapshot.child("duration").getValue().toString()));
                duration = dataSnapshot.child("duration").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled() : Error :" + databaseError.getMessage());
            }
        });
    }

    private void getQuestion() {
        questionList = new ArrayList<>();
        questionRef = FirebaseDatabase.getInstance().getReference();
        Query query = questionRef
                .child("AdminUsers")
                .child(examinerId)
                .child("MyQuizLists")
                .child(quizId)
                .child("questionList");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                    questionList.add(new QuestionListModel(
                            String.valueOf(dataSnapshot.child(String.valueOf(i)).child("question").getValue()),
                            String.valueOf(dataSnapshot.child(String.valueOf(i)).child("optionA").getValue()),
                            String.valueOf(dataSnapshot.child(String.valueOf(i)).child("optionB").getValue()),
                            String.valueOf(dataSnapshot.child(String.valueOf(i)).child("optionC").getValue()),
                            String.valueOf(dataSnapshot.child(String.valueOf(i)).child("optionD").getValue()),
                            Integer.parseInt(dataSnapshot.child(String.valueOf(i)).child("answer").getValue().toString())
                    ));
                }

                instruction4.setText(MessageFormat.format("4. This Quiz contains {0} questions.", questionList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startQuizButton.showResultIcon(false);
                Log.e(TAG, "onCancelled : Error :" + databaseError.getMessage());
            }
        });
    }

    private void sendToQuizActivity() {
        startQuizButton.showResultIcon(true);
        Intent quizIntent = new Intent(CandidateQuizBasicInstruction.this, CandidateActiveQuizActivity.class);
        quizIntent.putParcelableArrayListExtra("questionList", (ArrayList<? extends Parcelable>) questionList);
        quizIntent.putExtra("examinerId", examinerId);
        quizIntent.putExtra("quizId", quizId);
        quizIntent.putExtra("duration", duration);
        quizIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(quizIntent);
        finish();
    }
}