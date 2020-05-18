package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;

public class ExaminerAddQuestionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText questionHeading, optionA, optionB, optionC, optionD;
    private Spinner spinner;
    private Button addButton;
    private int ansNo = 1;
    private String[] ansList = {"1", "2", "3", "4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_add_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.question);


        questionHeading = findViewById(R.id.questionHeadingId);
        optionA = findViewById(R.id.optionAId);
        optionB = findViewById(R.id.optionBId);
        optionC = findViewById(R.id.optionCId);
        optionD = findViewById(R.id.optionDId);
        spinner = findViewById(R.id.ansSpinId);
        addButton = findViewById(R.id.saveThisQuestion);
        addButton.setOnClickListener(this);
        spinner = findViewById(R.id.ansSpinId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                ansList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {

        String heading = questionHeading.getText().toString().trim();
        String A = optionA.getText().toString().trim();
        String B = optionB.getText().toString().trim();
        String C = optionC.getText().toString().trim();
        String D = optionD.getText().toString().trim();
        boolean check = validation(heading, A, B, C, D);
        if (check) {
            addQuestionToList(heading, A, B, C, D, ansNo);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ansNo = Integer.parseInt(ansList[position]);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing Selected
    }


    private void addQuestionToList(String heading, String a, String b, String c, String d, int ansNo) {
        Intent intent = new Intent();
        intent.putExtra("heading", heading);
        intent.putExtra("optionA", a);
        intent.putExtra("optionB", b);
        intent.putExtra("optionC", c);
        intent.putExtra("optionD", d);
        intent.putExtra("ansNo", ansNo);
        setResult(2, intent);
        finish();

    }

    private boolean validation(String heading, String a, String b, String c, String d) {
        if (TextUtils.isEmpty(heading)) {
            questionHeading.setError("Enter Question");
            return false;
        } else if (TextUtils.isEmpty(a)) {
            optionA.setError("Enter Option A");
            return false;
        } else if (TextUtils.isEmpty(b)) {
            optionB.setError("Enter Option B");
            return false;
        } else if (TextUtils.isEmpty(c)) {
            optionC.setError("Enter Option C");
            return false;
        } else if (TextUtils.isEmpty(d)) {
            optionD.setError("Enter Option D");
            return false;
        } else {
            return true;
        }
    }
}
