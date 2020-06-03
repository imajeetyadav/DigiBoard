package com.ak47.digiboard.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ak47.digiboard.R;
import com.ak47.digiboard.activity.ActivitySelection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private static final int BROADCAST_NOTIFICATION_ID = 1;
    private String notificationChannelIdQuizAlert = "1000";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        String identifyDataType = remoteMessage.getData().get(getString(R.string.data_type));
        SharedPreferences sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE);
        int initialSetupInt = sharedPreferences.getInt("initial_setup", 0);
        if (identifyDataType.equals(getString(R.string.data_type_quiz_message)) && initialSetupInt == 1) {
            //build quiz broadcast notification
            String title = remoteMessage.getData().get(getString(R.string.data_title));
            String message = remoteMessage.getData().get(getString(R.string.data_message));
            sendBroadcastNotification(title, message);
        }

    }

    private void sendBroadcastNotification(String title, String message) {
        Log.d(TAG, "sendBroadcastNotification: building a admin broadcast notification");

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                notificationChannelIdQuizAlert);
        // Creates an Intent for the Activity
        Intent notifyIntent = new Intent(this, ActivitySelection.class);
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.ic_active_quiz)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_active_quiz))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle("New Quiz")
                .setContentText(title)
                .setSubText(message)
                .setColor(getColor(R.color.gradient_color))
                .setAutoCancel(true);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(BROADCAST_NOTIFICATION_ID, builder.build());

    }

    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer : sending token to server " + token);
        SharedPreferences sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE);
        int initialSetupInt = sharedPreferences.getInt("initial_setup", 0);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        if (initialSetupInt == 1) {
            rootRef.child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("token").setValue(token);
        } else if (initialSetupInt == 2) {
            rootRef.child("AdminUsers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("token").setValue(token);
        }
    }
}
