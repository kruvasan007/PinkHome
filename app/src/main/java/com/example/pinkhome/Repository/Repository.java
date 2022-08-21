package com.example.pinkhome.Repository;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pinkhome.model.Events;
import com.example.pinkhome.model.Task;
import com.example.pinkhome.model.TimeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Repository {
    final private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public Repository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void deleteItem(String nameDocument, String nameCollection) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection(nameCollection).document(nameDocument)
                .delete();
    }

    public void createItem(Map<String, Object> item, String nameDocument, String nameCollection) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection(nameCollection)
                .document(nameDocument)
                .set(item);
    }

    public LiveData<List<Events>> listenActivities() {
        MutableLiveData<List<Events>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("activities");
        collectionReference.addSnapshotListener(MetadataChanges.INCLUDE, (value, e) -> {
            List<DocumentSnapshot> documentSnapshot = value.getDocuments();
            List<Events> activities = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshot) {
                Events activity = new Events();
                activity.setNameActivity(String.valueOf(document.getData().get("name")));
                activity.setDate(String.valueOf(document.getData().get("date")));
                activity.setColor(String.valueOf(document.getData().get("color")));
                activities.add(activity);
            }
            activities.sort(new Comparator<Events>() {
                DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                @Override
                public int compare(Events lhs, Events rhs) {
                    try {
                        return f.parse(lhs.getDate()).compareTo(f.parse(rhs.getDate()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
            data.setValue(activities);
        });
        return data;
    }

    public LiveData<List<TimeItem>> listenTimeItem(String day) {
        MutableLiveData<List<TimeItem>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("timeItem");
        collectionReference.addSnapshotListener(MetadataChanges.INCLUDE, (value, e) -> {
            List<DocumentSnapshot> documentSnapshot = value.getDocuments();
            List<TimeItem> times = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshot) {
                if(Objects.requireNonNull(document.getData()).get("day").equals(day)){
                    TimeItem timeItem = new TimeItem();
                    timeItem.setHead(String.valueOf(document.getData().get("head")));
                    timeItem.setDescription(String.valueOf(document.getData().get("description")));
                    timeItem.setTime(Integer.parseInt(String.valueOf(document.getData().get("time"))));
                    timeItem.setDay(String.valueOf(document.getData().get("day")));
                    times.add(timeItem);
                }
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

    public void changeId(String description, int id) {
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks")
                .document(description)
                .update("id", id);
    }
}