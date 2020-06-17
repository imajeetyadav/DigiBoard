package com.ak47.digiboard.activity;

import android.os.Bundle;
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

/*
       Result of Candidate
    */
public class CandidateResultActivity extends AppCompatActivity {

    private static final String TAG = "CandidateResult";
    private RecyclerView quizRecyclerList;
    private DatabaseReference quizRef;
    private TextView noQuizFound;
    private int quizCount = 0;
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.result);

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
        FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizResultViewHolder> adapter = new FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizResultViewHolder>(candidateQuizListBaseModelFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CandidateQuizResultViewHolder holder, int position, @NonNull CandidateQuizListBaseModel model) {
                if (model.getIsAttempted()) {
                    quizCount = quizCount + 1;
                    noQuizFound.setText(MessageFormat.format("{0} {1}", getString(R.string.total_quiz), quizCount));
                    holder.quizName.setText(model.getQuizName());
                    holder.quizDescription.setText(model.getQuizDescription());

                    if (model.getResult() == null && model.getQuizStartTime() == null && model.getQuizEndTime() == null) {
                        holder.result.setText(R.string.not_submitted_properly);
                        holder.result.setBackgroundColor(getColor(R.color.bg_screen1));
                    } else {
                        holder.result.setText(String.format("Marks : %s %%", model.getResult()));
                        holder.result.setBackgroundColor(getColor(R.color.gradient_color));
                    }

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
            public CandidateQuizResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                                    int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_candidate_quiz_details, viewGroup, false);
                return new CandidateQuizResultViewHolder(view);
            }
        };
        quizRecyclerList.setAdapter(adapter);
        adapter.startListening();

    }

    private void showWarningMessage(String quizDate) {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setMessage("Leaderboard will available after " + quizDate)
                .show();
    }

    public static class CandidateQuizResultViewHolder extends RecyclerView.ViewHolder {
        TextView quizName;
        TextView quizDescription;
        TextView result;

        CandidateQuizResultViewHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.quizName);
            quizDescription = itemView.findViewById(R.id.quizDescription);
            result = itemView.findViewById(R.id.activeTillDateTime);
        }
    }
}
