package com.ak47.digiboard.activity;

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
import com.ak47.digiboard.model.CandidateQuizListBaseModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
    Candidate Quiz History

 */
public class CandidateHistoryActivity extends AppCompatActivity {
    private static final String TAG = "CandidateHistory";
    private RecyclerView quizRecyclerList;
    private DatabaseReference quizRef;
    private TextView noQuizFound;
    private int quizCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.quiz_history);

        noQuizFound = findViewById(R.id.no_quiz_found);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        quizRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("MyQuizLists");
        quizRecyclerList = findViewById(R.id.quizRecyclerView);
        quizRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CandidateQuizListBaseModel> candidateQuizListBaseModelFirebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<CandidateQuizListBaseModel>()
                        .setQuery(quizRef, CandidateQuizListBaseModel.class)
                        .build();


        FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizHistoryViewHolder> adapter = new FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizHistoryViewHolder>(candidateQuizListBaseModelFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CandidateQuizHistoryViewHolder holder, int position, @NonNull CandidateQuizListBaseModel model) {
                if (model.getIsAttempted()) {
                    quizCount = quizCount + 1;
                    noQuizFound.setText(MessageFormat.format("{0} {1}", getString(R.string.total_quiz), quizCount));
                    holder.quizName.setText(model.getQuizName());
                    holder.quizDescription.setText(model.getQuizDescription());
                    holder.activeTillDataTime.setTextColor(getColor(R.color.colorPrimaryDark));
                    holder.activeTillDataTime.setBackgroundColor(getColor(R.color.gradient_color));
                    holder.activeTillDataTime.setText(String.format("Quiz Date %s\nDone", model.getQuizDate()));
                } else {
                    try {
                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        Date dateToday = sdfDate.parse(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Calendar.getInstance().getTime()));
                        Date dateQuizLastDate = sdfDate.parse(model.getQuizDate());
                        if (dateToday.after(dateQuizLastDate)) {
                            quizCount = quizCount + 1;
                            noQuizFound.setText(MessageFormat.format("{0} {1}", getString(R.string.total_quiz), quizCount));
                            holder.quizName.setText(model.getQuizName());
                            holder.quizDescription.setText(model.getQuizDescription());
                            holder.activeTillDataTime.setTextColor(getColor(R.color.colorPrimaryDark));
                            holder.activeTillDataTime.setBackgroundColor(getColor(R.color.bg_screen1));
                            holder.activeTillDataTime.setText(String.format("Quiz Date %s\nExpired", model.getQuizDate()));
                        } else if (dateToday.equals(dateQuizLastDate)) {
                            String currentTime = new SimpleDateFormat("HH:mm", Locale.US).format(Calendar.getInstance().getTime()).replace(":", "");
                            String end = model.getEndTime().replace(":", "");
                            holder.quizName.setText(model.getQuizName());
                            holder.quizDescription.setText(model.getQuizDescription());

                            if (Integer.parseInt(currentTime) > Integer.parseInt(end)) {
                                quizCount = quizCount + 1;
                                noQuizFound.setText(MessageFormat.format("{0} {1}", getString(R.string.total_quiz), quizCount));
                                holder.activeTillDataTime.setTextColor(getColor(R.color.colorPrimaryDark));
                                holder.activeTillDataTime.setBackgroundColor(getColor(R.color.bg_screen1));
                                holder.activeTillDataTime.setText(String.format("Quiz Date%s\nExpired", model.getQuizDate()));

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
                    }
                }
            }

            @NonNull
            @Override
            public CandidateQuizHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_candidate_quiz_details, viewGroup, false);
                return new CandidateQuizHistoryViewHolder(view);
            }
        };

        quizRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class CandidateQuizHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView quizName;
        TextView quizDescription;
        TextView activeTillDataTime;

        CandidateQuizHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.quizName);
            quizDescription = itemView.findViewById(R.id.quizDescription);
            activeTillDataTime = itemView.findViewById(R.id.activeTillDateTime);
        }
    }
}
