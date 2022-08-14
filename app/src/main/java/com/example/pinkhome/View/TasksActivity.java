package com.example.pinkhome.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.ItemMoveCallback;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.TaskViewModel;
import com.example.pinkhome.Adapter.TasksAdapter;
import com.example.pinkhome.model.Task;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Collections;

public class TasksActivity extends BaseActivity {
    private ImageButton backButton;
    private Button addButton;
    private ImageButton doneButton;
    private MaterialDatePicker.Builder materialDateBuilder;
    private RecyclerView taskCurrentRecycler, taskDoneRecycler;
    private InputMethodManager imm;
    private TaskViewModel taskViewModel;
    private ArrayList<Task> taskCurrentList, taskDoneList;
    private TasksAdapter adapterCurrent, adapterDone;
    private ConstraintLayout overKeyboard;
    private EditText addTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initLayout();
        getCurrentTasks();
        getDoneTasks();
        backButton.setOnClickListener(v -> toMainActivity());
        addButton.setOnClickListener(v -> addTask());
        doneButton.setOnClickListener(v -> doneTask());
    }

    private void getCurrentTasks() {
        taskViewModel.listenTask(false).observe(this, items -> {
            taskCurrentList.clear();
            taskCurrentList.addAll(items);
            adapterCurrent.notifyDataSetChanged();
        });
    }

    private void getDoneTasks() {
        taskViewModel.listenTask(true).observe(this, items -> {
            taskDoneList.clear();
            taskDoneList.addAll(items);
            adapterDone.notifyDataSetChanged();
        });
    }

    private void addTask() {
        overKeyboard.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.INVISIBLE);
        addTask.requestFocus();
        imm.showSoftInput(addTask, InputMethodManager.SHOW_IMPLICIT);
    }

    private void doneTask() {
        overKeyboard.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.VISIBLE);
        int id = taskDoneList.size() + taskCurrentList.size();
        taskViewModel.addTask(addTask.getText().toString(), id);
        addTask.clearFocus();
        imm.hideSoftInputFromWindow(addTask.getWindowToken(), 0);
        addTask.setText("");
    }

    private void initLayout() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        addButton = findViewById(R.id.add_button);
        addTask = findViewById(R.id.over_keyboard).findViewById(R.id.add_task);
        doneButton = findViewById(R.id.over_keyboard).findViewById(R.id.done_button);
        overKeyboard = findViewById(R.id.over_keyboard);
        backButton = findViewById(R.id.back_button);
        taskCurrentRecycler = findViewById(R.id.today_task).findViewById(R.id.recycler_layout);
        taskDoneRecycler = findViewById(R.id.done_today_task).findViewById(R.id.recycler_layout_done);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskCurrentList = new ArrayList<>();
        taskDoneList = new ArrayList<>();
        adapterCurrent = new TasksAdapter(this, taskCurrentList);
        adapterDone = new TasksAdapter(this, taskDoneList);
        taskCurrentRecycler.setAdapter(adapterCurrent);
        taskDoneRecycler.setAdapter(adapterDone);


        materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.ACTION_STATE_DRAG;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adapterCurrent.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    if (viewHolder != null) {
                        viewHolder.itemView.setBackground(getDrawable(R.drawable.task_item_style));
                    }
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackground(getDrawable(R.drawable.task_item_style_yellow));
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }
        }).attachToRecyclerView(taskCurrentRecycler);
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        for (int i = 0; i < adapterCurrent.getItemCount(); i++) {
            taskViewModel.changeId(taskCurrentList.get(i).getDescription(), i);
        }
        finish();
    }
}
