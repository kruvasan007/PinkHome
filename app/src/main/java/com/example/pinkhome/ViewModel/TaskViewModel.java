package com.example.pinkhome.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pinkhome.Repository.TaskRepository;
import com.example.pinkhome.model.Task;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;
    private LiveData<List<Task>> taskLiveData;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        this.taskRepository = new TaskRepository();
        this.taskLiveData = taskRepository.getTask();
    }

    public LiveData<List<Task>> getTaskLiveData() {
        return taskLiveData;
    }

    public void addTask(String description){
        taskRepository.setTask(description, false);
    }
    public void setDone(String description){
        taskRepository.setDone(description);
    }

    public LiveData<List<Task>> listenTask(){ return taskRepository.listenTask(); }
}
