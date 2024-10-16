package com.example.poscanteen;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;

public class Addprofile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.add_profile);

        // Find the EditText by its ID
        EditText emailEditText = findViewById(R.id.emailEdit);

        // Get the current user from Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Check if a user is logged in and retrieve the email
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                // Set the email in the EditText
                emailEditText.setText(email);
            }
        } else {
            // Handle the case where no user is logged in
            emailEditText.setText("No user is logged in");
        }

    }
}
