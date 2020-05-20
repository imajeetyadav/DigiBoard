package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.ak47.digiboard.adapter.ExaminerSearchCandidateAdapter;
import com.ak47.digiboard.model.ExaminerCandidate;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExaminerSearchCandidateActivity extends AppCompatActivity {

    ArrayList<ExaminerCandidate> searchList;
    private EditText searchCandidate;
    private RecyclerView CandidateRecyclerList;
    private DatabaseReference candidateRef;
    private String TAG = "Search Candidate Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_search_candidate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.search_candidate);
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

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {

                    search(s.toString());
                } else {
                    search("");
                }

            }
        });
    }

    private void search(String s) {
        Query query = candidateRef.orderByChild("email").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    searchList.clear();
                    for (DataSnapshot friends : dataSnapshot.getChildren()) {
                        final ExaminerCandidate examinerCandidate = friends.getValue(ExaminerCandidate.class);
                        searchList.add(examinerCandidate);
                    }

                    ExaminerSearchCandidateAdapter searchFriendAdapter = new ExaminerSearchCandidateAdapter(getParent(), searchList);
                    CandidateRecyclerList.setAdapter(searchFriendAdapter);
                    searchFriendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error - " + databaseError.getDetails());

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ExaminerCandidate> options =
                new FirebaseRecyclerOptions.Builder<ExaminerCandidate>()
                        .setQuery(candidateRef, ExaminerCandidate.class)
                        .build();

        FirebaseRecyclerAdapter<ExaminerCandidate, SearchCandidateViewHolder> adapter =
                new FirebaseRecyclerAdapter<ExaminerCandidate, SearchCandidateViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SearchCandidateViewHolder searchCandidateViewHolder, final int position, @NonNull final ExaminerCandidate examinerCandidate) {

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
