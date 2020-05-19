package com.ak47.digiboard.common;

import com.ak47.digiboard.model.ExaminerCandidateListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ExaminerSaveCandidate {
    public ExaminerSaveCandidate(ArrayList<ExaminerCandidateListModel> candidateList, String candidateListName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists");

        HashMap<String, ArrayList<ExaminerCandidateListModel>> candidateListMap = new HashMap<>();
        candidateListMap.put("candidate", candidateList);

        String key = rootRef.push().getKey();
        assert key != null;
        rootRef.child(key).setValue(candidateListName);
        rootRef.child(key).child("candidate List").setValue(candidateListMap);
    }
}
