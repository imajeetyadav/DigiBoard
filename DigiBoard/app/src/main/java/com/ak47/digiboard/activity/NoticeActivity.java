package com.ak47.digiboard.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.ConfigModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NoticeActivity extends AppCompatActivity {
    private TextView notice;
    private String TAG = "NoticeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText("Try Again Later");

        notice = findViewById(R.id.notice);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AppConfig");

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ConfigModel configModel = dataSnapshot.getValue(ConfigModel.class);
                assert configModel != null;
                notice.setText(configModel.getMessage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Notice Activity Error : " + databaseError.getMessage());
            }
        });
    }
}

