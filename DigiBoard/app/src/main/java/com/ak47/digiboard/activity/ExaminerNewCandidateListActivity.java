package com.ak47.digiboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
     New candidate list
*/
public class ExaminerNewCandidateListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewCandidateList";
    private RecyclerView recyclerView;
    private FloatingActionButton saveListButton, addCandidateButton;
    private ExaminerNewCandidateListAdapter adapter;
    private ArrayList<ExaminerCandidateListModel> candidateList;
    private RotateLoading rotateLoading;
    private TextView noCandidateFound;
    private Paint p = new Paint();

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


        saveListButton = findViewById(R.id.saveCandidateListButton);
        addCandidateButton = findViewById(R.id.addNewCandidateButton);
        addCandidateButton.setOnClickListener(this);
        saveListButton.setOnClickListener(this);
        recyclerView = findViewById(R.id.candidateList);
        candidateList = new ArrayList<>();

        adapter = new ExaminerNewCandidateListAdapter(getApplicationContext(), candidateList, saveListButton, noCandidateFound);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExaminerNewCandidateListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        enableSwipe();
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
                if (candidateList.size() > 4) {

                    final EditText input = new EditText(this);
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setTitle("Save List")
                            .setMessage("What is your List Name?")
                            .setView(input)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // get user input and set it to result

                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyCandidateLists");
                                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.child(input.getText().toString()).exists()) {
                                                Log.e(TAG, String.valueOf(dataSnapshot.child(input.getText().toString()).exists()));
                                                rotateLoading.start();
                                                new ExaminerSaveCandidateList(candidateList, input.getText().toString());
                                                rotateLoading.stop();
                                                Intent intent = new Intent(ExaminerNewCandidateListActivity.this, ExaminerCandidateListsActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(ExaminerNewCandidateListActivity.this, "List name Already exist.Try other name", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e(TAG, databaseError.getMessage());
                                        }
                                    });
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
                    Toast.makeText(ExaminerNewCandidateListActivity.this, "Candidate already exist", Toast.LENGTH_LONG).show();
                } else {
                    ExaminerCandidateListModel model = new ExaminerCandidateListModel(name, email, profile_image);
                    candidateList.add(model);
                    adapter.notifyDataSetChanged();
                }
                saveListButton.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(ExaminerNewCandidateListActivity.this, ExaminerSearchCandidateActivity.class);
        startActivityForResult(intent, 2);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Candidate List is not saved")
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
