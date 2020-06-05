package com.ak47.digiboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.adapter.ExaminerQuestionListAdapter;
import com.ak47.digiboard.model.QuestionListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.victor.loading.rotate.RotateLoading;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/*
    #done
    -Question List
    -Add Question
 */
public class ExaminerQuestionListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QuestionListActivity";
    private RecyclerView recyclerView;
    private Button saveButton, addQuestionButton;
    private ExaminerQuestionListAdapter adapter;
    private ArrayList<QuestionListModel> questionList;
    private String quizName, quizDescription;
    private RotateLoading rotateLoading;
    private TextView noQuestionFoundTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_question_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.question_list);

        rotateLoading = findViewById(R.id.mainLoading);
        noQuestionFoundTextView = findViewById(R.id.no_question_found);

        Intent intent = getIntent();
        if (intent != null) {
            quizName = intent.getStringExtra("quizName");
            quizDescription = intent.getStringExtra("quizDescription");
        }

        saveButton = findViewById(R.id.saveQuestionButton);
        addQuestionButton = findViewById(R.id.addNewQuestionButton);
        addQuestionButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        recyclerView = findViewById(R.id.questionListId);
        questionList = new ArrayList<>();
        adapter = new ExaminerQuestionListAdapter(getApplicationContext(), questionList, saveButton, noQuestionFoundTextView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExaminerQuestionListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addNewQuestionButton:
                sendToAddQuestionActivity();
                break;
            case R.id.saveQuestionButton:
                // call SaveQuiz class Constructor
                // Todo: after testing change value to 9
                if (questionList.size() > 1) {
                    rotateLoading.start();
                    examinerSaveQuizList(questionList, quizName, quizDescription);

                } else {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setMessage("Number of Question Must Be More then 9")
                            .show();
                }
                break;
        }

    }

    private void examinerSaveQuizList(ArrayList<QuestionListModel> questionList, String quizName, String quizDescription) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");

        HashMap<String, ArrayList<QuestionListModel>> questionListMap = new HashMap<>();
        questionListMap.put("questionList", questionList);

        String key = rootRef.push().getKey();
        rootRef.child(key).setValue(questionListMap);
        rootRef.child(key).child("quizName").setValue(quizName);
        rootRef.child(key).child("quizDescription").setValue(quizDescription);
        rootRef.child(key).child("createdDateTime").setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        rootRef.child(key).child("publishInfo").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                rotateLoading.stop();
                sendToMainActivity();

            }
        });
    }

    private void sendToMainActivity() {

        Intent intent = new Intent(ExaminerQuestionListActivity.this, ExaminerMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToAddQuestionActivity() {
        Log.d(TAG, "Add new Question");
        Intent intent = new Intent(ExaminerQuestionListActivity.this, ExaminerAddQuestionActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityResult Called");
        if (requestCode == 2) {
            // add Question To  ArrayList
            if (data != null) {
                String heading = data.getStringExtra("heading");
                String optionA = data.getStringExtra("optionA");
                String optionB = data.getStringExtra("optionB");
                String optionC = data.getStringExtra("optionC");
                String optionD = data.getStringExtra("optionD");
                int ansNo = data.getIntExtra("ansNo", 1);
                // create object of QuestionListModel Class
                QuestionListModel model = new QuestionListModel(heading, optionA, optionB, optionC, optionD, ansNo);
                questionList.add(model);
                adapter.notifyDataSetChanged();
                saveButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Questions are not saved")
                .setMessage("Are you really want to close ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ExaminerQuestionListActivity.this, ExaminerMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNeutralButton("No", null)
                .show();
    }

}
