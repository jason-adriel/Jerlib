package com.example.jerlib.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static final FirebaseAuth auth = FirebaseAuth.getInstance();
    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }
}
