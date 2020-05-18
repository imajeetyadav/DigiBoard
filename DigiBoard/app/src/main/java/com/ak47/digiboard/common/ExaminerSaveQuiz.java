package com.ak47.digiboard.common;

import com.ak47.digiboard.model.QuestionListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ExaminerSaveQuiz {

    public ExaminerSaveQuiz(ArrayList<QuestionListModel> questionList, String quizName, String quizDescription, String quizEncryptionCode) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");
        HashMap<String, ArrayList<QuestionListModel>> questionListMap = new HashMap<>();
        questionListMap.put("questionList", questionList);

        HashMap<String, String> quizDetails = new HashMap<>();
        quizDetails.put("quizName", quizName);
        quizDetails.put("quizDescription", quizDescription);
        quizDetails.put("quizEncryptionCode", quizEncryptionCode);

        String key = rootRef.push().getKey();
        rootRef.child(key).setValue(questionListMap);
        rootRef.child(key).child("quizDetails").setValue(quizDetails);
    }
}
