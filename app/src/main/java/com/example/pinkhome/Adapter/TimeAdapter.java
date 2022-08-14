package com.example.pinkhome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.R;
import com.example.pinkhome.model.TimeItem;

import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {

    private ArrayList<TimeItem> data;

    public TimeAdapter(Context context, ArrayList<TimeItem> time){
        this.data = time;
    }

    @NonNull
    @Override
    public TimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeAdapter.ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView header, description, time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.text).findViewById(R.id.head);
            description = itemView.findViewById(R.id.text).findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
        }

        public void bind(TimeItem timeItem) {
            header.setText(timeItem.getHead());
            description.setText(timeItem.getDescription());
            time.setText(timeItem.getTime());
        }
    }
}
