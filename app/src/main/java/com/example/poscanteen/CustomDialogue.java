package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CustomDialogue extends AppCompatActivity {

    private int quantity = 1;  // Initial quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        Button buttonGoBack = findViewById(R.id.button_confirm);
        Button increaseQuantity = findViewById(R.id.increase_quantity);
        Button decreaseQuantity = findViewById(R.id.decrease_quantity);
        TextView quantityValue = findViewById(R.id.quantity_value);

        // Update the quantity TextView initially
        quantityValue.setText(String.valueOf(quantity));

        // Increase button listener
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityValue.setText(String.valueOf(quantity));
            }
        });

        // Decrease button listener
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    quantityValue.setText(String.valueOf(quantity));
                }
            }
        });

        // Set up the button to go back to MainActivity
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close this activity and go back to MainActivity
                finish();
            }
        });
    }
}
