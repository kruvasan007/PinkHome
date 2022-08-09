package com.example.pinkhome.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.TaskViewModel;
import com.example.pinkhome.Adapter.TasksAdapter;
import com.example.pinkhome.model.Task;

import java.util.ArrayList;

public class TasksActivity extends BaseActivity {
    private ImageButton backButton;
    private Button addButton, doneButton;
    private RecyclerView taskRecycler;
    private InputMethodManager imm;
    private TaskViewModel taskViewModel;
    private ArrayList<Task> taskList;
    private TasksAdapter adapter;
    private ConstraintLayout overKeyboard;
    private EditText addTask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initLayout();
        getTasks();
        backButton.setOnClickListener(v -> toMainActivity());
        addButton.setOnClickListener(v -> addTask());
        doneButton.setOnClickListener(v -> doneTask());
    }

    private void getTasks() {
        taskViewModel.listenTask().observe(this, items -> {
            taskList.clear();
            for (Task task: items) {
                if(!task.getDone()) taskList.add(task);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void addTask(){
        overKeyboard.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.INVISIBLE);
        addTask.requestFocus();
        imm.showSoftInput(addTask, InputMethodManager.SHOW_IMPLICIT);
    }

    private void doneTask(){
        overKeyboard.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.VISIBLE);
        taskViewModel.addTask(addTask.getText().toString());
        addTask.clearFocus();
        imm.hideSoftInputFromWindow(addTask.getWindowToken(),0);
    }

    private void initLayout() {
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        addButton = findViewById(R.id.add_button);
        addTask = findViewById(R.id.over_keyboard).findViewById(R.id.add_task);
        doneButton = findViewById(R.id.over_keyboard).findViewById(R.id.done_button);
        overKeyboard = findViewById(R.id.over_keyboard);
        backButton = findViewById(R.id.back_button);
        taskRecycler = findViewById(R.id.today_task).findViewById(R.id.recycler_layout);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskList = new ArrayList<>();
        adapter = new TasksAdapter(this,taskList);
        taskRecycler.setAdapter(adapter);

    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
