package com.example.pinkhome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.model.Activities;
import com.example.pinkhome.R;

import java.util.ArrayList;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {
    private ArrayList<Activities> activities;

    public ActivitiesAdapter(Context context, ArrayList<Activities> activities){
        this.activities = activities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_button,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesAdapter.ViewHolder holder, int position) {
        holder.bind(activities.get(position));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameView, dateView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            dateView = itemView.findViewById(R.id.date);
        }

        public void bind(Activities item){
            nameView.setText(item.getNameActivity());
            dateView.setText(item.getDate());
        }
    }
}
