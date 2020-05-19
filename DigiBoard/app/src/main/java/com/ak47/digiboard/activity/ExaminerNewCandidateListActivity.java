package com.ak47.digiboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.adapter.ExaminerCandidateListAdapter;
import com.ak47.digiboard.common.ExaminerSaveCandidate;
import com.ak47.digiboard.model.ExaminerCandidateListModel;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;


/*
    New candidate list
 */
public class ExaminerNewCandidateListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewCandidateList";
    private RecyclerView recyclerView;
    private Button saveListButton, addCandidateButton;
    private ExaminerCandidateListAdapter adapter;
    private ArrayList<ExaminerCandidateListModel> candidateList;
    private RotateLoading rotateLoading;
    private TextView noCandidateFound;

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

        adapter = new ExaminerCandidateListAdapter(getApplicationContext(), candidateList, saveListButton, noCandidateFound);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExaminerNewCandidateListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewCandidateButton:
                sendToAddCandidateActivity();
                break;
            case R.id.saveCandidateListButton:
                // call SaveQuiz class Constructor
                if (candidateList.size() > 5) {

                    // get prompts.xml view
                    LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());

                    View promptView = layoutInflater.inflate(R.layout.dialog_candidate_list_name, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());

                    // set prompts.xml to be the layout file of the alertdialog builder
                    alertDialogBuilder.setView(promptView);

                    final EditText candidateListName = promptView.findViewById(R.id.listName);

                    // setup a dialog window
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    rotateLoading.start();
                                    new ExaminerSaveCandidate(candidateList, candidateListName.toString());
                                    rotateLoading.stop();
                                    Intent intent = new Intent(ExaminerNewCandidateListActivity.this, ExaminerCandidateListsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create an alert dialog
                    AlertDialog alertD = alertDialogBuilder.create();

                    alertD.show();

                } else {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setMessage("Number of Question Must Be More then 4")
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityResult Called");
        if (requestCode == 2) {
            // add Candidate To  ArrayList
            if (data != null) {
                String name = data.getStringExtra("candidateName");
                String email = data.getStringExtra("candidateEmail");
                String profile_image = data.getStringExtra("profile_image");
                // create object of Candidate ListModel Class
                ExaminerCandidateListModel model = new ExaminerCandidateListModel(name, email, profile_image);
                candidateList.add(model);
                adapter.notifyDataSetChanged();
                saveListButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void sendToAddCandidateActivity() {
        Log.d(TAG, "Add new Candidate");
        Intent intent = new Intent(ExaminerNewCandidateListActivity.this, ExaminerSearchCandidateActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
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
