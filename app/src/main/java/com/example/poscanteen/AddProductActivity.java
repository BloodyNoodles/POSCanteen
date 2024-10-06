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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // Initialize views
        menuBtn = findViewById(R.id.menubtn);
        addonContainer = findViewById(R.id.addonContainer);
        sizeContainer = findViewById(R.id.sizeContainer);
        addAddonButton = findViewById(R.id.addAddonButton);
        addSizeButton = findViewById(R.id.addSizeButton);
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);
        imageButton = findViewById(R.id.imageButton);

        // Initialize EditText fields
        productName = findViewById(R.id.productName);
        sellingPrice = findViewById(R.id.sellingPriceInput);
        description = findViewById(R.id.descriptionInput);

        // Set the click listener for the add button
        addButton.setOnClickListener(v -> saveProduct());

        // Set the click listener for the addAddonButton to add new add-ons dynamically
        addAddonButton.setOnClickListener(v -> addNewAddonField());

        // Set the click listener for the addSizeButton to add new sizes dynamically
        addSizeButton.setOnClickListener(v -> addNewSizeField());

        // Set click listener for the cancel button
        cancelButton.setOnClickListener(v -> finish());

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
                fragment.toggleSideMenu();
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
            selectedImageView.setVisibility(View.VISIBLE);
            selectedImageView.setImageURI(imageUri);
        }
    }

    // Method to add new add-on field
    private void addNewAddonField() {
        LinearLayout addonLayout = new LinearLayout(this);
        addonLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);
        addonLayout.setLayoutParams(layoutParams);

        EditText newAddonField = new EditText(this);
        newAddonField.setHint("Enter an add-on here");
        newAddonField.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f);
        newAddonField.setLayoutParams(editTextParams);
        newAddonField.setBackground(ContextCompat.getDrawable(this, R.drawable.quantity_border));

        Button removeButton = new Button(this);
        removeButton.setText("Remove");
        removeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        removeButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        removeButton.setLayoutParams(buttonParams);

        removeButton.setOnClickListener(v -> addonContainer.removeView(addonLayout));

        addonLayout.addView(newAddonField);
        addonLayout.addView(removeButton);
        addonContainer.addView(addonLayout);
    }

    // Method to add new size field
    private void addNewSizeField() {
        LinearLayout sizeLayout = new LinearLayout(this);
        sizeLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);
        sizeLayout.setLayoutParams(layoutParams);

        EditText newSizeField = new EditText(this);
        newSizeField.setHint("Enter size");
        newSizeField.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f);
        newSizeField.setLayoutParams(editTextParams);
        newSizeField.setBackground(ContextCompat.getDrawable(this, R.drawable.quantity_border));

        Button removeButton = new Button(this);
        removeButton.setText("Remove");
        removeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        removeButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        removeButton.setLayoutParams(buttonParams);

        removeButton.setOnClickListener(v -> sizeContainer.removeView(sizeLayout));

        sizeLayout.addView(newSizeField);
        sizeLayout.addView(removeButton);
        sizeContainer.addView(sizeLayout);
    }

    // Method to save the product
    private void saveProduct() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String enteredProductName = productName.getText().toString().trim();
            String sellingPriceStr = sellingPrice.getText().toString().trim();
            String descriptionStr = description.getText().toString().trim();

            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String category = "";
            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                category = selectedRadioButton.getText().toString();
            }

            // Validate inputs before saving
            if (enteredProductName.isEmpty() || sellingPriceStr.isEmpty() || descriptionStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(AddProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the add-ons
            List<String> addOns = new ArrayList<>();
            for (int i = 0; i < addonContainer.getChildCount(); i++) {
                LinearLayout addonLayout = (LinearLayout) addonContainer.getChildAt(i);
                EditText addonInput = (EditText) addonLayout.getChildAt(0);
                String addonText = addonInput.getText().toString().trim();
                if (!addonText.isEmpty()) {
                    addOns.add(addonText);
                }
            }

            // Get the sizes
            List<String> sizes = new ArrayList<>();
            for (int i = 0; i < sizeContainer.getChildCount(); i++) {
                LinearLayout sizeLayout = (LinearLayout) sizeContainer.getChildAt(i);
                EditText sizeInput = (EditText) sizeLayout.getChildAt(0);
                String sizeText = sizeInput.getText().toString().trim();
                if (!sizeText.isEmpty()) {
                    sizes.add(sizeText);
                }
            }

            // Create a product object
            Map<String, Object> product = new HashMap<>();
            product.put("name", enteredProductName);
            product.put("category", category);
            product.put("price", sellingPriceStr);
            product.put("description", descriptionStr);
            product.put("addOns", addOns);
            product.put("sizes", sizes);

            // Check if an image is selected and upload it
            if (imageUri != null) {
                uploadImage(userId, product);
            } else {
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

        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                product.put("imageUrl", uri.toString());
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
                    clearFields();
                    Toast.makeText(AddProductActivity.this, "Product added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to clear all input fields
    private void clearFields() {
        productName.setText("");
        sellingPrice.setText("");
        description.setText("");
        addonContainer.removeAllViews();
        sizeContainer.removeAllViews();
        imageUri = null;
        ImageView selectedImageView = findViewById(R.id.selectedImageView);
        selectedImageView.setVisibility(View.GONE); // Hide the image view after clearing
    }
}
