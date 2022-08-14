package com.example.pinkhome.Repository;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pinkhome.model.Activities;
import com.example.pinkhome.model.Task;
import com.example.pinkhome.model.TimeItem;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.gson.internal.bind.JsonTreeReader;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {
    final private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public Repository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Activities>> getActivities() {
        MutableLiveData<List<Activities>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("activities");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
                List<Activities> activities = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshot) {
                    Activities activity = new Activities();
                    activity.setNameActivity(String.valueOf(document.getData().get("name")));
                    activity.setDate(String.valueOf(document.getData().get("date")));
                    activities.add(activity);
                }
                activities.sort(Comparator.comparing(Activities::getReverseDate));
                data.setValue(activities);
            } else {
                Log.d("E", "get failed with ", task.getException());
            }
        });
        return data;
    }

    public LiveData<List<Activities>> listenActivities() {
        MutableLiveData<List<Activities>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("activities");
        collectionReference.addSnapshotListener(MetadataChanges.INCLUDE, (value, e) -> {
            List<DocumentSnapshot> documentSnapshot = value.getDocuments();
            List<Activities> activities = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshot) {
                Activities activity = new Activities();
                activity.setNameActivity(String.valueOf(document.getData().get("name")));
                activity.setDate(String.valueOf(document.getData().get("date")));
                activities.add(activity);
            }
            activities.sort(Comparator.comparing(Activities::getReverseDate));
            data.setValue(activities);
        });
        return data;
    }

    public void deleteActivities(String name) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("activities").document(name)
                .delete();
    }

    public void createActivities(String name, String date) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("name", name);
        activity.put("date", date);
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("activities")
                .document(name)
                .set(activity);
    }

    public void createTimeItem(Map<String, Object> item, String name) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("timeItem")
                .document(name)
                .set(item);
    }

    public LiveData<List<TimeItem>> listenTimeItem() {
        MutableLiveData<List<TimeItem>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("timeItem");
        collectionReference.addSnapshotListener(MetadataChanges.INCLUDE, (value, e) -> {
            List<DocumentSnapshot> documentSnapshot = value.getDocuments();
            List<TimeItem> times = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshot) {
                TimeItem timeItem = new TimeItem();
                timeItem.setHead(String.valueOf(document.getData().get("head")));
                timeItem.setDescription(String.valueOf(document.getData().get("description")));
                timeItem.setTime(String.valueOf(document.getData().get("time")));
                times.add(timeItem);
            }
            times.sort(Comparator.comparing(TimeItem::getTime));
            data.setValue(times);
        });
        return data;
    }

    public MutableLiveData<List<Task>> getTask() {
        final MutableLiveData<List<Task>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Task> tasks = new ArrayList<>();
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : docs) {
                    Task item = new Task();
                    item.setDescription(String.valueOf(documentSnapshot.getData().get("description")));
                    item.setDone(Boolean.valueOf(String.valueOf(documentSnapshot.getData().get("done"))));
                    item.setId(Integer.parseInt(String.valueOf(documentSnapshot.getData().get("id"))));
                    tasks.add(item);
                }
                tasks.sort(Comparator.comparing(Task::getId));
                data.setValue(tasks);
            } else {
                Log.d("E", "get failed with ", task.getException());
            }
        });
        return data;
    }

    public MutableLiveData<List<Task>> listenTask(Boolean done) {
        final MutableLiveData<List<Task>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks");
        collectionReference.addSnapshotListener((value, error) -> {
            if (error == null) {
                List<DocumentSnapshot> documentSnapshot = value.getDocuments();
                List<Task> tasks = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshot) {
                    if (Boolean.valueOf(String.valueOf(document.getData().get("done"))).equals(done)) {
                        Task item = new Task();
                        item.setDescription(String.valueOf(document.getData().get("description")));
                        item.setDone(Boolean.valueOf(String.valueOf(document.getData().get("done"))));
                        item.setId(Integer.parseInt(String.valueOf(document.getData().get("id"))));
                        tasks.add(item);
                    }
                }
                tasks.sort(Comparator.comparing(Task::getId));
                data.setValue(tasks);
            }
        });
        return data;
    }

    public void setDone(String descr, Boolean state) {
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks").document(descr).update("done", state);
    }

    public void setTask(String descr, Boolean done, int id) {
        Map<String, Object> item = new HashMap<>();
        item.put("description", descr);
        item.put("done", done);
        item.put("id", id);
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks").document(descr).set(item);
    }

    public void changeId(String description, int id) {
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks")
                .document(description)
                .update("id", id);
    }

    public void deleteTimeItem(String name) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("timeItem").document(name)
                .delete();
    }
}
