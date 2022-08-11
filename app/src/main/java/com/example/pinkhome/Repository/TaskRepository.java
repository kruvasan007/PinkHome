package com.example.pinkhome.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.pinkhome.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskRepository {
    final private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public TaskRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Task>> getTask() {
        final MutableLiveData<List<Task>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("tasks");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Task> tasks = new ArrayList<>();
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : docs) {
                    Task item = new Task();
                    item.setDescription(String.valueOf(documentSnapshot.getData().get("description")));
                    item.setDone(Boolean.valueOf(String.valueOf(documentSnapshot.getData().get("done"))));
                    tasks.add(item);
                }
                data.setValue(tasks);
            } else {
                Log.d("E", "get failed with ", task.getException());
            }
        });
        return data;
    }

    public MutableLiveData<List<Task>> listenTask(Boolean done){
        final MutableLiveData<List<Task>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("tasks");
        collectionReference.addSnapshotListener((value, error) -> {
            if(error == null){
                List<DocumentSnapshot> documentSnapshot = value.getDocuments();
                List<Task> tasks = new ArrayList<>();
                for (DocumentSnapshot document: documentSnapshot) {
                    if(Boolean.valueOf(String.valueOf(document.getData().get("done"))).equals(done)){
                        Task item = new Task();
                        item.setDescription(String.valueOf(document.getData().get("description")));
                        item.setDone(Boolean.valueOf(String.valueOf(document.getData().get("done"))));
                        tasks.add(item);
                    }
                }
                data.setValue(tasks);
            }
        });
        return data;
    }

    public void setDone(String descr, Boolean state){
        db.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("tasks").document(descr).update("done", state);
    }

    public void setTask(String descr, Boolean done) {
        Map<String, Object> item = new HashMap<>();
        item.put("description", descr);
        item.put("done", done);
        db.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("tasks").document(descr).set(item);
    }
}
