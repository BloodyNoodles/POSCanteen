package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class transaction extends AppCompatActivity {
    private ImageButton exitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to a layout resource for the transaction activity
        setContentView(R.layout.transaction); // Make sure you have a corresponding layout XML file

        // Initialize the exit button
        exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(v -> {
            // Finish the current activity and go back to the previous one
            finish();
        });

    }
}
