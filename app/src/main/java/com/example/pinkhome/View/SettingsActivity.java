package com.example.pinkhome.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.UserViewModel;

public class SettingsActivity extends BaseActivity {
    private Button logoutButton;
    private ImageButton backButton;
    private EditText nickName;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logoutButton = findViewById(R.id.logout);
        backButton = findViewById(R.id.back_button);
        nickName = findViewById(R.id.nickName);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        backButton.setOnClickListener(v -> toMainActivity());
        logoutButton.setOnClickListener(v -> {
            userViewModel.logOut();
            toRegActivity();
        });
        setNickName();
        nickName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    userViewModel.updateNickName(nickName.getText().toString());
                    v.clearFocus();
                    return true;
                }
            }
            return false;
        });
    }

    private void setNickName() {
        userViewModel.getUserName().observe(this, userName -> {
            nickName.setText(userName);
            nickName.clearFocus();
        });
    }

    private void toRegActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
