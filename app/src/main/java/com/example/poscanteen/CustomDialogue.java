package com.example.poscanteen;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CustomDialogue extends Dialog {

    private Context context;
    private TextView quantityValue;
    private int quantity = 1; // Default quantity

    // Update the constructor to accept productId
    public CustomDialogue(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_order);

        LinearLayout addsOnContainer = findViewById(R.id.addsOnContainer);
        LinearLayout sizesContainer = findViewById(R.id.sizesContainer);
        Button confirmButton = findViewById(R.id.button_confirm);
        Button cancelButton = findViewById(R.id.button_cancel);
        Button decreaseQuantity = findViewById(R.id.decrease_quantity);
        Button increaseQuantity = findViewById(R.id.increase_quantity);
        quantityValue = findViewById(R.id.quantity_value);

        // Update the quantity display
        quantityValue.setText(String.valueOf(quantity));

        // Set click listeners for quantity buttons
        decreaseQuantity.setOnClickListener(v -> {
            if (quantity > 1) { // Prevent quantity from going below 1
                quantity--;
                quantityValue.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });

        increaseQuantity.setOnClickListener(v -> {
            quantity++;
            quantityValue.setText(String.valueOf(quantity));
        });

        fetchProductDetails(addsOnContainer, sizesContainer);

        confirmButton.setOnClickListener(v -> dismiss());
        cancelButton.setOnClickListener(v -> dismiss());
    }


    private void fetchProductDetails(LinearLayout addsOnContainer, LinearLayout sizesContainer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Fetching product details logic here (if needed)
    }
}
