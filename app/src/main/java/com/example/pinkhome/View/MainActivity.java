package com.example.pinkhome.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.Adapter.ActivitiesAdapter;
import com.example.pinkhome.Adapter.EmptyActivityAdapter;
import com.example.pinkhome.Adapter.TaskTodayAdapter;
import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.ActivitiesViewModel;
import com.example.pinkhome.ViewModel.TaskViewModel;
import com.example.pinkhome.ViewModel.UserViewModel;
import com.example.pinkhome.model.Activities;
import com.example.pinkhome.model.Task;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private RecyclerView eventsRecycle, taskRecycler;

    private LinearLayoutManager layoutManager;
    private ArrayList<Activities> eventsArrayList = new ArrayList<>();
    private ArrayList<Task> taskList = new ArrayList<>();
    private ActivitiesViewModel eventsViewModel;
    private UserViewModel userViewModel;
    private ActivitiesAdapter adapterActivities;
    private TextView username;
    private ImageButton settings;
    private ImageButton tasksButton, eventsButton;
    private ProgressBar progressBarEvents, progressBarUser;
    private TaskViewModel taskViewModel;
    private TaskTodayAdapter adapterTask;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        getActivities();
        getTasks();
        setUserData();

        settings.setOnClickListener(v -> toSettings());
        eventsButton.setOnClickListener(v -> toEvents());
        tasksButton.setOnClickListener(v -> toTasks());

        eventsRecycle.setOnTouchListener((v, event) -> {
            if(eventsRecycle.getAdapter().getClass().getName().equals(EmptyActivityAdapter.class.getName())){
                toEvents();
                return true;
            }
            else return false;
        });
    }

    private void getActivities() {
        eventsViewModel.getActivitiesResponse().observe(this, events -> {
            progressBarEvents.setVisibility(View.INVISIBLE);
            if (events.size() != 0) {
                eventsArrayList.addAll(events);
                adapterActivities.notifyDataSetChanged();
            } else {
                eventsRecycle.setAdapter(new EmptyActivityAdapter());
            }
        });
    }

    private void getTasks() {
        taskViewModel.getTaskLiveData().observe(this, items -> {
            for (Task task: items) {
                if(!task.getDone()) taskList.add(task);
            }
            adapterTask.notifyDataSetChanged();
        });
    }

    private void setUserData() {
        userViewModel.getUserName().observe(this, userName -> {
            progressBarUser.setVisibility(View.INVISIBLE);
            if(userName != null){
                username.setText(userName);
            } else username.setText("");
        });
    }

    private void initLayout() {
        progressBarUser = findViewById(R.id.head).findViewById(R.id.progressUser);
        eventsRecycle = findViewById(R.id.recycler_layout_activity);
        layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        eventsRecycle.setLayoutManager(layoutManager);
        adapterActivities = new ActivitiesAdapter(MainActivity.this, eventsArrayList);
        eventsRecycle.setAdapter(adapterActivities);
        progressBarEvents = findViewById(R.id.progressBar);

        taskRecycler = findViewById(R.id.today_task).findViewById(R.id.task_recycle);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskList = new ArrayList<>();
        adapterTask = new TaskTodayAdapter(this,taskList);
        taskRecycler.setAdapter(adapterTask);

        settings = findViewById(R.id.head).findViewById(R.id.settings_button);
        eventsButton = findViewById(R.id.category).findViewById(R.id.linear_button_category).findViewById(R.id.event).findViewById(R.id.event_button);
        tasksButton = findViewById(R.id.category).findViewById(R.id.linear_button_category).findViewById(R.id.task).findViewById(R.id.task_button);

        username = findViewById(R.id.head).findViewById(R.id.user).findViewById(R.id.login);
        eventsViewModel = ViewModelProviders.of(this).get(ActivitiesViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    private void toEvents() {
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
        finish();
    }

    private void toSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void toTasks() {
        Intent intent = new Intent(this, TasksActivity.class);
        startActivity(intent);
        finish();
    }
}