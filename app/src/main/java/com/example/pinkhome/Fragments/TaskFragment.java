package com.example.pinkhome.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.Adapter.TaskTodayAdapter;
import com.example.pinkhome.Day;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.TaskViewModel;
import com.example.pinkhome.model.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskFragment extends Fragment {
    private RecyclerView taskRecycler;
    private ArrayList<Task> taskList = new ArrayList<>();
    private TaskViewModel taskViewModel;
    private TaskTodayAdapter adapterTask;
    private String dayOfWeek;
    private TextView dayText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = ViewModelProviders.of(requireActivity()).get(TaskViewModel.class);
        taskRecycler = view.findViewById(R.id.task_recycle);
        dayText = view.findViewById(R.id.day_of_week);
        adapterTask = new TaskTodayAdapter(requireContext(), taskList);
        taskRecycler.setAdapter(adapterTask);
        getTasks();
        dayOfWeek = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        dayText.setText(Day.valueOf("DAY_"+dayOfWeek).getDay());
    }

    private void getTasks() {
        taskViewModel.getTaskLiveData().observe(requireActivity(), items -> {
            for (Task task : items) {
                if (!task.getDone()) taskList.add(task);
                if (taskList.size() == 3) break;
            }
            adapterTask.notifyDataSetChanged();
        });
    }
}
