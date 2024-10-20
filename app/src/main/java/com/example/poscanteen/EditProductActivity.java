package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class EditProductActivity extends AppCompatActivity {

    private ImageButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);

        // Initialize the cancel button
        cancelButton = findViewById(R.id.cancelButton);

        // Set click listener for cancel button
        cancelButton.setOnClickListener(v -> {
            // Navigate back to MainActivity (home screen)
            Intent intent = new Intent(EditProductActivity.this, home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the current activity
        });
    }
}
