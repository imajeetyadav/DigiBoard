package com.ak47.digiboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.adapter.ExaminerQuestionListAdapter;
import com.ak47.digiboard.common.ExaminerSaveQuizList;
import com.ak47.digiboard.model.ExaminerQuestionListModel;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;


/*
    -Question List
    -Add Question
     //ToDo : Add questions
 */
public class ExaminerQuestionListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QuestionListActivity";
    private RecyclerView recyclerView;
    private Button saveButton, addQuestionButton;
    private ExaminerQuestionListAdapter adapter;
    private ArrayList<ExaminerQuestionListModel> questionList;
    private String quizName, quizDescription, quizEncryptionCode;
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
            quizEncryptionCode = intent.getStringExtra("quizEncryptionCode");
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
                if (questionList.size() > 9) {
                    rotateLoading.start();
                    new ExaminerSaveQuizList(questionList, quizName, quizDescription, quizEncryptionCode);
                    rotateLoading.stop();
                    Intent intent = new Intent(ExaminerQuestionListActivity.this, ExaminerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setMessage("Number of Question Must Be More then 9")
                            .show();
                }
                break;
        }

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
                ExaminerQuestionListModel model = new ExaminerQuestionListModel(heading, optionA, optionB, optionC, optionD, ansNo);
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
                        finish();
                    }
                })
                .setNeutralButton("No", null)
                .show();
    }

}
