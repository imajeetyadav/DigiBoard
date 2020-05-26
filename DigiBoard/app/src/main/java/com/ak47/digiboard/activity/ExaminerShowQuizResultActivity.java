package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class ExaminerShowQuizResultActivity extends AppCompatActivity {
    private static final String TAG = "Quiz Result";
    private String quizName;
    private String key;
    private String userId;
    private DatabaseReference quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_show_quiz_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.result);

        Intent intent = getIntent();
        if (intent != null) {
            quizName = intent.getStringExtra("QuizName");
        } else {
            finish();
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("quizName").getValue().equals(quizName)) {
                        key = ds.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error : " + databaseError.getMessage());
                new AlertDialog.Builder(ExaminerShowQuizResultActivity.this, R.style.AlertDialogStyle)
                        .setMessage("Error Occur")
                        .show();
            }
        });
    }


}
