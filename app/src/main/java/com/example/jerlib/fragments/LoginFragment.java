package com.example.jerlib.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.jerlib.R;
import com.example.jerlib.activities.HomeActivity;
import com.example.jerlib.utils.FirebaseUtil;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    FirebaseUser user = FirebaseUtil.auth.getCurrentUser();
                    assert user != null;
                    String uuid = user.getUid();
                    String name = user.getDisplayName();
                    String email = user.getEmail();

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("email", email);
                    userData.put("uuid", uuid);

                    Map<String, Object> shelfData = new HashMap<>();
                    shelfData.put("name", "Default Shelf");
                    shelfData.put("created_at", new Timestamp(new Date()));
                    shelfData.put("updated_at", new Timestamp(new Date()));
                    shelfData.put("entries", List.of());

                    FirebaseUtil.db().collection("Shelves")
                            .add(shelfData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("FIRESTORE", "Document update success.");
                                    String key = documentReference.getId();
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", name);
                                    userData.put("email", email);
                                    userData.put("uuid", uuid);
                                    userData.put("shelves", List.of(key));

                                    FirebaseUtil.db().collection("Users")
                                            .document(uuid)
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("FIRESTORE", "User data written successfully.");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("FIRESTORE", "Failed to write user data.", e);
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FIRESTORE", "Document update failed.");
                                }
                            });

                    Intent startApp = new Intent(getContext(), HomeActivity.class);
                    requireContext().startActivity(startApp);
                    requireActivity().finish();
                }
            }
    );

    public LoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        EditText emailET = view.findViewById(R.id.loginEmailET);
        EditText passwordET = view.findViewById(R.id.loginPasswordET);
        Button loginButton = view.findViewById(R.id.loginLoginBtn);
        TextView error = view.findViewById(R.id.loginErrorTV);
        SignInButton googleBtn = view.findViewById(R.id.loginGoogleBtn);

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = List.of(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        googleBtn.setOnClickListener(v -> {
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();
            signInLauncher.launch(signInIntent);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                error.setText("Password and email cannot be empty!");
            }
            else {
                FirebaseUtil.auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(view.getContext().getMainExecutor(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("FIREBASE", "signInWithEmail:success");
                                    Intent startApp = new Intent(view.getContext(), HomeActivity.class);
                                    view.getContext().startActivity(startApp);
                                    requireActivity().finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("FIREBASE", "signInWithEmail:failure", task.getException());
                                    error.setText("Invalid email/password combination.");
                                }
                            }
                        });
            }
        });

        return view;
    }
}