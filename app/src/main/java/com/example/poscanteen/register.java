package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    private TextView loginText;
    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput  = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerBtn);
        loginText = findViewById(R.id.logintxt);

        // On click, redirect to login page
        loginText.setOnClickListener(e -> {
            Intent intent = new Intent(register.this, MainActivity.class);
            startActivity(intent);
        });

        // On register button click
        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (validateInputs(username, email, password, confirmPassword)) {
                registerUser(username, email, password);
            }
        });
    }

    // Method to validate user inputs
    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty()) {
            usernameInput.setError("Username is required");
            return false;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    // Method to register user and insert data into Firestore
    private void registerUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful, store the username in Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            storeUserData(user.getUid(), username, email);
                        }
                    } else {
                        // Registration failed, show an error message
                        Toast.makeText(register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Method to store user data in Firestore
    private void storeUserData(String userId, String username, String email) {
        // Create a map for user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);

        // Store data in Firestore
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // User data stored successfully, navigate to another activity or show a success message
                    Toast.makeText(register.this, "User registered successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(register.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Failed to store user data
                    Toast.makeText(register.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}