package com.example.pinkhome.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.pinkhome.BaseActivity;
import com.example.pinkhome.R;
import com.example.pinkhome.ViewModel.UserViewModel;

import java.io.IOException;

import io.getstream.avatarview.AvatarShape;
import io.getstream.avatarview.AvatarView;

public class SettingsActivity extends BaseActivity {
    private Button logoutButton;
    private ImageButton backButton;
    public static final int PICK_IMAGE = 1;
    private EditText nickName;
    private AvatarView avatarView;
    private UserViewModel userViewModel;
    private Bitmap selectedAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logoutButton = findViewById(R.id.logout);
        backButton = findViewById(R.id.back_button);
        nickName = findViewById(R.id.nickName);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        avatarView = findViewById(R.id.avatar);
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
            if(userName != ""){
                avatarView.setAvatarInitials(userName.split("")[0]);
            }
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
