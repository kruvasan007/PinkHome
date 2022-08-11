package com.example.pinkhome.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.Adapter.ActivitiesAdapter;
import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.ActivitiesViewModel;
import com.example.pinkhome.model.Activities;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class EventsActivity extends BaseActivity {
    private ImageButton backButton, addButton;
    private ActivitiesViewModel activitiesViewModel;
    private EditText nameText;
    private MaterialTextView dateText;
    private ProgressBar progressBar;

    public static final String TAG = "EditActivity";
    private ArrayList<Activities> eventsArrayList = new ArrayList<>();
    private RecyclerView eventsRecycle;
    private RecyclerView.LayoutManager layoutManager;
    private ActivitiesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initRecycler();
        backButton.setOnClickListener(v -> toMainActivity());
        addButton.setOnClickListener(v -> createNewCard());
        activitiesViewModel = ViewModelProviders.of(this).get(ActivitiesViewModel.class);
        getActivities();

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        dateText.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> dateText.setText(materialDatePicker.getHeaderText()));
    }

    private void initRecycler() {
        backButton = findViewById(R.id.back_button);
        addButton = findViewById(R.id.new_card).findViewById(R.id.add_button);
        nameText = findViewById(R.id.new_card).findViewById(R.id.name);
        dateText = findViewById(R.id.new_card).findViewById(R.id.date);
        progressBar = findViewById(R.id.progressBar);
        eventsRecycle = findViewById(R.id.recycler_layout_activity);
        layoutManager = new LinearLayoutManager(EventsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        eventsRecycle.setLayoutManager(layoutManager);
        adapter = new ActivitiesAdapter(EventsActivity.this, eventsArrayList);
        eventsRecycle.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                activitiesViewModel.deleteActivities(eventsArrayList.get(viewHolder.getAdapterPosition()).getNameActivity());
                eventsArrayList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(eventsRecycle);
    }

    private void getActivities() {
        activitiesViewModel.listenActivitiesResponse().observe(this, events -> {
            progressBar.setVisibility(View.INVISIBLE);
            eventsArrayList.clear();
            eventsArrayList.addAll(events);
            adapter.notifyDataSetChanged();
        });
    }

    private void createNewCard() {
        String name = nameText.getText().toString();
        String date = dateText.getText().toString();
        activitiesViewModel.createActivities(name,date);
        dateText.setText("");
        nameText.setText("");
        nameText.clearFocus();
        dateText.clearFocus();
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
