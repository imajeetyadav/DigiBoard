package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
        #Done
    - Add Quiz Title,description
    then send to add question activity
   */
public class ExaminerQuizCreateActivity extends AppCompatActivity {
    private TextInputEditText quizName, quizDescription, quizEncryptionCode;
    private Button nextButton;
    private String TAG = "Quiz Create Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_quiz_create);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        mTitle.setText(R.string.quiz_details);

        quizName = findViewById(R.id.quizNameId);
        quizDescription = findViewById(R.id.quizDescriptionId);
        quizEncryptionCode = findViewById(R.id.quizEncryptionCode);
        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = quizName.getText().toString().trim();
                String Description = quizDescription.getText().toString().trim();
                String EncryptionCode = quizEncryptionCode.getText().toString().trim();

                if (validation(Name, Description, EncryptionCode)) {
                    // send To QuestionListActivity
                    sendToQuestionListActivity(Name, Description, EncryptionCode);
                }
            }
        });


    }

    private void sendToQuestionListActivity(final String Name, final String Description, final String EncryptionCode) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId);
        quizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChild("MyQuizLists") && ds.child("MyQuizLists").child("quizName").getValue().equals(Name)) {
                        quizName.setError("Already Exist. Please try other name");
                        break;
                    } else {
                        Intent intent = new Intent(ExaminerQuizCreateActivity.this, ExaminerQuestionListActivity.class);
                        intent.putExtra("quizName", Name);
                        intent.putExtra("quizDescription", Description);
                        intent.putExtra("quizEncryptionCode", EncryptionCode);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "checkExistence fail");
                new AlertDialog.Builder(ExaminerQuizCreateActivity.this, R.style.AlertDialogStyle)
                        .setMessage("Error Occur")
                        .show();
            }
        });

    }

    private boolean validation(String Name, String Description, String EncryptionCode) {
        boolean check = true;
        if (TextUtils.isEmpty(Name)) {
            quizName.setError("Enter Quiz Name");
            check = false;
        }
        if (TextUtils.isEmpty(Description)) {
            quizDescription.setError("Enter Quiz Description ");
            check = false;
        }
        if (TextUtils.isEmpty(EncryptionCode)) {
            quizEncryptionCode.setError("Enter Quiz Encryption Code");
            check = false;
        }
        if (EncryptionCode.length() != 4) {
            quizEncryptionCode.setError("4 digit of code required");
            check = false;
        }

        return check;
    }

}
