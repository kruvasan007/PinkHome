package com.example.pinkhome.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.Adapter.ActivitiesAdapter;
import com.example.pinkhome.Adapter.EmptyActivityAdapter;
import com.example.pinkhome.Adapter.TaskTodayAdapter;
import com.example.pinkhome.Adapter.TimeAdapter;
import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.ActivitiesViewModel;
import com.example.pinkhome.ViewModel.TaskViewModel;
import com.example.pinkhome.ViewModel.TimeViewModel;
import com.example.pinkhome.ViewModel.UserViewModel;
import com.example.pinkhome.model.Activities;
import com.example.pinkhome.model.Task;
import com.example.pinkhome.model.TimeItem;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;

import io.getstream.avatarview.AvatarView;

public class MainActivity extends BaseActivity {
    private RecyclerView eventsRecycle, taskRecycler, timeRecycler;

    private LinearLayoutManager layoutManager;
    private ArrayList<Activities> eventsArrayList = new ArrayList<>();
    private ArrayList<TimeItem> timeItemList = new ArrayList<>();
    private ArrayList<Task> taskList = new ArrayList<>();
    private ActivitiesViewModel eventsViewModel;
    private UserViewModel userViewModel;
    private ActivitiesAdapter adapterActivities;
    private TextView username;
    private AvatarView avatarView;
    private TimeAdapter timeAdapter;
    private ImageButton settings;
    private Button addTimeItemButton;
    private ImageButton tasksButton, eventsButton;
    private ProgressBar progressBarEvents, progressBarUser;
    private TaskViewModel taskViewModel;
    private TaskTodayAdapter adapterTask;
    private TimeViewModel timeViewModel;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        getActivities();
        getTasks();
        getTimeItems();
        setUserData();

        settings.setOnClickListener(v -> toSettings());
        eventsButton.setOnClickListener(v -> toEvents());
        tasksButton.setOnClickListener(v -> toTasks());
        addTimeItemButton.setOnClickListener(v -> addTimeItem());

        eventsRecycle.setOnTouchListener((v, event) -> {
            if (eventsRecycle.getAdapter().getClass().getName().equals(EmptyActivityAdapter.class.getName())) {
                toEvents();
                return true;
            } else return false;
        });
    }

    private void addTimeItem() {
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select time")
                .build();
        materialTimePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            timeViewModel.addTimeItem(materialTimePicker.getHour()+"",materialTimePicker.getMinute()+"",materialTimePicker.getHour()+":"+materialTimePicker.getMinute(),timeItemList.size());
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
            for (Task task : items) {
                if (!task.getDone()) taskList.add(task);
                if (taskList.size() == 3) break;
            }
            adapterTask.notifyDataSetChanged();
        });
    }

    private void getTimeItems() {
        timeViewModel.listenTimeItem().observe(this, items -> {
            timeItemList.clear();
            timeItemList.addAll(items);
            timeAdapter.notifyDataSetChanged();
        });
    }

    private void setUserData() {
        userViewModel.getUserName().observe(this, userName -> {
            progressBarUser.setVisibility(View.INVISIBLE);
            if (userName != null) {
                username.setText(userName);
                if (!userName.equals("")) {
                    avatarView.setAvatarInitials(userName.split("")[1]);
                }
            } else username.setText("");
        });
    }

    private void initLayout() {
        ConstraintLayout mainContent = findViewById(R.id.main_content);
        progressBarUser = mainContent.findViewById(R.id.head).findViewById(R.id.progressUser);
        eventsRecycle = mainContent.findViewById(R.id.recycler_layout_activity);
        progressBarEvents = mainContent.findViewById(R.id.progressBar);
        avatarView = mainContent.findViewById(R.id.head).findViewById(R.id.user).findViewById(R.id.avatar);
        taskRecycler = mainContent.findViewById(R.id.today_task).findViewById(R.id.task_recycle);
        settings = mainContent.findViewById(R.id.head).findViewById(R.id.settings_button);
        eventsButton = mainContent.findViewById(R.id.category).findViewById(R.id.linear_button_category).findViewById(R.id.event).findViewById(R.id.event_button);
        tasksButton = mainContent.findViewById(R.id.category).findViewById(R.id.linear_button_category).findViewById(R.id.task).findViewById(R.id.task_button);
        username = mainContent.findViewById(R.id.head).findViewById(R.id.user).findViewById(R.id.login);
        timeRecycler = findViewById(R.id.bottom_schedule).findViewById(R.id.recycler_layout_time);
        addTimeItemButton = findViewById(R.id.bottom_schedule).findViewById(R.id.add_button);

        eventsViewModel = ViewModelProviders.of(this).get(ActivitiesViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        timeViewModel = ViewModelProviders.of(this).get(TimeViewModel.class);

        layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        eventsRecycle.setLayoutManager(layoutManager);
        adapterActivities = new ActivitiesAdapter(MainActivity.this, eventsArrayList);
        adapterTask = new TaskTodayAdapter(this, taskList);
        timeAdapter = new TimeAdapter(this, timeItemList);
        timeRecycler.setAdapter(timeAdapter);
        eventsRecycle.setAdapter(adapterActivities);
        taskRecycler.setAdapter(adapterTask);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                timeViewModel.deleteTimeItem(timeItemList.get(viewHolder.getAdapterPosition()).getHead());
                timeItemList.remove(viewHolder.getAdapterPosition());
                timeAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(timeRecycler);
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