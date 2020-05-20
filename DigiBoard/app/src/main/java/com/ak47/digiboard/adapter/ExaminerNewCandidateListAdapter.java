package com.ak47.digiboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.ExaminerCandidateListModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/*
    #done
    create list by searching in ExaminerSearchCandidateActivity and left swipe to delete candidate

 */

public class ExaminerNewCandidateListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Button saveListButton;
    private TextView noCandidateFound;
    private ArrayList<ExaminerCandidateListModel> candidateList;


    public ExaminerNewCandidateListAdapter(Context context, ArrayList<ExaminerCandidateListModel> candidateList, Button saveListButton, TextView noCandidateFound) {
        this.context = context;
        this.candidateList = candidateList;
        this.saveListButton = saveListButton;
        this.noCandidateFound = noCandidateFound;
    }

    public void removeItem(int position) {
        candidateList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, candidateList.size());
        noCandidateFound.setText("Total Candidate: " + candidateList.size());
    }

    public void restoreItem(ExaminerCandidateListModel examinerCandidateListModel, int position) {
        candidateList.add(position, examinerCandidateListModel);
        // notify item added by position
        notifyItemInserted(position);
        noCandidateFound.setText("Total Candidate: " + candidateList.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_examiner_candidate_info, parent, false);
        return new CandidateListViewHolder(v);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExaminerCandidateListModel examinerCandidateListModel = candidateList.get(position);
        final CandidateListViewHolder candidateListViewHolder = (CandidateListViewHolder) holder;
        candidateListViewHolder.candidateName.setText(examinerCandidateListModel.getName());
        candidateListViewHolder.candidateEmail.setText(examinerCandidateListModel.getEmail());
        Picasso.get().load(examinerCandidateListModel.getProfilePic()).placeholder(R.drawable.ic_profile).into(candidateListViewHolder.profileImage);

        noCandidateFound.setText("Total Candidate: " + candidateList.size());
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    static class CandidateListViewHolder extends RecyclerView.ViewHolder {
        TextView candidateName, candidateEmail;
        CircleImageView profileImage;

        CandidateListViewHolder(@NonNull View itemView) {
            super(itemView);
            candidateName = itemView.findViewById(R.id.candidate_name);
            candidateEmail = itemView.findViewById(R.id.candidate_email);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
