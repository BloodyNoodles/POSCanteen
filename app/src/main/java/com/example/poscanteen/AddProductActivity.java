package com.example.poscanteen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Code for picking images
    private Uri imageUri; // To hold the image URI

    private ImageButton menuBtn; // Image button for selecting images
    private LinearLayout addonContainer, sizeContainer; // Initialize addonContainer
    private Button addAddonButton, addSizeButton, addButton, cancelButton, imageButton;

    private EditText productName, sellingPrice, description;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the layout for this activity
        setContentView(R.layout.add_product);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get current user
        currentUser = mAuth.getCurrentUser();

        // Initialize views after setting the content view
        menuBtn = findViewById(R.id.menubtn);
        addonContainer = findViewById(R.id.addonContainer); // Make sure the addonContainer ID matches your XML
        addAddonButton = findViewById(R.id.addAddonButton);
        addSizeButton = findViewById(R.id.addSizeButton); // Initialize addSizeButton
        sizeContainer = findViewById(R.id.sizeContainer);

        // Initialize buttons
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);
        imageButton = findViewById(R.id.imageButton);

        // Set the click listener for the add button
        addButton.setOnClickListener(v -> saveProduct());

        // Set the click listener for the addAddonButton to add new add-ons dynamically
        addAddonButton.setOnClickListener(v -> addNewAddonField());

        // Set the click listener for the addSizeButton to add new sizes dynamically
        addSizeButton.setOnClickListener(v -> addNewSizeField());

        // Set click listener for the cancel button
        cancelButton.setOnClickListener(v -> {
            finish(); // This will take you back to the previous activity
        });

        // Set click listener for image selection
        imageButton.setOnClickListener(v -> openFileChooser());

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

    // Method to open file chooser for images
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Display the selected image in the ImageView
            ImageView selectedImageView = findViewById(R.id.selectedImageView);
            selectedImageView.setVisibility(View.VISIBLE); // Make it visible
            selectedImageView.setImageURI(imageUri); // Set the image URI
        }
    }

    // FOR ADD-ON FUNCTION (CLASS) ONLY
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

    // FUNCTION TO ADD SIZE
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

    // Method to save the product
    private void saveProduct() {
        currentUser = mAuth.getCurrentUser(); // Ensure this is updated
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String enteredProductName = ((EditText) findViewById(R.id.productName)).getText().toString().trim();
            String sellingPrice = ((EditText) findViewById(R.id.sellingPriceInput)).getText().toString().trim();
            String description = ((EditText) findViewById(R.id.descriptionInput)).getText().toString().trim();

            // Get selected category from RadioGroup
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String category = "";
            if (selectedId != -1) { // Check if any radio button is selected
                RadioButton selectedRadioButton = findViewById(selectedId);
                category = selectedRadioButton.getText().toString();
            }

            // Validate inputs before saving
            if (enteredProductName.isEmpty() || sellingPrice.isEmpty() || description.isEmpty() || category.isEmpty()) {
                Toast.makeText(AddProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a product object
            Map<String, Object> product = new HashMap<>();
            product.put("name", enteredProductName);
            product.put("category", category);
            product.put("price", sellingPrice);
            product.put("description", description);

            // Check if an image is selected and upload it
            if (imageUri != null) {
                uploadImage(userId, product);
            } else {
                // Save product without image if none is selected
                saveProductToFirestore(userId, product);
            }
        } else {
            Toast.makeText(AddProductActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to upload image to Firebase Storage
    private void uploadImage(String userId, Map<String, Object> product) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + userId + "/" + System.currentTimeMillis() + ".jpg");

        // Upload the image
        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Add the image URL to the product map
                product.put("imageUrl", uri.toString());

                // Display the uploaded image
                ImageView selectedImageView = findViewById(R.id.selectedImageView);
                selectedImageView.setVisibility(View.VISIBLE); // Make it visible
                Glide.with(this).load(uri.toString()).into(selectedImageView); // Load the image using Glide or any image loading library

                // Save product to Firestore
                saveProductToFirestore(userId, product);
            }).addOnFailureListener(e -> {
                Toast.makeText(AddProductActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(AddProductActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
        });
    }


    // Method to save the product to Firestore
    private void saveProductToFirestore(String userId, Map<String, Object> product) {
        db.collection("users").document(userId).collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    // Clear all fields after successfully adding the product
                    clearFields();
                    Toast.makeText(AddProductActivity.this, "Product added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to clear all input fields
    private void clearFields() {
        ((EditText) findViewById(R.id.productName)).setText("");
        ((EditText) findViewById(R.id.sellingPriceInput)).setText("");
        ((EditText) findViewById(R.id.descriptionInput)).setText("");
        addonContainer.removeAllViews(); // Clear add-ons
        sizeContainer.removeAllViews(); // Clear sizes
        imageUri = null; // Reset image URI
        // You can reset any other fields or states as necessary
    }
}
