package com.example.jerlib.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jerlib.R;
import com.example.jerlib.fragments.LoginFragment;
import com.example.jerlib.fragments.RegisterFragment;

public class AuthActivity extends AppCompatActivity {
    TextView authSwitchTV;
    Fragment loginFragment;
    Fragment registerFragment;
    Boolean onLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginAuth), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        onLogin = true;
        authSwitchTV = findViewById(R.id.authSwitchTV);
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        loadFragment(loginFragment);
        authSwitchTV.setText(R.string.auth_register_prompt);
        authSwitchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLogin) {
                    authSwitchTV.setText(R.string.auth_login_prompt);
                    loadFragment(registerFragment);
                    onLogin = false;
                } else {
                    authSwitchTV.setText(R.string.auth_register_prompt);
                    loadFragment(loginFragment);
                    onLogin = true;
                }
            }
        });
    }

    private void loadFragment(Fragment fg) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.authFragmentContainer, fg);
        transaction.commit();
    }

}