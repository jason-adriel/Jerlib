package com.example.jerlib.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jerlib.R;
import com.example.jerlib.activities.EditProfileActivity;
import com.example.jerlib.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {
    String TAG = "ProfileFragment";
    TextView profileNameTV, profileEmailTV, profileJoinedTV;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    Button profileEditBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = FirebaseUtil.auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileNameTV = view.findViewById(R.id.profileNameTV);
        profileEmailTV = view.findViewById(R.id.profileEmailTV);
        profileJoinedTV = view.findViewById(R.id.profileJoinedTV);
        profileEditBtn = view.findViewById(R.id.profileEditBtn);

        if (currentUser != null) {
            String email = currentUser.getEmail();
            profileEmailTV.setText(email);
            fetchUserDataByEmail(email); // Query user data by email
        }

        profileEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditProfileActivity.class);
            intent.putExtra("userId", currentUser.getUid());
            intent.putExtra("userEmail", profileEmailTV.getText());
            intent.putExtra("userName", profileNameTV.getText());
            intent.putExtra("userJoined", profileJoinedTV.getText());

            startActivity(intent);
        });

        return view;
    }

    private void fetchUserDataByEmail(String email) {
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String name = document.getString("name");
                        String joinedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                                .format(document.getDate("created_at"));

                        Log.d(TAG, "Name: " + (name != null ? name : "Name not available"));
                        Log.d(TAG, "Joined Date: " + (joinedDate != null ? joinedDate : "Joined date not available"));

                        // Update UI
                        profileNameTV.setText(name != null ? name : "Name not available");
                        profileJoinedTV.setText(joinedDate != null ? joinedDate : "Joined date not available");
                    }
                } else {
                    Log.d(TAG, "No user found with the email: " + email);
                }
            })
            .addOnFailureListener(e -> Log.e(TAG, "Error fetching user data", e));
    }

}