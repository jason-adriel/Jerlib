package com.example.jerlib.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jerlib.R;
import com.example.jerlib.fragments.HomeFragment;
import com.example.jerlib.fragments.ProfileFragment;
import com.example.jerlib.fragments.SearchFragment;

public class HomeActivity extends AppCompatActivity {

    ImageView homeHomeIcon, homeSearchIcon, homeProfileIcon;
    final Fragment homeFragment = new HomeFragment();
    final Fragment searchFragment = new SearchFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager manager = getSupportFragmentManager();
    Fragment active;
    ImageView activeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scopusDetailsAuthorTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initial config
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.homeFragmentContainer, homeFragment);
        transaction.add(R.id.homeFragmentContainer, searchFragment).hide(searchFragment);
        transaction.add(R.id.homeFragmentContainer, profileFragment).hide(profileFragment);
        transaction.commit();
        active = homeFragment;

        homeHomeIcon = findViewById(R.id.homeHomeIcon);
        homeSearchIcon = findViewById(R.id.homeSearchIcon);
        homeProfileIcon = findViewById(R.id.homeUserIcon);
        activeIcon = homeHomeIcon;
        homeHomeIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.blue)));

        homeHomeIcon.setOnClickListener(v -> {
            loadFragment(homeFragment);
            homeHomeIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.blue)));
            activeIcon = homeHomeIcon;
        });

        homeSearchIcon.setOnClickListener(v -> {
            loadFragment(searchFragment);
            homeSearchIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.blue)));
            activeIcon = homeSearchIcon;
        });

        homeProfileIcon.setOnClickListener(v -> {
            loadFragment(profileFragment);
            homeProfileIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.blue)));
            activeIcon = homeProfileIcon;
        });
    }

    private void loadFragment(Fragment fg){
        if (active == fg) {
            return;
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.show(fg);
        transaction.hide(active);
        active = fg;
        activeIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
        transaction.commit();
    }
}