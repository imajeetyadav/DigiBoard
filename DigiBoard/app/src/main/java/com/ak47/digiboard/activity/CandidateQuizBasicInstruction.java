package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.victor.loading.rotate.RotateLoading;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.shadowfax.proswipebutton.ProSwipeButton;

//  todo: check in picture in picture mode,active calling, internet connection
public class CandidateQuizBasicInstruction extends AppCompatActivity {
    private static final String TAG = "BasicInstruction";
    private String examinerId, quizId, duration;
    private ProSwipeButton startQuizButton;
    private List<QuestionListModel> questionList;
    private DatabaseReference questionRef, examinerRef;
    private TextView instruction4, instruction5, name, email;
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_quiz_basic_instruction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.instructions_title);
        rotateLoading = findViewById(R.id.mainLoading);
        startQuizButton = findViewById(R.id.startQuiz);
        instruction4 = findViewById(R.id.instruction_4);
        instruction5 = findViewById(R.id.instruction_5);
        name = findViewById(R.id.examinerName);
        email = findViewById(R.id.examinerEmail);


        rotateLoading.start();
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

        startQuizButton.setOnSwipeListener(() -> new Handler().postDelayed(this::sendToQuizActivity, 2000));

    }

    private void getExaminerDetails() {
        examinerRef = FirebaseDatabase.getInstance().getReference();

        Query query = examinerRef.child("AdminUsers")
                .child(examinerId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                email.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                rotateLoading.stop();
                startQuizButton.setVisibility(View.VISIBLE);
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
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    instruction5.setText(MessageFormat.format("5. Quiz duration is {0} minutes.", Objects.requireNonNull(dataSnapshot.child("duration").getValue()).toString()));
                    duration = Objects.requireNonNull(dataSnapshot.child("duration").getValue()).toString();
                } catch (NullPointerException e) {
                    Log.e(TAG, "getDuration Error : " + e.getMessage());
                    warningDialog();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, " onCancelled() : Error :" + databaseError.getMessage());
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
                try {
                    for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                        questionList.add(new QuestionListModel(
                                Objects.requireNonNull(dataSnapshot.child(String.valueOf(i)).child("question").getValue()).toString(),
                                Objects.requireNonNull(dataSnapshot.child(String.valueOf(i)).child("optionA").getValue()).toString(),
                                Objects.requireNonNull(dataSnapshot.child(String.valueOf(i)).child("optionB").getValue()).toString(),
                                Objects.requireNonNull(dataSnapshot.child(String.valueOf(i)).child("optionC").getValue()).toString(),
                                Objects.requireNonNull(dataSnapshot.child(String.valueOf(i)).child("optionD").getValue()).toString(),
                                Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(String.valueOf(i)).child("answer").getValue()).toString())
                        ));
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, " getQuestion() Error : " + e.getMessage());
                    warningDialog();
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

    private void warningDialog() {
        new AlertDialog.Builder(CandidateQuizBasicInstruction.this, R.style.AlertDialogStyle)
                .setMessage("Unable To Connect\nTry again Later")
                .setPositiveButton("Ok", (dialog, which) -> finish())
                .show();
    }
}