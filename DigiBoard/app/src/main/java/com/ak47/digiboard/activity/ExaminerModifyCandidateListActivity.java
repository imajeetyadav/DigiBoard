package com.ak47.digiboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.adapter.ExaminerNewCandidateListAdapter;
import com.ak47.digiboard.common.ExaminerSaveCandidateList;
import com.ak47.digiboard.common.SwipeToDeleteCallback;
import com.ak47.digiboard.model.ExaminerCandidateListModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

/*
    #Done
    activity_examiner_new_candidate_list.xml layout used
 */

public class ExaminerModifyCandidateListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ModifyCandidateList";
    private RecyclerView recyclerView;
    private Button saveListButton, addCandidateButton;
    private ExaminerNewCandidateListAdapter adapter;
    private ArrayList<ExaminerCandidateListModel> candidateList;
    private RotateLoading rotateLoading;
    private TextView noCandidateFound;
    private Paint p = new Paint();
    private String listName;
    private String getData = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_new_candidate_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.new_candidate_list);

        rotateLoading = findViewById(R.id.mainLoading);
        noCandidateFound = findViewById(R.id.no_candidate_found);

        // get listName from ExaminerCandidateListsActivity
        Intent intent = getIntent();
        if (intent != null) {
            listName = intent.getStringExtra("listName");
            getData = intent.getStringExtra("getData");
        }

        saveListButton = findViewById(R.id.saveCandidateListButton);
        addCandidateButton = findViewById(R.id.addNewCandidateButton);
        addCandidateButton.setOnClickListener(this);
        saveListButton.setOnClickListener(this);
        recyclerView = findViewById(R.id.candidateList);
        candidateList = new ArrayList<>();

        adapter = new ExaminerNewCandidateListAdapter(getApplicationContext(), candidateList, saveListButton, noCandidateFound);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExaminerModifyCandidateListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // add data to list from firebase
        if (getData.equals("yes")) {
            getData = addCandidateData(listName);
        }
        enableSwipe();
    }

    private String addCandidateData(String listName) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists").child(listName);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ExaminerCandidateListModel model = data.getValue(ExaminerCandidateListModel.class);
                    if (model != null) {
                        candidateList.add(model);
                        adapter.notifyDataSetChanged();
                    }
                    noCandidateFound.setText("Total Candidate: " + dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error : " + databaseError.getMessage());
            }
        });

        return "no";
    }

    private void enableSwipe() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final ExaminerCandidateListModel deletedModel = candidateList.get(position);
                final int deletedPosition = position;
                adapter.removeItem(position);
                // showing snack bar with Undo option
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), " Removed from List", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        adapter.restoreItem(deletedModel, deletedPosition);
                        recyclerView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewCandidateButton:
                sendToAddCandidateActivity();
                break;
            case R.id.saveCandidateListButton:
                // call SaveQuiz class Constructor
                if (candidateList.size() > 3) {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setTitle("List Modified")
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // get user input and set it to result
                                    rotateLoading.start();
                                    new ExaminerSaveCandidateList(candidateList, listName);
                                    rotateLoading.stop();
                                    Intent intent = new Intent(ExaminerModifyCandidateListActivity.this, ExaminerCandidateListsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();

                } else {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setMessage("Number of Candidate Must Be More then 4")
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityResult Called");
        if (requestCode == 2) {
            // add Candidate To  ArrayList
            if (data != null) {
                String name = data.getStringExtra("candidateName");
                final String email = data.getStringExtra("candidateEmail");
                String profile_image = data.getStringExtra("profile_image");
                // create object of Candidate ListModel Class
                if (checkAlreadyExist(candidateList, email)) {
                    Toast.makeText(ExaminerModifyCandidateListActivity.this, "Candidate already exist", Toast.LENGTH_LONG).show();
                } else {
                    ExaminerCandidateListModel model = new ExaminerCandidateListModel(name, email, profile_image);
                    candidateList.add(model);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private boolean checkAlreadyExist(ArrayList<ExaminerCandidateListModel> candidateList, String email) {
        for (ExaminerCandidateListModel candidateListModel : candidateList) {
            if (candidateListModel.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void sendToAddCandidateActivity() {
        Log.d(TAG, "Add new Candidate");
        Intent intent = new Intent(ExaminerModifyCandidateListActivity.this, ExaminerSearchCandidateActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Candidate list is not saved")
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


