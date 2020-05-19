package com.ak47.digiboard.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;

public class ExaminerSearchCandidateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_search_candidate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.search_candidate);


    }
}
