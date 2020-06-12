package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.ExaminerCandidateInfoModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/*
    Search Candidate By there Email Id
 */
public class ExaminerSearchCandidateActivity extends AppCompatActivity {

    ArrayList<ExaminerCandidateInfoModel> searchList;
    private EditText searchCandidate;
    private RecyclerView CandidateRecyclerList;
    private DatabaseReference candidateRef;
    private String TAG = "Search Candidate Activity";
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_search_candidate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.search_candidate);
        rotateLoading = findViewById(R.id.mainLoading);
        candidateRef = FirebaseDatabase.getInstance().getReference().child("users");
        searchList = new ArrayList<>();
        CandidateRecyclerList = findViewById(R.id.find_candidate_recycler_list);
        CandidateRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        searchCandidate = findViewById(R.id.search_candidate);
        searchCandidate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!rotateLoading.isStart()) {
                    rotateLoading.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {

                    search(s.toString());
                    if (rotateLoading != null && rotateLoading.isStart()) {
                        rotateLoading.stop();
                    }
                } else {
                    search("");
                    if (rotateLoading != null && rotateLoading.isStart()) {
                        rotateLoading.stop();
                    }
                }

            }
        });
    }

    private void search(String s) {
        Query query = candidateRef.orderByChild("email").startAt(s).endAt(s + "\uf8ff");

        candidateRecycleView(query);
    }

    private void candidateRecycleView(Query query) {

        FirebaseRecyclerOptions<ExaminerCandidateInfoModel> options =
                new FirebaseRecyclerOptions.Builder<ExaminerCandidateInfoModel>()
                        .setQuery(query, ExaminerCandidateInfoModel.class)
                        .build();
        rotateLoading.start();
        FirebaseRecyclerAdapter<ExaminerCandidateInfoModel, SearchCandidateViewHolder> adapter =
                new FirebaseRecyclerAdapter<ExaminerCandidateInfoModel, SearchCandidateViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SearchCandidateViewHolder searchCandidateViewHolder, final int position, @NonNull final ExaminerCandidateInfoModel examinerCandidate) {

                        searchCandidateViewHolder.userName.setText(examinerCandidate.getName());
                        searchCandidateViewHolder.userEmail.setText(examinerCandidate.getEmail());
                        Picasso.get().load(examinerCandidate.getProfilePic()).placeholder(R.drawable.ic_profile).into(searchCandidateViewHolder.profileImage);

                        searchCandidateViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                String user_id =getRef(position).getKey();
                                Intent intent = new Intent();
                                intent.putExtra("candidateName", examinerCandidate.getName());
                                intent.putExtra("candidateEmail", examinerCandidate.getEmail());
                                intent.putExtra("profile_image", examinerCandidate.getProfilePic());
                                setResult(2, intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onDataChanged() {
                        if (rotateLoading != null && rotateLoading.isStart()) {
                            rotateLoading.stop();
                        }
                    }

                    @NonNull
                    @Override
                    public SearchCandidateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_examiner_candidate_info, viewGroup, false);
                        return new SearchCandidateViewHolder(view);
                    }
                };

        CandidateRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    protected void onStart() {
        super.onStart();

        candidateRecycleView(candidateRef);


    }

    public static class SearchCandidateViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;
        CircleImageView profileImage;

        SearchCandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.candidate_name);
            userEmail = itemView.findViewById(R.id.candidate_email);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }


}
