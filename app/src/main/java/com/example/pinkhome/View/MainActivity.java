package com.example.pinkhome.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.example.pinkhome.Adapter.TimeAdapter;
import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.Day;
import com.example.pinkhome.Fragments.EventsFragment;
import com.example.pinkhome.Fragments.TaskFragment;
import com.example.pinkhome.Fragments.TaskListFragment;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.TimeViewModel;
import com.example.pinkhome.ViewModel.UserViewModel;
import com.example.pinkhome.model.TimeItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;

import io.getstream.avatarview.AvatarView;

public class MainActivity extends BaseActivity {
    private RecyclerView timeRecycler;

    private TextView lastButton;

    private int time;
    private View bottomPanel;
    private EditText input;
    private String head, descr;
    private ArrayList<TimeItem> timeItemList = new ArrayList<>();
    private UserViewModel userViewModel;
    private FragmentManager firstFragmentManager, secondFragmentManager;
    private String dayOfWeek;
    private TextView username;
    private AvatarView avatarView;
    private TimeAdapter timeAdapter;
    private ImageButton settings;
    private Button addTimeItemButton;
    private TextView tasksButton, eventsButton, mainButton;
    private ProgressBar progressBarUser;
    private TimeViewModel timeViewModel;
    private TaskFragment mainTaskFragment;
    private EventsFragment mainEventFragment, eventsFragment;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        getTimeItems();
        setUserData();


        settings.setOnClickListener(v -> toSettings());
        eventsButton.setOnClickListener(v -> toEvents());
        tasksButton.setOnClickListener(v -> toTasks());
        mainButton.setOnClickListener(v -> toMain());

        addTimeItemButton.setOnClickListener(v -> addTimeItem());
    }

    private void addTimeItem() {
        input = new EditText(this);
        input.setMaxWidth(15);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select time")
                .build();
        materialTimePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            time = materialTimePicker.getHour() * 60 + materialTimePicker.getMinute();
            getHead();
        });
    }

    private void getHead() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Название")
                .setView(input)
                .setPositiveButton("ok", (dialog, which) -> {
                    head = String.valueOf(input.getText());
                    dialog.dismiss();
                    getDescription();
                })
                .show();
    }

    private void getDescription() {
        input = new EditText(this);
        input.setMaxWidth(15);
        input.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        new MaterialAlertDialogBuilder(this)
                .setTitle("Описание")
                .setView(input)
                .setPositiveButton("ok", (dialog, which) -> {
                    descr = String.valueOf(input.getText());
                    timeViewModel.addTimeItem(head, descr, String.valueOf(time), dayOfWeek, timeItemList.size());
                })
                .show();
    }

    private void getTimeItems() {
        timeViewModel.listenTimeItem(dayOfWeek).observe(this, items -> {
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
                    avatarView.setAvatarInitials(userName.split("")[0]);
                }
            } else username.setText("");
        });
    }

    private void initLayout() {
        dayOfWeek = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        ConstraintLayout mainContent = findViewById(R.id.main_content);
        progressBarUser = mainContent.findViewById(R.id.progressUser);
        avatarView = mainContent.findViewById(R.id.user).findViewById(R.id.avatar);

        mainTaskFragment = new TaskFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.first_container, mainTaskFragment).commit();
        mainEventFragment = new EventsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.two_container, mainEventFragment).addToBackStack("main_event").commit();

        settings = mainContent.findViewById(R.id.settings_button);

        bottomPanel = findViewById(R.id.bottom_schedule);
        //category button
        tasksButton = mainContent.findViewById(R.id.category).findViewById(R.id.task);
        mainButton = mainContent.findViewById(R.id.category).findViewById(R.id.all_button);
        eventsButton = mainContent.findViewById(R.id.category).findViewById(R.id.event);
        lastButton = mainButton;
        mainButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        username = mainContent.findViewById(R.id.user).findViewById(R.id.login);
        addTimeItemButton = findViewById(R.id.bottom_schedule).findViewById(R.id.add_button);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        timeViewModel = ViewModelProviders.of(this).get(TimeViewModel.class);

        timeRecycler = findViewById(R.id.bottom_schedule).findViewById(R.id.recycler_layout_time);
        timeAdapter = new TimeAdapter(this, timeItemList);
        timeRecycler.setAdapter(timeAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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

    @SuppressLint("PrivateResource")
    private void toEvents() {
        if(lastButton != eventsButton) {
            bottomPanel.setVisibility(View.INVISIBLE);
            eventsFragment = new EventsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.first_container, eventsFragment).runOnCommit(() -> eventsFragment.setVisibilityCard(true)).commit();
            if (lastButton == mainButton)
                getSupportFragmentManager().popBackStack("main_event", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            lastButton.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            eventsButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            lastButton = eventsButton;
        }
    }

    private void toSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void toTasks() {
        if(lastButton != tasksButton) {
            bottomPanel.setVisibility(View.INVISIBLE);
            if (lastButton == mainButton)
                getSupportFragmentManager().popBackStack("main_event", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            TaskListFragment taskListFragment = new TaskListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.first_container, taskListFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            lastButton.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tasksButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            lastButton = tasksButton;
        }
    }

    private void toMain() {
        bottomPanel.setVisibility(View.VISIBLE);
        if(lastButton != mainButton){
            lastButton.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mainButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            lastButton = mainButton;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.first_container, new TaskFragment()).commit();
            eventsFragment = new EventsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.two_container, eventsFragment)
                    .addToBackStack("main_event")
                    .commit();
        }
    }

}