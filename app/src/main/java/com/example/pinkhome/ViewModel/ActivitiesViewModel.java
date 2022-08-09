package com.example.pinkhome.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pinkhome.Repository.ActivitiesRepository;
import com.example.pinkhome.model.Activities;

import java.util.List;

public class ActivitiesViewModel extends AndroidViewModel {

    private ActivitiesRepository activitiesRepository;
    private LiveData<List<Activities>> activitiesLiveData;

    public ActivitiesViewModel(@NonNull Application application) {
        super(application);
        activitiesRepository = new ActivitiesRepository();
        this.activitiesLiveData = activitiesRepository.getActivities();
    }

    public void deleteActivities(String name){
        activitiesRepository.deleteActivities(name);

        Toast.makeText(getApplication().getBaseContext(), "Запись удалена",Toast.LENGTH_SHORT).show();
    }

    public LiveData<List<Activities>> getActivitiesResponse(){
        return activitiesLiveData;
    }

    public LiveData<List<Activities>> listenActivitiesResponse(){ return activitiesRepository.listenActivities(); }

    public void createActivities(String name, String date) {
        if(!name.equals("") && !date.equals("")){
            activitiesRepository.createActivities(name,date);
            Toast.makeText(getApplication().getBaseContext(), "Добавлена новая запись",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplication().getBaseContext(), "Поля не заполнены",Toast.LENGTH_SHORT).show();
        }
    }
}
