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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.jerlib.R;
import com.example.jerlib.activities.HomeActivity;
import com.example.jerlib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

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