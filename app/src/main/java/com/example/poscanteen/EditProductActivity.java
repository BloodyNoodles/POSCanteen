package com.example.poscanteen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText inputProductName;
    private RadioGroup radioGroup;
    private ImageView selectedImageView;
    private Button addAddonButton, sizesAndPricesButton, deleteButton, saveButton;
    private LinearLayout addonsContainer, sizesAndPriceContainer;
    private Uri imageUri;

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String productId; // to hold the product ID passed from the previous activity

    private List<View> addonViews = new ArrayList<>(); // To keep track of added addon fields
    private List<View> sizePriceViews = new ArrayList<>(); // To keep track of added size/price fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product); // Adjust to your layout file

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Get product ID from Intent
        productId = getIntent().getStringExtra("PRODUCT_ID");

        // Initialize views
        inputProductName = findViewById(R.id.InputproductName);
        radioGroup = findViewById(R.id.radioGroup);
        selectedImageView = findViewById(R.id.selectedImageView);
        addAddonButton = findViewById(R.id.addAddonButton);
        sizesAndPricesButton = findViewById(R.id.sizesAndPricesButton);
        deleteButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        addonsContainer = findViewById(R.id.addonsContainer);
        sizesAndPriceContainer = findViewById(R.id.SizesandPriceContainer);

        // Set listeners
        selectedImageView.setOnClickListener(v -> openImageChooser());
        saveButton.setOnClickListener(v -> saveProduct());
        deleteButton.setOnClickListener(v -> deleteProduct());
        addAddonButton.setOnClickListener(v -> addAddonField());
        sizesAndPricesButton.setOnClickListener(v -> addSizeAndPriceField());

        // Load existing product data
        loadProductData(productId);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                selectedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProductData(String productId) {
        // Load product data from Firestore using the productId
        db.collection("products").document(productId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                inputProductName.setText(documentSnapshot.getString("name"));
                // Load other fields and set them appropriately
                // For example, if you have category as a String
                String category = documentSnapshot.getString("category");
                if (category != null) {
                    if (category.equals("Snacks")) {
                        radioGroup.check(R.id.radioButton1);
                    } else if (category.equals("Drinks")) {
                        radioGroup.check(R.id.radioButton2);
                    } else if (category.equals("Essentials")) {
                        radioGroup.check(R.id.radioButton3);
                    }
                }
                // Load and display image if available
                String imageUrl = documentSnapshot.getString("imageUrl");
                if (imageUrl != null) {
                    selectedImageView.setImageURI(Uri.parse(imageUrl)); // Use your method to load the image from the URL
                }
                // Handle loading of add-ons and sizes/prices here if needed
            }
        }).addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Error loading product", Toast.LENGTH_SHORT).show());
    }

    private void saveProduct() {
        final String name = inputProductName.getText().toString(); // Declare name as final
        final int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        final String category; // Declare category as a final variable

        // Set the category based on the selected radio button
        if (selectedRadioButtonId == R.id.radioButton1) {
            category = "Snacks";
        } else if (selectedRadioButtonId == R.id.radioButton2) {
            category = "Drinks";
        } else if (selectedRadioButtonId == R.id.radioButton3) {
            category = "Essentials";
        } else {
            category = ""; // Handle case where no category is selected
        }

        if (imageUri != null) {
            // Upload image to Firebase Storage
            StorageReference fileRef = storageRef.child("product_images/" + productId + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        final String imageUrl = uri.toString(); // Declare imageUrl as final
                        saveProductToFirestore(name, category, imageUrl);
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(EditProductActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show()
            );
        } else {
            saveProductToFirestore(name, category, null); // Save without image if not selected
        }
    }



    private void saveProductToFirestore(String name, String category, String imageUrl) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", name);
        productData.put("category", category);
        if (imageUrl != null) {
            productData.put("imageUrl", imageUrl);
        }

        // Add add-ons to the product data
        List<Map<String, Object>> addons = new ArrayList<>();
        for (View addonView : addonViews) {
            EditText addonName = addonView.findViewById(R.id.addonName);
            EditText addonPrice = addonView.findViewById(R.id.addonPrice);
            if (addonName.getText().length() > 0 && addonPrice.getText().length() > 0) {
                Map<String, Object> addonData = new HashMap<>();
                addonData.put("addonName", addonName.getText().toString());
                addonData.put("addonPrice", addonPrice.getText().toString());
                addons.add(addonData);
            }
        }
        productData.put("addons", addons);

        // Add sizes and prices to the product data
        List<Map<String, Object>> sizesAndPrices = new ArrayList<>();
        for (View sizePriceView : sizePriceViews) {
            EditText sizeInput = sizePriceView.findViewById(R.id.sizeInput);
            EditText priceInput = sizePriceView.findViewById(R.id.priceInput);
            if (sizeInput.getText().length() > 0 && priceInput.getText().length() > 0) {
                Map<String, Object> sizePriceData = new HashMap<>();
                sizePriceData.put("size", sizeInput.getText().toString());
                sizePriceData.put("price", priceInput.getText().toString());
                sizesAndPrices.add(sizePriceData);
            }
        }
        productData.put("sizesAndPrices", sizesAndPrices);

        // Update Firestore document
        db.collection("products").document(productId).set(productData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProductActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Error updating product", Toast.LENGTH_SHORT).show());
    }

    private void deleteProduct() {
        db.collection("products").document(productId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProductActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Error deleting product", Toast.LENGTH_SHORT).show());
    }

    private void addAddonField() {
        View addonView = getLayoutInflater().inflate(R.layout.addon_field, null); // Create a new layout for the addon
        EditText addonName = addonView.findViewById(R.id.addonName);
        EditText addonPrice = addonView.findViewById(R.id.addonPrice);
        addonsContainer.addView(addonView);
        addonViews.add(addonView); // Keep track of the view for saving later
    }

    private void addSizeAndPriceField() {
        View sizePriceView = getLayoutInflater().inflate(R.layout.size_price_field, null); // Create a new layout for size/price
        EditText sizeInput = sizePriceView.findViewById(R.id.sizeInput);
        EditText priceInput = sizePriceView.findViewById(R.id.priceInput);
        sizesAndPriceContainer.addView(sizePriceView);
        sizePriceViews.add(sizePriceView); // Keep track of the view for saving later
    }
}
