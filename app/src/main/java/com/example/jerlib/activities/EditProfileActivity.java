package com.example.jerlib.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jerlib.R;
import com.example.jerlib.utils.FirebaseUtil;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    Button editSaveBtn, editCancelBtn, confirmConfirmBtn;
    EditText editProfileNameET, editProfileEmailET, editProfilePasswordET, confirmPasswordET;
    TextView editProfileErrorTV, confirmErrorTV;
    String editName, editEmail, editPassword;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userEmail = intent.getStringExtra("userEmail");
        String userName = intent.getStringExtra("userName");
        Dialog confirmDialog = new Dialog(EditProfileActivity.this);
        confirmDialog.setContentView(R.layout.credentials_confirmation_dialog);

        editProfileNameET = findViewById(R.id.editProfileNameET);
        editProfileEmailET = findViewById(R.id.editProfileEmailET);
        editProfilePasswordET = findViewById(R.id.editProfilePasswordET);
        editProfileErrorTV = findViewById(R.id.editProfileErrorTV);

        editProfileNameET.setText(userName);
        editProfileEmailET.setText(userEmail);

        editName = editProfileNameET.getText().toString();
        editEmail = editProfileEmailET.getText().toString();
        editPassword = editProfilePasswordET.getText().toString();

        confirmConfirmBtn = confirmDialog.findViewById(R.id.confirmConfirmBtn);
        confirmPasswordET = confirmDialog.findViewById(R.id.confirmPasswordET);
        confirmErrorTV = confirmDialog.findViewById(R.id.confirmErrorTV);

        editSaveBtn = findViewById(R.id.editSaveBtn);
        editSaveBtn.setOnClickListener(v -> {
            editName = editProfileNameET.getText().toString();
            editEmail = editProfileEmailET.getText().toString();
            editPassword = editProfilePasswordET.getText().toString();
            if ((editName.isEmpty() || editName.equals(userName)) && (editEmail.isEmpty() || editEmail.equals(userEmail)) && editPassword.isEmpty()) {
                editProfileErrorTV.setText("One field must not be empty to change the profile Empty fields will NOT update that field.");
            } else if (!editPassword.isEmpty() && editPassword.length() < 6) {
                editProfileErrorTV.setText("Password must be at least 6 characters long.");
            }else {
                Log.d("CONFIRM", "Showed confirm dialog.");
                confirmDialog.show();
            }
        });

        editCancelBtn = findViewById(R.id.editCancelBtn);
        editCancelBtn.setOnClickListener(v -> {
            finish();
        });

        confirmConfirmBtn.setOnClickListener(v -> {
            String currentPassword = confirmPasswordET.getText().toString();
            if (currentPassword.isEmpty()) {
                confirmErrorTV.setText("Current password cannot be empty.");
            } else {
                assert userEmail != null;
                FirebaseUtil.auth.signInWithEmailAndPassword(userEmail, currentPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (!editName.equals(userName)) {
                                    // Update the display name
                                    FirebaseFirestore db = FirebaseUtil.db();
                                    assert userId != null;
                                    DocumentReference userRef = db.collection("Users").document(userId);

                                    Map<String, Object> updatedFields = new HashMap<>();
                                    updatedFields.put("name", editName);  // Update the "name" field with the new name
                                    userRef.update(updatedFields)
                                            .addOnCompleteListener(t -> {
                                                if (t.isSuccessful()) {
                                                    Log.d("Firestore Update", "Name updated successfully");
                                                } else {
                                                    Log.e("Firestore Update", "Failed to update name" + t.getException());

                                                }
                                            });
                                }
                                if (!editEmail.equals(userEmail)) {
                                    // Update the email
                                    FirebaseUtil.auth.getCurrentUser().updateEmail(editEmail)
                                            .addOnCompleteListener(emailTask -> {
                                                if (emailTask.isSuccessful()) {
                                                    Log.d("Email Update", "Email updated successfully");
                                                    FirebaseFirestore db = FirebaseUtil.db();
                                                    assert userId != null;
                                                    DocumentReference userRef = db.collection("Users").document(userId);

                                                    Map<String, Object> updatedFields = new HashMap<>();
                                                    updatedFields.put("email", editEmail);  // Update the "email" field with the new email
                                                    userRef.update(updatedFields)
                                                            .addOnCompleteListener(t -> {
                                                                if (t.isSuccessful()) {
                                                                    Log.d("Firestore Update", "Email updated successfully");
                                                                } else {
                                                                    Log.e("Firestore Update", "Failed to update email" + t.getException());
                                                                }
                                                            });
                                                } else {
                                                    Log.e("Email Update", "Failed to update email" + emailTask.getException());
                                                }
                                            });
                                }
                                else if (!editPassword.isEmpty()) {
                                    // Update the password
                                    FirebaseUtil.auth.signInWithEmailAndPassword(!editEmail.isEmpty() ? editEmail : userEmail,  editPassword);
                                    FirebaseUtil.auth.getCurrentUser().updatePassword(editPassword)
                                            .addOnCompleteListener(passwordTask -> {
                                                if (passwordTask.isSuccessful()) {
                                                    Log.d("Password Update", "Password updated successfully");
                                                    FirebaseUtil.auth.signInWithEmailAndPassword(userEmail, editPassword);
                                                } else {
                                                    Log.e("Password Update", "Failed to update password" + passwordTask.getException());
                                                }
                                            });
                                }

                                // After all updates, dismiss the dialog and show a success message or update UI
                                confirmDialog.dismiss();
                                FirebaseUtil.auth.signOut();
                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                Intent reloadHome = new Intent(EditProfileActivity.this, AuthActivity.class);
                                reloadHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(reloadHome);
                                finish();
                            } else {
                                // If password verification fails
                                confirmErrorTV.setText("Incorrect current password.");
                            }
                        });
            }
        });

        if (Objects.requireNonNull(FirebaseUtil.auth.getCurrentUser()).getDisplayName() != null) {
            editProfileEmailET.setEnabled(false);
            editProfilePasswordET.setEnabled(false);
            editProfileNameET.setEnabled(false);
            editSaveBtn.setEnabled(false);
            TextView editProfileConfirmTV = findViewById(R.id.editProfileConfirmTV);
            editProfileConfirmTV.setText("Profile cannot be changed with Google sign in.");
        }

    }
}