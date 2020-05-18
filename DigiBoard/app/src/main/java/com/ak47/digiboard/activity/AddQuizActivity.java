package com.ak47.digiboard.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ak47.digiboard.R;

public class AddQuizActivity extends AppCompatActivity {

       /*
        - Add Quiz Title,description
        then jump to add question activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);

    }
}
