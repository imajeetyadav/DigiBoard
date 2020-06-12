package com.ak47.digiboard.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.CandidateQuizListBaseModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.victor.loading.rotate.RotateLoading;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CandidateMyQuizListActivity extends AppCompatActivity {
    private static final String TAG = "MyQuizList";
    private RecyclerView quizRecyclerList;
    private DatabaseReference quizRef;
    private TextView noQuizFound;
    private int quizCount = 0;
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_my_quiz_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.my_quizzes);

        noQuizFound = findViewById(R.id.no_quiz_found);
        rotateLoading = findViewById(R.id.mainLoading);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        quizRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("MyQuizLists");
        quizRecyclerList = findViewById(R.id.quizRecyclerView);
        quizRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CandidateQuizListBaseModel> candidateQuizListBaseModelFirebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<CandidateQuizListBaseModel>()
                        .setQuery(quizRef, CandidateQuizListBaseModel.class)
                        .build();
        rotateLoading.start();
        FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizListViewHolder> adapter = new FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizListViewHolder>(candidateQuizListBaseModelFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CandidateQuizListViewHolder holder, int position, @NonNull CandidateQuizListBaseModel model) {
                if (!model.getIsAttempted()) {
                    try {
                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        Date dateToday = sdfDate.parse(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Calendar.getInstance().getTime()));
                        Date dateQuizLastDate = sdfDate.parse(model.getQuizDate());
                        if (dateToday.before(dateQuizLastDate)) {
                            quizCount = quizCount + 1;
                            noQuizFound.setText(MessageFormat.format("{0} {1}", getString(R.string.total_quiz), quizCount));
                            holder.quizName.setText(model.getQuizName());
                            holder.quizDescription.setText(model.getQuizDescription());
                            holder.activeTillDataTime.setText(String.format("Active At \nDate : %s \nTime : %s", model.getQuizDate(), model.getStartTime()));
                        } else if (dateToday.equals(dateQuizLastDate)) {
                            String currentTime = new SimpleDateFormat("HH:mm", Locale.US).format(Calendar.getInstance().getTime()).replace(":", "");
                            String start = model.getStartTime().replace(":", "");
                            String end = model.getEndTime().replace(":", "");
                            boolean endTimeCheck = Integer.parseInt(currentTime) < Integer.parseInt(end);
                            boolean startTimeCheck = Integer.parseInt(currentTime) > Integer.parseInt(start);
                            if (startTimeCheck && endTimeCheck) {
                                quizCount = quizCount + 1;
                                noQuizFound.setText(MessageFormat.format("{0} {1}", getString(R.string.total_quiz), quizCount));
                                holder.quizName.setText(model.getQuizName());
                                holder.quizDescription.setText(model.getQuizDescription());
                                holder.activeTillDataTime.setTextColor(getColor(R.color.colorPrimaryDark));
                                holder.activeTillDataTime.setBackgroundColor(getColor(R.color.gradient_color));
                                holder.activeTillDataTime.setText(MessageFormat.format("Active Now\nExpire At {0}", model.getEndTime()));

                            } else if (endTimeCheck) {
                                quizCount = quizCount + 1;
                                noQuizFound.setText(MessageFormat.format("{0} {1}", getString(R.string.total_quiz), quizCount));
                                holder.quizName.setText(model.getQuizName());
                                holder.quizDescription.setText(model.getQuizDescription());
                                holder.activeTillDataTime.setText(String.format("Active At \nDate : %s \nTime : %s", model.getQuizDate(), model.getStartTime()));

                            } else {
                                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                layoutParams.height = 0;
                                holder.itemView.setLayoutParams(layoutParams);
                            }
                        } else {
                            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            layoutParams.height = 0;
                            holder.itemView.setLayoutParams(layoutParams);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.height = 0;
                        holder.itemView.setLayoutParams(layoutParams);
                    }

                    holder.itemView.setOnClickListener(v -> {
                        if (holder.activeTillDataTime.getText().toString().contains("Active Now")) {

                            // NotificationManager to check DND Permission
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                                android.app.AlertDialog enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
                                enableNotificationListenerAlertDialog.show();
                            } else {
                                Intent quizInstructionIntent = new Intent(CandidateMyQuizListActivity.this, CandidateQuizBasicInstruction.class);
                                quizInstructionIntent.putExtra("examinerId", model.getExaminer());
                                quizInstructionIntent.putExtra("quizId", model.getQuizId());
                                startActivity(quizInstructionIntent);
                            }
                        } else {
                            showWarningMessage(model.getQuizDate(), model.getStartTime());
                        }
                    });

                } else {
                    ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = 0;
                    holder.itemView.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void onDataChanged() {
                if (rotateLoading != null && rotateLoading.isStart()) {
                    rotateLoading.stop();
                }
            }

            @NonNull
            @Override
            public CandidateQuizListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_candidate_quiz_details, viewGroup, false);
                return new CandidateQuizListViewHolder(view);
            }
        };

        quizRecyclerList.setAdapter(adapter);
        adapter.startListening();

    }

    private android.app.AlertDialog buildNotificationServiceAlertDialog() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.notification_listener_service)
                .setMessage(R.string.notification_listener_service_explanation)
                .setCancelable(false)
                .setPositiveButton("Enable Notification Access",
                        (dialog, id) -> {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(intent);
                        });
        return (alertDialogBuilder.create());
    }


    private void showWarningMessage(String quizDate, String startTime) {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setMessage("Quiz Active At " + quizDate + " " + startTime)
                .show();
    }

    public static class CandidateQuizListViewHolder extends RecyclerView.ViewHolder {

        TextView quizName;
        TextView quizDescription;
        TextView activeTillDataTime;

        CandidateQuizListViewHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.quizName);
            quizDescription = itemView.findViewById(R.id.quizDescription);
            activeTillDataTime = itemView.findViewById(R.id.activeTillDateTime);
        }
    }
}
