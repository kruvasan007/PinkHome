package com.example.pinkhome.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pinkhome.Repository.Repository;
import com.example.pinkhome.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskViewModel extends AndroidViewModel {
    private Repository repo;
    private LiveData<List<Task>> taskLiveData;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        this.repo = new Repository();
        this.taskLiveData = repo.getTask();
    }

    public LiveData<List<Task>> getTaskLiveData() {
        return taskLiveData;
    }

    public void addTask(String description, int id){
        if (!description.equals("")) {
            Map<String, Object> item = new HashMap<>();
            item.put("description", description);
            item.put("done", false);
            item.put("id", id);
            repo.createItem(item, description, "tasks");
        } else{
            Toast.makeText(getApplication().getBaseContext(), "Пустое поле", Toast.LENGTH_SHORT).show();
        }
    }
    public void setDone(String description, Boolean state){
        repo.setDone(description, state);
    }
    public void changeId(String description, int id){
        repo.changeId(description,id);
    }

    public LiveData<List<Task>> listenTask(Boolean done){
        return repo.listenTask(done);
    }
}
