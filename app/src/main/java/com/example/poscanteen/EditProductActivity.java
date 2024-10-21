package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends AppCompatActivity {

    private ImageButton cancelButton;
    private LinearLayout addonContainer, sizeContainer;
    private List<SizePricePair> sizePriceList = new ArrayList<>();
    private Button addAddonButton, addSizeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);

        // Initialize views
        cancelButton = findViewById(R.id.cancelButton);
        addonContainer = findViewById(R.id.addonsContainer);
        sizeContainer = findViewById(R.id.SizesandPriceContainer);
        addAddonButton = findViewById(R.id.addAddonButton);
        addSizeButton = findViewById(R.id.sizesAndPricesButton);

        // Set listeners
        addSizeButton.setOnClickListener(v ->  addSizeAndPriceFields());
        addAddonButton.setOnClickListener(v -> addAddonFields());

        cancelButton.setOnClickListener(v -> {
            // Navigate back to home screen
            Intent intent = new Intent(EditProductActivity.this, home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the current activity
        });

        // Add initial size and price fields
        addInitialSizeAndPriceFields();
    }

    // Add the initial size and price fields without a delete button
    private void addInitialSizeAndPriceFields() {
        LinearLayout sizePriceLayout = new LinearLayout(this);
        sizePriceLayout.setOrientation(LinearLayout.HORIZONTAL);
        sizePriceLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText sizeInput = new EditText(this);
        sizeInput.setHint("Size");
        sizeInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        EditText priceInput = new EditText(this);
        priceInput.setHint("Price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Add hidden delete button for layout consistency
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.delete);
        deleteButton.setBackground(null);  // Remove the default background
        deleteButton.setVisibility(View.INVISIBLE);  // Hide the button

        // Set button size for consistent layout
        int sizeInDp = 50;
        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(8, 8, 8, 8);
        deleteButton.setLayoutParams(params);

        // Add fields to layout
        sizePriceLayout.addView(sizeInput);
        sizePriceLayout.addView(priceInput);
        sizePriceLayout.addView(deleteButton);

        // Add layout to container
        sizeContainer.addView(sizePriceLayout);

        // Store reference to size and price pair
        sizePriceList.add(new SizePricePair(sizeInput, priceInput));
    }

    // Add dynamic add-on fields
    private void addAddonFields() {
        LinearLayout addonLayout = new LinearLayout(this);
        addonLayout.setOrientation(LinearLayout.HORIZONTAL);
        addonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText addonNameInput = new EditText(this);
        addonNameInput.setHint("Add-on Name");
        addonNameInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        EditText addonPriceInput = new EditText(this);
        addonPriceInput.setHint("Add-on Price");
        addonPriceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        addonPriceInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Add delete button
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.delete);
        deleteButton.setBackground(null);

        // Set the scale type to CENTER_INSIDE to prevent cropping
        deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Ensures the entire image fits inside the button

        int sizeInDp = 50;
        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(8, 8, 8, 8);
        deleteButton.setLayoutParams(params);

        // Handle delete action
        deleteButton.setOnClickListener(v -> addonContainer.removeView(addonLayout));

        addonLayout.addView(addonNameInput);
        addonLayout.addView(addonPriceInput);
        addonLayout.addView(deleteButton);
        addonContainer.addView(addonLayout);
    }

    // Add dynamic size and price fields
    private void addSizeAndPriceFields() {
        LinearLayout sizePriceLayout = new LinearLayout(this);
        sizePriceLayout.setOrientation(LinearLayout.HORIZONTAL);
        sizePriceLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText sizeInput = new EditText(this);
        sizeInput.setHint("Size");
        sizeInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        EditText priceInput = new EditText(this);
        priceInput.setHint("Price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Add delete button
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.delete);
        deleteButton.setBackground(null);

        // Set the scale type to CENTER_INSIDE to prevent cropping
        deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Ensures the entire image fits inside the button

        int sizeInDp = 50;
        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(8, 8, 8, 8);
        deleteButton.setLayoutParams(params);

        // Handle delete action
        deleteButton.setOnClickListener(v -> sizeContainer.removeView(sizePriceLayout));

        sizePriceLayout.addView(sizeInput);
        sizePriceLayout.addView(priceInput);
        sizePriceLayout.addView(deleteButton);
        sizeContainer.addView(sizePriceLayout);

        sizePriceList.add(new SizePricePair(sizeInput, priceInput));
    }


    // Internal class to handle size and price pair
    private static class SizePricePair {
        private final EditText sizeInput;
        private final EditText priceInput;

        public SizePricePair(EditText sizeInput, EditText priceInput) {
            this.sizeInput = sizeInput;
            this.priceInput = priceInput;
        }

        public String getSize() {
            return sizeInput.getText().toString().trim();
        }

        public String getPrice() {
            return priceInput.getText().toString().trim();
        }
    }
}
