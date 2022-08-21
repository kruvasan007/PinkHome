package com.example.pinkhome.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.Adapter.TasksAdapter;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.TaskViewModel;
import com.example.pinkhome.model.Task;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;

public class TaskListFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_list_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addButton = view.findViewById(R.id.add_button);
        addTask = view.findViewById(R.id.over_keyboard).findViewById(R.id.add_task);
        doneButton = view.findViewById(R.id.over_keyboard).findViewById(R.id.done_button);
        overKeyboard = view.findViewById(R.id.over_keyboard);
        backButton = view.findViewById(R.id.back_button);
        taskCurrentRecycler = view.findViewById(R.id.today_task).findViewById(R.id.recycler_layout);
        taskDoneRecycler = view.findViewById(R.id.recycler_layout_done);

        initLayout();
        getCurrentTasks();
        getDoneTasks();
        addButton.setOnClickListener(v -> addTask());
        doneButton.setOnClickListener(v -> doneTask());
    }

    private void getCurrentTasks() {
        taskViewModel.listenTask(false).observe(requireActivity(), items -> {
            taskCurrentList.clear();
            taskCurrentList.addAll(items);
            adapterCurrent.notifyDataSetChanged();
        });
    }

    private void getDoneTasks() {
        taskViewModel.listenTask(true).observe(requireActivity(), items -> {
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
        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskCurrentList = new ArrayList<>();
        taskDoneList = new ArrayList<>();
        adapterCurrent = new TasksAdapter(requireActivity(), taskCurrentList);
        adapterDone = new TasksAdapter(requireActivity(), taskDoneList);
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
                        viewHolder.itemView.setBackground(requireActivity().getDrawable(R.drawable.task_item_style));
                    }
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackground(requireActivity().getDrawable(R.drawable.task_item_style_yellow));
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

    @Override
    public void onStop() {
        super.onStop();
        for (int i = 0; i < adapterCurrent.getItemCount(); i++) {
            taskViewModel.changeId(taskCurrentList.get(i).getDescription(), i);
        }
    }
}
