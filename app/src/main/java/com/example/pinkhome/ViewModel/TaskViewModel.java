package com.example.pinkhome.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.pinkhome.Repository.TaskRepository;
import com.example.pinkhome.model.Task;

import java.util.Collections;
import java.util.Comparator;
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
        if (!description.equals("")) {
            taskRepository.setTask(description, false);
        } else{
            Toast.makeText(getApplication().getBaseContext(), "Пустое поле", Toast.LENGTH_SHORT).show();
        }
    }
    public void setDone(String description, Boolean state){
        taskRepository.setDone(description, state);
    }

    public LiveData<List<Task>> listenTask(Boolean done){
        return taskRepository.listenTask(done);
    }
}
