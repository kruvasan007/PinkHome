package com.example.pinkhome.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pinkhome.Repository.Repository;
import com.example.pinkhome.model.TimeItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeViewModel extends AndroidViewModel {
    private Repository repository;

    public TimeViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public void addTimeItem(String head, String description, String time, int id){
        if(id < 6) {
            Map<String, Object> item = new HashMap<>();
            item.put("head", head);
            item.put("description", description);
            item.put("time", time);
            repository.createTimeItem(item, head);
        } else Toast.makeText(getApplication().getApplicationContext(),"Куда столько дел, ты чо...", Toast.LENGTH_SHORT).show();
    }

    public LiveData<List<TimeItem>> listenTimeItem(){ return repository.listenTimeItem();}

    public void deleteTimeItem(String name) {
        repository.deleteTimeItem(name);
    }
}
