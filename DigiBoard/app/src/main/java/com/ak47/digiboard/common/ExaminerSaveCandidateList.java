package com.ak47.digiboard.common;

import com.ak47.digiboard.model.ExaminerCandidateListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ExaminerSaveCandidateList {
    public ExaminerSaveCandidateList(ArrayList<ExaminerCandidateListModel> candidateList, String candidateListName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists");
        rootRef.child(candidateListName).setValue(candidateList);
    }
}
