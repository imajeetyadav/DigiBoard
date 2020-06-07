package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.ak47.digiboard.adapter.ExaminerResultAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.victor.loading.rotate.RotateLoading;

import java.util.HashMap;

public class ExaminerShowQuizResultActivity extends AppCompatActivity {
    private static final String TAG = "Quiz Result";
    private String quizName;
    private String key;
    private DatabaseReference quizRef, candidateRef;
    private HashMap<String, String> resultMap;
    private ListView resultListView;
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_show_quiz_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.result);

        resultListView = findViewById(R.id.resultListViewId);
        resultMap = new HashMap<>();

        rotateLoading = findViewById(R.id.mainLoading);
        rotateLoading.start();

        Intent intent = getIntent();
        if (intent != null) {
            quizName = intent.getStringExtra("QuizName");
        } else {
            finish();
        }
        candidateRef = FirebaseDatabase.getInstance().getReference("users");
        quizRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("MyQuizLists");
        getQuizKey();

    }

    private void getQuizKey() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("quizName").getValue().equals(quizName)) {
                        key = ds.getKey();
                        getResult();
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

    private void getResult() {
        Log.e(TAG, "key " + key);
        Query query = quizRef.child(key)
                .child("quizCandidate");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Query queryEmail = candidateRef.orderByChild("email").equalTo(String.valueOf(snapshot.child("email").getValue()));
                    queryEmail.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotCandidate) {
                            for (DataSnapshot data : dataSnapshotCandidate.getChildren()) {
                                if (data.child("MyQuizLists").child(key).hasChild("result")) {
                                    resultMap.put(String.valueOf(snapshot.child("email").getValue()), data.child("MyQuizLists").child(key).child("result").getValue() + " %");
                                } else if (data.child("MyQuizLists").child(key).hasChild("startQuizTime")) {
                                    resultMap.put(String.valueOf(snapshot.child("email").getValue()), "Not Submitted Properly");
                                } else {
                                    resultMap.put(String.valueOf(snapshot.child("email").getValue()), "Not Attempted");
                                }
                            }
                            if (resultMap.size() > 0) {
                                showResult(resultMap);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Error : " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error : " + databaseError.getMessage());
            }
        });
    }

    public void showResult(HashMap<String, String> resultMap) {
        ExaminerResultAdapter adapter = new ExaminerResultAdapter(resultMap);
        resultListView.setAdapter(adapter);
        rotateLoading.stop();
    }
}
