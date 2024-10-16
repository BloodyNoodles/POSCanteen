package com.example.poscanteen;

import android.app.Activity;
import android.content.Intent;
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
    private LinearLayout addonContainer, sizeContainer; // Initialize addonContainer and sizeContainer
    private Button addAddonButton, addSizeButton, addButton, cancelButton, imageButton;
    private EditText productName, description;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private List<SizePricePair> sizePriceList = new ArrayList<>(); // To store size and price pairs

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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

        productName = findViewById(R.id.productName);
        description = findViewById(R.id.descriptionInput);

        // Set click listeners
        addButton.setOnClickListener(v -> saveProduct());
        addAddonButton.setOnClickListener(v -> addNewAddonField());
        addSizeButton.setOnClickListener(v -> addSizeAndPriceFields());
        cancelButton.setOnClickListener(v -> finish());
        imageButton.setOnClickListener(v -> openFileChooser());
        addAddonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddonFields();  // Call the method to add add-ons dynamically
            }
        });


        // Handle side menu toggle
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sideMenus, new SideMenuFragment())
                    .commit();
        }

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

    // Optional helper class to store addon name and price inputs
    private class AddonPair {
        private EditText addonNameInput;
        private EditText addonPriceInput;

        public AddonPair(EditText addonNameInput, EditText addonPriceInput) {
            this.addonNameInput = addonNameInput;
            this.addonPriceInput = addonPriceInput;
        }

        public String getAddonName() {
            return addonNameInput.getText().toString().trim();
        }

        public String getAddonPrice() {
            return addonPriceInput.getText().toString().trim();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
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
        newAddonField.setPadding(50, 16, 16, 16);
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
    // Method to dynamically add add-on name and price fields
    private void addAddonFields() {
        // Create a new horizontal LinearLayout to hold the add-on name and price fields
        LinearLayout addonLayout = new LinearLayout(this);
        addonLayout.setOrientation(LinearLayout.HORIZONTAL);
        addonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create an EditText for the add-on name
        EditText addonNameInput = new EditText(this);
        addonNameInput.setHint("Add-on Name");
        addonNameInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 for equal width

        // Create an EditText for the add-on price
        EditText addonPriceInput = new EditText(this);
        addonPriceInput.setHint("Add-on Price");
        addonPriceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); // For decimal input
        addonPriceInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 for equal width

        // Add the EditText fields to the horizontal LinearLayout
        addonLayout.addView(addonNameInput);
        addonLayout.addView(addonPriceInput);

        // Add the horizontal layout to the main container (addonContainer)
        addonContainer.addView(addonLayout);

        // You can also add the add-on fields to a list if you want to save the inputs later
        // addonList.add(new AddonPair(addonNameInput, addonPriceInput)); // (Optional for later use)
    }

    // Method to add size and price input fields dynamically
    private void addSizeAndPriceFields() {
        // Create a new horizontal LinearLayout to hold the size and price fields
        LinearLayout sizePriceLayout = new LinearLayout(this);
        sizePriceLayout.setOrientation(LinearLayout.HORIZONTAL);
        sizePriceLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create an EditText for size
        EditText sizeInput = new EditText(this);
        sizeInput.setHint("Size");
        sizeInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 for equal width

        // Create an EditText for price
        EditText priceInput = new EditText(this);
        priceInput.setHint("Price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); // For decimal input
        priceInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 for equal width

        // Add the new EditText views to the horizontal LinearLayout
        sizePriceLayout.addView(sizeInput);
        sizePriceLayout.addView(priceInput);

        // Add the horizontal layout to the main container
        sizeContainer.addView(sizePriceLayout);

        // Store size and price values on input changes or when saving the product
        sizePriceList.add(new SizePricePair(sizeInput, priceInput)); // Add size-price pair to the list
    }

    // Method to save the product
    private void saveProduct() {
        // Get current authenticated user
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get user ID
            String enteredProductName = productName.getText().toString().trim(); // Get product name
            String descriptionStr = description.getText().toString().trim(); // Get description

            // Get selected category from the RadioGroup
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String category = "";
            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                category = selectedRadioButton.getText().toString();
            }

            // Validate mandatory inputs (product name, description, category)
            if (enteredProductName.isEmpty() || descriptionStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(AddProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the add-ons (loop through dynamically added fields)
            List<Map<String, String>> addOns = new ArrayList<>();
            for (int i = 0; i < addonContainer.getChildCount(); i++) {
                LinearLayout addonLayout = (LinearLayout) addonContainer.getChildAt(i);
                EditText addonNameInput = (EditText) addonLayout.getChildAt(0);
                EditText addonPriceInput = (EditText) addonLayout.getChildAt(1);
                String addonName = addonNameInput.getText().toString().trim();
                String addonPrice = addonPriceInput.getText().toString().trim();

                if (!addonName.isEmpty() && !addonPrice.isEmpty()) {
                    Map<String, String> addonData = new HashMap<>();
                    addonData.put("addonName", addonName);
                    addonData.put("addonPrice", addonPrice);
                    addOns.add(addonData); // Add the name and price as a map
                }
            }

            // Get the sizes and prices (from SizePricePair list)
            List<Map<String, String>> sizesAndPrices = new ArrayList<>();
            for (SizePricePair pair : sizePriceList) {
                String size = pair.getSize();
                String price = pair.getPrice();
                if (!size.isEmpty() && !price.isEmpty()) {
                    Map<String, String> sizePriceMap = new HashMap<>();
                    sizePriceMap.put("size", size);
                    sizePriceMap.put("price", price);
                    sizesAndPrices.add(sizePriceMap); // Add the size and price as a map
                }
            }

            // Create the product data map to be saved in Firestore
            Map<String, Object> productData = new HashMap<>();
            productData.put("productName", enteredProductName); // Product name
            productData.put("description", descriptionStr);     // Product description
            productData.put("category", category);              // Product category
            productData.put("addons", addOns);                  // Add-ons (name and price)
            productData.put("sizesAndPrices", sizesAndPrices);  // Sizes and prices
            productData.put("userId", userId);                  // User ID (current user)

            // Save the product data to Firestore
            db.collection("products")
                    .add(productData) // Add product data
                    .addOnSuccessListener(documentReference -> {
                        // If imageUri is not null, upload the image after saving the product data
                        if (imageUri != null) {
                            uploadImage(documentReference.getId()); // Call method to upload image
                        } else {
                            Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Finish the activity after success
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure in saving product
                        Toast.makeText(AddProductActivity.this, "Error saving product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void uploadImage(String productId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_images/" + productId + ".jpg");
        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            db.collection("products").document(productId)
                    .update("imageUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddProductActivity.this, "Product added successfully with image!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddProductActivity.this, "Error updating image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        })).addOnFailureListener(e -> Toast.makeText(AddProductActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Helper class to store size and price inputs
    private class SizePricePair {
        private EditText sizeInput;
        private EditText priceInput;

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
