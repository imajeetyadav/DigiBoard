package com.ak47.digiboard.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.NotificationModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;

/*
       #Done
       Common Notification Activity
    */
public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "Notification";
    private RecyclerView notificationRecyclerList;
    private DatabaseReference notificationRef;
    private TextView noNotificationFound;
    private int notificationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.notifications);
        SharedPreferences sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE);
        int initialSetupInt = sharedPreferences.getInt("initial_setup", 0);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noNotificationFound = findViewById(R.id.no_notification_found);
        if (initialSetupInt == 1) {
            notificationRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Notifications");
        } else if (initialSetupInt == 2) {
            notificationRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("Notifications");
        }
        notificationRecyclerList = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<NotificationModel> notificationModelFirebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<NotificationModel>()
                        .setQuery(notificationRef, NotificationModel.class)
                        .build();

        FirebaseRecyclerAdapter<NotificationModel, NotificationViewHolder> adapter = new FirebaseRecyclerAdapter<NotificationModel, NotificationViewHolder>(notificationModelFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull NotificationModel model) {
                notificationCount = notificationCount + 1;
                noNotificationFound.setText(MessageFormat.format("{0} {1}", "Total Number of Notification : ", notificationCount));
                holder.notificationTitle.setText(model.getTitle());
                holder.notificationMessage.setText(model.getMessage());
                holder.notificationType.setText(model.getType());
                holder.notificationDateTime.setText(model.getNotificationTime());
            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_notifcation, viewGroup, false);
                return new NotificationViewHolder(view);
            }
        };

        notificationRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle;
        TextView notificationMessage;
        TextView notificationType;
        TextView notificationDateTime;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationMessage = itemView.findViewById(R.id.notification_message);
            notificationDateTime = itemView.findViewById(R.id.notificationDateTime);
            notificationType = itemView.findViewById(R.id.notification_type);

        }
    }
}
