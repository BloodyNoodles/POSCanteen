package com.example.poscanteen;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.widget.Button; // Add this import
import com.google.android.material.textview.MaterialTextView;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton menuBtn;
    private int addonCount = 0; // Initialize addonCount
    private LinearLayout addonContainer, sizeContainer; // Initialize addonContainer
    private Button addAddonButton, addSizeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the layout for this activity
        setContentView(R.layout.add_product);

        // Initialize views after setting the content view
        menuBtn = findViewById(R.id.menubtn);
        addonContainer = findViewById(R.id.addonContainer); // Make sure the addonContainer ID matches your XML
        addonContainer = findViewById(R.id.addonContainer); // Make sure the addonContainer ID matches your XML
        addAddonButton = findViewById(R.id.addAddonButton);
        addSizeButton = findViewById(R.id.addSizeButton); // Initialize addSizeButton

        // Initialize sizeContainer
        sizeContainer = findViewById(R.id.sizeContainer);

        // Set the click listener for the addAddonButton to add new add-ons dynamically
        addAddonButton.setOnClickListener(v -> addNewAddonField());

        // Set the click listener for the addSizeButton to add new sizes dynamically
        addSizeButton.setOnClickListener(v -> addNewSizeField());


        // Add the fragment to the activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sideMenus, new SideMenuFragment())
                    .commit();
        }

        // Toggle the side menu when the menu button is clicked
        menuBtn.setOnClickListener(v -> {
            SideMenuFragment fragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.sideMenus);
            if (fragment != null) {
                fragment.toggleSideMenu(); // Ensure the fragment is properly initialized before toggling
            }
        });
    }



    // FOR ADDS ON FUNCTION (CLASS) ONLY
    private void addNewAddonField() {
        // Create a new horizontal LinearLayout to hold both the EditText and the Button
        LinearLayout addonLayout = new LinearLayout(this);
        addonLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);
        addonLayout.setLayoutParams(layoutParams);

        // Create new EditText dynamically
        EditText newAddonField = new EditText(this);
        newAddonField.setHint("Enter an add-on here");  // Set a generic hint
        newAddonField.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f); // Weight of 1 to take up available space
        newAddonField.setLayoutParams(editTextParams);

        // Set border background if needed
        newAddonField.setBackground(ContextCompat.getDrawable(this, R.drawable.quantity_border));

        // Create a "Remove" button
        Button removeButton = new Button(this);
        removeButton.setText("Remove");

        // Set background color to blue (you can use your own color resource)
        removeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));

        removeButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));  // Make text white
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        removeButton.setLayoutParams(buttonParams);

        // Set click listener to remove the addonLayout when "Remove" button is clicked
        removeButton.setOnClickListener(v -> addonContainer.removeView(addonLayout));

        // Add the EditText and Button to the horizontal LinearLayout
        addonLayout.addView(newAddonField);
        addonLayout.addView(removeButton);

        // Add the horizontal LinearLayout to the container
        addonContainer.addView(addonLayout);
    }

    //FUMCTION NG ADD SIZE

    private void addNewSizeField() {
        // Create a new horizontal LinearLayout to hold both the EditText and the Button
        LinearLayout sizeLayout = new LinearLayout(this);
        sizeLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);
        sizeLayout.setLayoutParams(layoutParams);

        // Create new EditText dynamically for size
        EditText newSizeField = new EditText(this);
        newSizeField.setHint("Enter size");  // Set a generic hint
        newSizeField.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f); // Weight of 1 to take up available space
        newSizeField.setLayoutParams(editTextParams);

        // Set border background if needed
        newSizeField.setBackground(ContextCompat.getDrawable(this, R.drawable.quantity_border));

        // Create a "Remove" button
        Button removeButton = new Button(this);
        removeButton.setText("Remove");

        // Set background color to blue (you can use your own color resource)
        removeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));

        removeButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));  // Make text white
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        removeButton.setLayoutParams(buttonParams);

        // Set click listener to remove the sizeLayout when "Remove" button is clicked
        removeButton.setOnClickListener(v -> sizeContainer.removeView(sizeLayout));

        // Add the EditText and Button to the horizontal LinearLayout
        sizeLayout.addView(newSizeField);
        sizeLayout.addView(removeButton);

        // Add the horizontal LinearLayout to the container
        sizeContainer.addView(sizeLayout);
    }


}
