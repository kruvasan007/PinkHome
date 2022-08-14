package com.example.pinkhome.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pinkhome.Repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends AndroidViewModel {
    private UserRepository authRepository;
    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<Boolean> logOutLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        authRepository = new UserRepository(application);
        userLiveData = authRepository.getUserData();
        logOutLiveData = authRepository.getLoggedOutData();
    }

    public void login(String email, String password) {
        authRepository.singUpUser(email, password);
        userLiveData = authRepository.getUserData();
    }

    public void logOut(){
        authRepository.logOut();
    }

    public void register(String email, String password) {
        authRepository.registerUser(email, password);
        userLiveData = authRepository.getUserData();
    }

    public void updateNickName(String nickName){
        if(!nickName.equals("") && nickName.length() >= 1) {
            authRepository.setUserName(nickName);
        } else
            Toast.makeText(getApplication().getApplicationContext(), "Недопустимый ник",Toast.LENGTH_SHORT).show();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
    public LiveData<String> getUserName() {
        return authRepository.getUserName();
    }
}
