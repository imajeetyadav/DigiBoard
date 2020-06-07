package com.ak47.digiboard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ak47.digiboard.R;

import java.util.ArrayList;
import java.util.Map;

public class ExaminerResultAdapter extends BaseAdapter {
    private final ArrayList<Map.Entry<String, String>> mData;

    public ExaminerResultAdapter(Map<String, String> map) {
        mData = new ArrayList<>();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.candidate_email)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.candidate_score)).setText(item.getValue());

        return result;
    }
}
