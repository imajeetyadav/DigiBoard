package com.ak47.digiboard.common;

import com.ak47.digiboard.model.ExaminerQuestionListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ExaminerSaveQuizList {

    public ExaminerSaveQuizList(ArrayList<ExaminerQuestionListModel> questionList, String quizName, String quizDescription, String quizEncryptionCode) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");

        HashMap<String, ArrayList<ExaminerQuestionListModel>> questionListMap = new HashMap<>();
        questionListMap.put("questionList", questionList);

        String key = rootRef.push().getKey();
        rootRef.child(key).setValue(questionListMap);
        rootRef.child(key).child("quizName").setValue(quizName);
        rootRef.child(key).child("quizDescription").setValue(quizDescription);
        rootRef.child(key).child("quizEncryptionCode").setValue(quizEncryptionCode);
        rootRef.child(key).child("publishInfo").setValue(false);
        rootRef.child(key).child("createdDateTime").setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));

    }
}
