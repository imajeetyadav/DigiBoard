package com.ak47.digiboard.activity;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.ak47.digiboard.common.ExaminerDeleteCandidateList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
    #Done
    Show Candidate lists and click to delete and  modify options
 */

public class ExaminerCandidateListsActivity extends AppCompatActivity {

    private Button addNewCandidateList;
    private ListView candidateLists;
    private ArrayList<String> candidateListsName = new ArrayList<>();
    private String userId;
    private String TAG = "CandidateListsActivity";
    private TextView noOFList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_candidate_lists);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.candidate_lists);

        candidateLists = findViewById(R.id.candidate_lists);
        noOFList = findViewById(R.id.no_list_found);

        addNewCandidateList = findViewById(R.id.addNewCandidateListButton);
        addNewCandidateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newCandidateList = new Intent(ExaminerCandidateListsActivity.this, ExaminerNewCandidateListActivity.class);
                startActivity(newCandidateList);
            }
        });

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
                new AlertDialog.Builder(ExaminerCandidateListsActivity.this, R.style.AlertDialogStyle)
                        .setTitle(" What do you want? ")
                        .setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(ExaminerCandidateListsActivity.this, ExaminerModifyCandidateListActivity.class);
                                intent.putExtra("listName", listName.getText().toString());
                                intent.putExtra("getData", "yes");
                                startActivity(intent);
                            }
                        }).setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ExaminerDeleteCandidateList(listName.getText().toString());

                        arrayAdapter.remove(arrayAdapter.getItem(position));
                        arrayAdapter.notifyDataSetChanged();
                        noOFList.setText(getString(R.string.total_candidate_lists) + candidateListsName.size());
                    }
                }).show();
            }
        });

    }

}
