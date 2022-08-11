package com.example.pinkhome.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.ViewModel.TaskViewModel;
import com.example.pinkhome.model.Task;
import com.example.pinkhome.R;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private ArrayList<Task> data;
    private final TaskViewModel taskViewModel;

    public TasksAdapter(@NonNull Context context, ArrayList<Task> data) {
        this.data = data;
        this.taskViewModel = ViewModelProviders.of((FragmentActivity) context).get(TaskViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private CheckBox button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            button = itemView.findViewById(R.id.button);

            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                taskViewModel.setDone(description.getText().toString(), isChecked);
            });
        }

        @SuppressLint("ResourceAsColor")
        public void bind(Task item) {
            description.setText(item.getDescription());
            button.setChecked(item.getDone());
            if (item.getDone()) {
                description.setPaintFlags(description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                description.setTypeface(null, Typeface.BOLD);
                itemView.setBackground(itemView.getContext().getDrawable(R.drawable.task_item_style));
            }
        }
    }
}
