package com.ak47.digiboard.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ak47.digiboard.R;
import com.ak47.digiboard.activity.NoticeActivity;
import com.ak47.digiboard.model.ConfigModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckNoticeAndVersion {
    private static final String TAG = "CheckNotice";

    public CheckNoticeAndVersion(final Activity activity) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("AppConfig").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "CheckNotice : Check Service");
                ConfigModel configModel = dataSnapshot.getValue(ConfigModel.class);
                assert configModel != null;
                if (configModel.getService()) {
                    Intent intentNoticeActivity = new Intent(activity, NoticeActivity.class);
                    activity.startActivity(intentNoticeActivity);
                    activity.finish();
                } else if (Integer.parseInt(activity.getString(R.string.versionCode)) < configModel.getVersion()) {
                    new AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                            .setTitle("New Update Available")
                            .setCancelable(false)
                            .setPositiveButton("Update Now ",
                                    (dialog, id) -> {
                                        try {
                                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ak47.digiboard")));
                                        } catch (android.content.ActivityNotFoundException e) {
                                            Log.e(TAG, "android.content.ActivityNotFoundException");
                                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ak47.digiboard")));
                                        } finally {
                                            activity.finish();
                                        }
                                    }).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "CheckNotice : Error : " + databaseError.getMessage());
            }
        });
    }
}
