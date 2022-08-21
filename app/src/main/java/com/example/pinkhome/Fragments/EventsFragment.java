package com.example.pinkhome.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinkhome.Adapter.EventAdapter;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.EventsViewModel;
import com.example.pinkhome.model.Events;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textview.MaterialTextView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class EventsFragment extends Fragment{
    private LinearLayoutManager layoutManager;
    private EditText nameText;
    private String selectedColor = String.valueOf(R.color.green_white);
    private ImageButton addButton;
    private ImageView colorChooseButton;
    private MaterialTextView dateText;
    private MutableLiveData<Integer> visibilityCard = new MutableLiveData<>();
    private ArrayList<Events> eventsArrayList = new ArrayList<>();
    private RecyclerView eventsRecycle;
    private EventsViewModel eventsViewModel;
    private EventAdapter adapterActivities;
    private ConstraintLayout addCard;
    private ProgressBar progressBarEvents;
    private LinearSnapHelper snapHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addCard = view.findViewById(R.id.new_card);
        eventsViewModel = ViewModelProviders.of(requireActivity()).get(EventsViewModel.class);
        progressBarEvents = view.findViewById(R.id.progressBar);
        eventsRecycle = view.findViewById(R.id.recycler_layout_activity);
        addCard.setVisibility(View.INVISIBLE);
        nameText = addCard.findViewById(R.id.name);
        selectedColor = String.valueOf(R.color.green_white);
        colorChooseButton = addCard.findViewById(R.id.choose_color);
        dateText = addCard.findViewById(R.id.date);
        addButton = addCard.findViewById(R.id.add_button);
        visibilityCard.observe(requireActivity(), integer -> {
            addCard.setVisibility(integer);
        });
        colorChooseButton.setOnClickListener(v -> colorPicker());
        initRecycler();
        getActivities();

        addButton.setOnClickListener(v -> createNewCard());
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        dateText.setOnClickListener(v -> materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.setTimeInMillis((Long) selection);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = format.format(calendar.getTime());
                    dateText.setText(formattedDate);
                });
    }

    private void colorPicker() {
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder()
                .setColor(R.color.green_white)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowCustom(true)
                .setAllowPresets(true)
                .setColorShape(ColorShape.SQUARE)
                .create();
        colorPickerDialog.show(requireFragmentManager(),"00");
        colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int idColor) {
                colorChooseButton.setBackgroundColor(idColor);
                selectedColor = String.valueOf(idColor);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
    }

    private void initRecycler(){
        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        adapterActivities = new EventAdapter(requireActivity(), eventsArrayList);
        eventsRecycle.setAdapter(adapterActivities);
        eventsRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View newSnapPosition = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (newSnapPosition != null) {
                    if (layoutManager.getPosition(newSnapPosition) != adapterActivities.getLastSnapPosition()) {
                        adapterActivities.setSnapPosition(layoutManager.getPosition(newSnapPosition));
                    }
                }
            }
        });
        eventsRecycle.setLayoutManager(layoutManager);
        eventsRecycle.setItemAnimator(new DefaultItemAnimator());
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(eventsRecycle);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                eventsViewModel.deleteActivities(eventsArrayList.get(viewHolder.getAdapterPosition()).getNameActivity());
                eventsArrayList.remove(viewHolder.getAdapterPosition());
                adapterActivities.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(eventsRecycle);
    }

    private void getActivities() {
        eventsViewModel.listenActivitiesResponse().observe(requireActivity(), events -> {
            progressBarEvents.setVisibility(View.INVISIBLE);
            if (events.size() != 0) {
                eventsArrayList.clear();
                eventsArrayList.addAll(events);
                adapterActivities.notifyDataSetChanged();
            } else {
                Events activities = new Events();
                activities.setNameActivity("Событий нет");
                eventsArrayList.add(activities);
                adapterActivities.notifyDataSetChanged();
            }
        });
    }

    private void createNewCard() {
        String name = nameText.getText().toString();
        String date = dateText.getText().toString();
        eventsViewModel.createActivities(name, date, selectedColor);
        dateText.setText("");
        nameText.setText("");
        selectedColor = String.valueOf(R.color.green_white);
        nameText.clearFocus();
        dateText.clearFocus();
    }

    public void setVisibilityCard(Boolean visible){
        if(visible){
            visibilityCard.setValue(View.VISIBLE);
            runAnimationUp();
        } else {
            visibilityCard.setValue(View.INVISIBLE);
            runAnimationDown();
        }
    }

    private void runAnimationUp()
    {
        Animator set = AnimatorInflater.loadAnimator(requireActivity(), R.animator.slide_in_down);
        set.setTarget(eventsRecycle);
        set.start();
    }

    private void runAnimationDown()
    {
        Animator set = AnimatorInflater.loadAnimator(requireActivity(), R.animator.slide_in_up);
        set.setTarget(eventsRecycle);
        set.start();
    }

}
