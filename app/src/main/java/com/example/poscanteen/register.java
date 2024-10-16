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
    private EditText emailInput, passwordInput, confirmPasswordInput;
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
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
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
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (validateInputs(email, password, confirmPassword)) {
                createAccount(email, password);
            }
        });
    }

    // Method to validate user inputs
    private boolean validateInputs(String email, String password, String confirmPassword) {

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
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User is successfully created
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sendVerificationEmail(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Email sent successfully, notify the user
                        Toast.makeText(register.this,
                                "Verification email sent to " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If email fails to send, handle the error
                        Toast.makeText(register.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // Email is verified, proceed to the main activity
                            startActivity(new Intent(register.this, MainActivity.class));
                            finish();
                        } else {
                            // Email not verified, prompt the user to verify their email
                            Toast.makeText(register.this,
                                    "Please verify your email before logging in.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    FirebaseAuth.AuthStateListener authListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (user.isEmailVerified()) {
                    // The user's email is verified, now you can allow login
                    // or redirect them to the main app screen
                }
            });
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    private void resendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(register.this,
                        "Verification email resent.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(register.this,
                        "Failed to resend verification email.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to store user data in Firestore
    private void storeUserData(String userId, String email, String password) {
        // Create a map for user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);

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
