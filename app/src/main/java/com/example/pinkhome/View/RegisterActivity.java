package com.example.pinkhome.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.UserViewModel;

public class RegisterActivity extends BaseActivity {
    private UserViewModel userAuthViewModel;
    private Button loginButton;
    private EditText emailText, passwordText;
    private TextView registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);

        userAuthViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userAuthViewModel.getUserLiveData().observe(this, firebaseUser -> {
            System.out.println("1111111111111111111111222222222");
            if (firebaseUser != null) {
                startMainActivity();
            }
        });

        loginButton.setOnClickListener(v ->  {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if (email.length() > 0 && password.length() > 0) {
                userAuthViewModel.login(email, password);
            } else {
                Toast.makeText(this, "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(view -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if (email.length() > 0 && password.length() > 0) {
                userAuthViewModel.register(email, password);
            } else {
                Toast.makeText(this, "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
