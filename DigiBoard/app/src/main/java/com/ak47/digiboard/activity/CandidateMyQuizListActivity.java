package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CandidateMyQuizListActivity extends AppCompatActivity {
    private static final String TAG = "MyQuizList";
    private RecyclerView quizRecyclerList;
    private DatabaseReference quizRef;
    private TextView noQuizFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_my_quiz_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.my_quizzes);

        noQuizFound = findViewById(R.id.no_quiz_found);


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        quizRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("MyQuizLists");
        quizRecyclerList = findViewById(R.id.quizRecyclerView);
        quizRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noQuizFound.setText(getString(R.string.total_quiz) + (int) dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error : " + databaseError.getMessage());
            }
        });

        FirebaseRecyclerOptions<CandidateQuizListBaseModel> candidateQuizListBaseModelFirebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<CandidateQuizListBaseModel>()
                        .setQuery(quizRef, CandidateQuizListBaseModel.class)
                        .build();


        FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizListViewHolder> adapter = new FirebaseRecyclerAdapter<CandidateQuizListBaseModel, CandidateQuizListViewHolder>(candidateQuizListBaseModelFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CandidateQuizListViewHolder holder, int position, @NonNull CandidateQuizListBaseModel model) {

                if (!model.isAttempted()) {
                    holder.quizName.setText(model.getQuizName());
                    holder.quizDescription.setText(model.getQuizDescription());
                    holder.activeTillDataTime.setText("Active At \n" + "Date : " + model.getQuizDate() + " \nTime : " + model.getStartTime());
//                    try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                    Date dateToday = sdf.parse("2018-08-30");
//                    Date dateQuizLastDate = sdf.parse(model.getQuizDate());
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent quizInstructionIntent = new Intent(CandidateMyQuizListActivity.this, CandidateQuizBasicInstruction.class);
                        quizInstructionIntent.putExtra("examinerId", model.getExaminer());
                        quizInstructionIntent.putExtra("quizId", model.getQuizId());
                        startActivity(quizInstructionIntent);
                    }
                });
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
