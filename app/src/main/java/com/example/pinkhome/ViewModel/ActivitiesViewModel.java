package com.example.pinkhome.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pinkhome.Repository.Repository;
import com.example.pinkhome.model.Activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiesViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Activities>> activitiesLiveData;

    public ActivitiesViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
        this.activitiesLiveData = repository.listenActivities();
    }

    public void deleteActivities(String name){
        repository.deleteItem(name,"activities");

        Toast.makeText(getApplication().getBaseContext(), "Delete",Toast.LENGTH_SHORT).show();
    }

    public LiveData<List<Activities>> listenActivitiesResponse(){ return activitiesLiveData; }

    public void createActivities(String name, String date) {
        if(!name.equals("") && !date.equals("")){
            Map<String, Object> activity = new HashMap<>();
            activity.put("name", name);
            activity.put("date", date);
            repository.createItem(activity,name, "activities");
            Toast.makeText(getApplication().getBaseContext(), "Add new event",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplication().getBaseContext(), "Null field",Toast.LENGTH_SHORT).show();
        }
    }
}
