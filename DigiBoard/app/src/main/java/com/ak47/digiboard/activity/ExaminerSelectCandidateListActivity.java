package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExaminerSelectCandidateListActivity extends AppCompatActivity {
    private Button addNewCandidateList;
    private ListView candidateLists;
    private ArrayList<String> candidateListsName = new ArrayList<>();
    private String userId;
    private String TAG = "Select Candidate List ";
    private TextView noOFList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_select_candidate_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.candidate_lists);

        candidateLists = findViewById(R.id.candidate_lists);
        noOFList = findViewById(R.id.no_list_found);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.layout_examiner_candidate_lists_model, candidateListsName);
        candidateLists.setAdapter(arrayAdapter);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot name : dataSnapshot.getChildren()) {
                    candidateListsName.add(name.getKey());
                    noOFList.setText(getString(R.string.total_candidate_lists) + candidateListsName.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });


        candidateLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                final TextView listName = view.findViewById(R.id.text1);
                Intent intent = new Intent();
                intent.putExtra("listName", listName.getText().toString());
                setResult(2, intent);
                finish();
            }
        });

    }
}
