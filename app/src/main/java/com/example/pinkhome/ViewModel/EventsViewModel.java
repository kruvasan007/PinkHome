package com.example.pinkhome.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pinkhome.Repository.Repository;
import com.example.pinkhome.model.Events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Events>> activitiesLiveData;

    public EventsViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public void deleteActivities(String name){
        repository.deleteItem(name,"activities");
        Toast.makeText(getApplication().getBaseContext(), "Delete",Toast.LENGTH_SHORT).show();
    }

    public LiveData<List<Events>> listenActivitiesResponse(){ return repository.listenActivities(); }

    public void createActivities(String name, String date, String color) {
        if(!name.equals("") && !date.equals("")){
            Map<String, Object> activity = new HashMap<>();
            activity.put("name", name);
            activity.put("date", date);
            activity.put("color", color);
            repository.createItem(activity,name, "activities");
            Toast.makeText(getApplication().getBaseContext(), "Add new event",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplication().getBaseContext(), "Null field",Toast.LENGTH_SHORT).show();
        }
    }
}
