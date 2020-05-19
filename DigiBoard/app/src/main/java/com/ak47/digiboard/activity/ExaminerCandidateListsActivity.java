package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;

public class ExaminerCandidateListsActivity extends AppCompatActivity {

    Button addNewCandidateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_candidate_lists);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.student_list);

        addNewCandidateList = findViewById(R.id.addNewCandidateListButton);
        addNewCandidateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newCandidateList = new Intent(ExaminerCandidateListsActivity.this, ExaminerNewCandidateListActivity.class);
                startActivity(newCandidateList);
            }
        });
    }

}
