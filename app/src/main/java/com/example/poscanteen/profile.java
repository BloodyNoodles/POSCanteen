package com.example.poscanteen;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profile extends AppCompatActivity {
    private ImageButton menuBtn;
    private EditText emailEdit, currentPasswordEdit, newPasswordEdit, confirmPasswordEdit;
    private Button updateBtn, cancelBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        menuBtn = findViewById(R.id.menubtn);
        emailEdit = findViewById(R.id.emailEdit);
        currentPasswordEdit = findViewById(R.id.currentPasswordEdit);
        newPasswordEdit = findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        updateBtn = findViewById(R.id.update_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        // Handle window insets to adapt for system bars
        View profileView = findViewById(R.id.profile);
        if (profileView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(profileView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Add the side menu fragment to the activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sideMenus, new SideMenuFragment()) // Replace with the fragment container ID
                    .commit();
        }

        // Toggle the side menu when the menu button is clicked
        menuBtn.setOnClickListener(v -> {
            SideMenuFragment fragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.sideMenus);
            if (fragment != null) {
                fragment.toggleSideMenu();
            }
        });

        // Fetch current user's email
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                emailEdit.setText(email);
                emailEdit.setFocusable(false); // Make email non-editable
                emailEdit.setClickable(false);
            }
        }

        // Update button click listener
        updateBtn.setOnClickListener(v -> updatePassword());

        // Cancel button click listener
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void updatePassword() {
        String currentPassword = currentPasswordEdit.getText().toString().trim();
        String newPassword = newPasswordEdit.getText().toString().trim();
        String confirmPassword = confirmPasswordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(profile.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(profile.this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

            // Re-authenticate the user
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update the password
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(profile.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Log.e("profile", "Error updating password", updateTask.getException());
                                            Toast.makeText(profile.this, "Error updating password", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.e("profile", "Error re-authenticating", task.getException());
                            Toast.makeText(profile.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
