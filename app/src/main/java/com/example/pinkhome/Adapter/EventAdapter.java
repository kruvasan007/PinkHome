package com.example.pinkhome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.model.Activities;
import com.example.pinkhome.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private ArrayList<Activities> activities;
    private int snapPosition = RecyclerView.NO_POSITION;
    private int lastPosition = -1;
    public EventAdapter(Context context, ArrayList<Activities> activities){
        this.activities = activities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_button,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.bind(activities.get(position));
        if(snapPosition == position){
            holder.itemView.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200).start();
        }else {
            holder.itemView.animate().scaleX(1.00f).scaleY(1.00f).setDuration(200).start();
        }
    }

    public void setSnapPosition(int snapPosition) {
        this.lastPosition = this.snapPosition;
        this.snapPosition = snapPosition;
        notifyDataSetChanged();
    }

    public int getLastSnapPosition() {
        return lastPosition;
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameView, dateView;
        private final ImageView squareView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            dateView = itemView.findViewById(R.id.date);
            squareView = itemView.findViewById(R.id.square);
        }

        public void bind(Activities item){
            nameView.setText(item.getNameActivity());
            dateView.setText(item.getDate());
        }
    }
}
