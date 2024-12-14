package com.example.jerlib.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jerlib.R;
import com.example.jerlib.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scopusDetailsAuthorTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseUtil.auth.getCurrentUser();
        if (currentUser == null) {
            Intent invalidAuth = new Intent(this, AuthActivity.class);
            this.startActivity(invalidAuth);
            finish();
        } else {
            FirebaseUtil.auth.signOut();
            finish();
            startActivity(getIntent());
            Intent startApp = new Intent(this, HomeActivity.class);
            this.startActivity(startApp);
            finish();
        }
    }
}