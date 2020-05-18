package com.ak47.digiboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.model.QuestionListModel;

import java.util.ArrayList;


public class QuestionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Button saveButton;
    private TextView noQuestionFound;
    private ArrayList<QuestionListModel> questionList;

    public QuestionListAdapter(Context context, ArrayList<QuestionListModel> questionList, Button saveButton, TextView noQuestionFoundTextView) {
        this.context = context;
        this.questionList = questionList;
        this.saveButton = saveButton;
        this.noQuestionFound = noQuestionFoundTextView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_examiner_single_question, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        QuestionListModel model = questionList.get(position);

        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.question.setText(model.getQuestion());
        myViewHolder.seq.setText(String.valueOf(position + 1));
        myViewHolder.a.setText(model.getOptionA());
        myViewHolder.b.setText(model.getOptionB());
        myViewHolder.c.setText(model.getOptionC());
        myViewHolder.d.setText(model.getOptionD());
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, myViewHolder.delete);
                popupMenu.getMenuInflater().inflate(R.menu.deletemenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(context, "delete clicked", Toast.LENGTH_SHORT).show();
                        questionList.remove(position);
                        if (questionList.size() == 0) {
                            saveButton.setVisibility(View.INVISIBLE);
                            noQuestionFound.setVisibility(View.INVISIBLE);
                        }
                        notifyDataSetChanged();

                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView question, seq, a, b, c, d;
        private ImageView delete;

        MyViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.qid);
            a = itemView.findViewById(R.id.A);
            b = itemView.findViewById(R.id.B);
            c = itemView.findViewById(R.id.C);
            d = itemView.findViewById(R.id.D);
            seq = itemView.findViewById(R.id.sequenceId);
            delete = itemView.findViewById(R.id.textViewOptions);

        }

    }
}
