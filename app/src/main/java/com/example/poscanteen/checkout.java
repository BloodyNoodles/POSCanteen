package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class checkout extends AppCompatActivity {

    AppCompatButton additionalBtn;
    AppCompatButton payBtn;
    ImageButton menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        // Initialize buttons
        additionalBtn = findViewById(R.id.additionalbtn);
        payBtn = findViewById(R.id.paybtn);
        menuBtn = findViewById(R.id.menubtn);

        // Handle window insets to adapt for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to add items
        additionalBtn.setOnClickListener(v -> {
            Intent intent = new Intent(checkout.this, home.class);
            startActivity(intent);
        });

        // Button to initiate payment
        payBtn.setOnClickListener(v -> {
            Intent intent = new Intent(checkout.this, transaction.class);
            startActivity(intent);
        });

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
    }

    @Override
    public void onBackPressed() {
        // Call the super method to ensure proper behavior
        super.onBackPressed();
    }
}
