package com.ak47.digiboard.common;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ak47.digiboard.activity.NoticeActivity;
import com.ak47.digiboard.model.ConfigModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckNotice {
    private static final String TAG = "CheckNotice";

    public CheckNotice(final Activity activity) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("AppConfig").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ConfigModel configModel = dataSnapshot.getValue(ConfigModel.class);
                Log.e(TAG, "Check Service");
                assert configModel != null;
                if (configModel.getService()) {
                    Intent intentNoticeActivity = new Intent(activity, NoticeActivity.class);
                    activity.startActivity(intentNoticeActivity);
                    activity.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Examiner Error : " + databaseError.getMessage());
            }
        });
    }
}
