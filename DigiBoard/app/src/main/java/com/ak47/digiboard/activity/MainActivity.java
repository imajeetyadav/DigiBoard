package com.ak47.digiboard.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ak47.digiboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private ImageView imageView;
    private TextView textName, textEmail;
    private CardView activeQuiz, quizHistory, quizResult, quizNotification, quizSetting, quizAbout;
    private android.app.AlertDialog enableNotificationListenerAlertDialog;
    // Loading Animation
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotateLoading = findViewById(R.id.mainLoading);

        rotateLoading.start();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        changeStatusBarColor();

        imageView = findViewById(R.id.user_photo);
        textName = findViewById(R.id.user_name);
        textEmail = findViewById(R.id.user_email);

        activeQuiz = findViewById(R.id.activeQuiz);
        quizHistory = findViewById(R.id.history);
        quizResult = findViewById(R.id.result);
        quizNotification = findViewById(R.id.notification);
        quizSetting = findViewById(R.id.setting);
        quizAbout = findViewById(R.id.about);

        activeQuiz.setOnClickListener(this);
        quizHistory.setOnClickListener(this);
        quizResult.setOnClickListener(this);
        quizNotification.setOnClickListener(this);
        quizSetting.setOnClickListener(this);
        quizAbout.setOnClickListener(this);


        assert user != null;
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        rootRef.keepSynced(true);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userProfile = Objects.requireNonNull(dataSnapshot.child("profilePic").getValue()).toString();
                textName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                textEmail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                Picasso.get().load(userProfile).placeholder(R.drawable.profle_pic).into(imageView);
                rotateLoading.stop();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Profile Retrieval  - " + databaseError.getMessage());
                rotateLoading.stop();

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activeQuiz:
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                assert notificationManager != null;
                if (!notificationManager.isNotificationPolicyAccessGranted()) {
                    enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
                    enableNotificationListenerAlertDialog.show();
                } else {
                    finish();
                    startActivity(new Intent(MainActivity.this, QuizActivity.class));
                }
                break;
            case R.id.history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
            case R.id.result:
                startActivity(new Intent(MainActivity.this, ResultActivity.class));
                break;
            case R.id.notification:
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            default:
                Log.e(TAG, "Invalid Selection");
                break;
        }

    }


    private void changeStatusBarColor() {

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //if the user is not logged in
        //opening the login activity
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private android.app.AlertDialog buildNotificationServiceAlertDialog() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.notification_listener_service)
                .setMessage(R.string.notification_listener_service_explanation)
                .setCancelable(false)
                .setPositiveButton("Enable Notification Access",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);
                            }
                        });
        return (alertDialogBuilder.create());
    }

}
