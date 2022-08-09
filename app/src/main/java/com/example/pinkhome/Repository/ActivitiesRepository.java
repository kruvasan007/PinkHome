package com.example.pinkhome.Repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pinkhome.model.Activities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiesRepository {
    final private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ActivitiesRepository(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Activities>> getActivities(){
        MutableLiveData<List<Activities>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("activities");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
                List<Activities> activities = new ArrayList<>();
                for (DocumentSnapshot document: documentSnapshot) {
                    Activities activity = new Activities();
                    activity.setNameActivity(String.valueOf(document.getData().get("name")));
                    activity.setDate(String.valueOf(document.getData().get("date")));
                    activities.add(activity);
                }
                data.setValue(activities);
            } else {
                Log.d("E", "get failed with ", task.getException());
            }
        });
        return data;
    }

    public LiveData<List<Activities>> listenActivities(){
        MutableLiveData<List<Activities>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("activities");
        collectionReference.addSnapshotListener(MetadataChanges.INCLUDE, (value, e) -> {
            List<DocumentSnapshot> documentSnapshot = value.getDocuments();
            List<Activities> activities = new ArrayList<>();
            for (DocumentSnapshot document: documentSnapshot) {
                Activities activity = new Activities();
                activity.setNameActivity(String.valueOf(document.getData().get("name")));
                activity.setDate(String.valueOf(document.getData().get("date")));
                activities.add(activity);
            }
            data.setValue(activities);
        });
        return data;
    }

    public void deleteActivities(String name){
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("activities").document(name)
                .delete();
    }

    public void createActivities(String name, String date){
        Map<String, Object> activity = new HashMap<>();
        activity.put("name", name);
        activity.put("date", date);
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("activities")
                .document(name)
                .set(activity);
    }
}
