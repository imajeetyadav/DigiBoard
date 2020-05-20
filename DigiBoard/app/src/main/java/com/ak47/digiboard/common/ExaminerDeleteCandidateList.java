package com.ak47.digiboard.common;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExaminerDeleteCandidateList {
    public ExaminerDeleteCandidateList(String listName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists");
        rootRef.child(listName).removeValue();
    }
}
