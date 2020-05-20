package com.ak47.digiboard.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.ExaminerCandidateInfoModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExaminerSearchCandidateAdapter extends RecyclerView.Adapter<ExaminerSearchCandidateAdapter.SearchViewHolder> {

    private Activity activity;
    private ArrayList<ExaminerCandidateInfoModel> searchList;


    public ExaminerSearchCandidateAdapter(Activity activity, ArrayList<ExaminerCandidateInfoModel> searchList) {
        this.activity = activity;
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_examiner_candidate_info, parent, false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int position) {
        final ExaminerCandidateInfoModel examinerCandidateInfoModel = searchList.get(position);

        searchViewHolder.userName.setText(examinerCandidateInfoModel.getName());
        searchViewHolder.userEmail.setText(examinerCandidateInfoModel.getEmail());
        Picasso.get().load(examinerCandidateInfoModel.getProfilePic()).placeholder(R.drawable.ic_profile).into(searchViewHolder.profileImage);
        searchViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                                String user_id =getRef(position).getKey();
                Intent intent = new Intent();
                intent.putExtra("candidateName", examinerCandidateInfoModel.getName());
                intent.putExtra("candidateEmail", examinerCandidateInfoModel.getEmail());
                intent.putExtra("profile_image", examinerCandidateInfoModel.getProfilePic());
                activity.setResult(2, intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;
        CircleImageView profileImage;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.candidate_name);
            userEmail = itemView.findViewById(R.id.candidate_email);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
