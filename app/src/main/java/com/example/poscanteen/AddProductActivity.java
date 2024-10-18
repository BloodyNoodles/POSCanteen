package com.example.poscanteen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
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
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private ImageButton menuBtn;
    private LinearLayout addonContainer, sizeContainer;
    private Button addAddonButton, addSizeButton, addButton, cancelButton, imageButton;
    private EditText productName, description;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private List<SizePricePair> sizePriceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        // Initialize Firebase
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

        // Set listeners
        addButton.setOnClickListener(v -> saveProduct());
        cancelButton.setOnClickListener(v -> finish());
        imageButton.setOnClickListener(v -> openFileChooser());
        addAddonButton.setOnClickListener(v -> addAddonFields());
        addSizeButton.setOnClickListener(v -> addSizeAndPriceFields());

        // Set up the side menu fragment
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

        addInitialSizeAndPriceFields();
    }

    // Add the initial size and price fields without delete button
    private void addInitialSizeAndPriceFields() {
        LinearLayout sizePriceLayout = new LinearLayout(this);
        sizePriceLayout.setOrientation(LinearLayout.HORIZONTAL);
        sizePriceLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText sizeInput = new EditText(this);
        sizeInput.setHint("Size");
        sizeInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Setting weight to 1, same as dynamic fields

        EditText priceInput = new EditText(this);
        priceInput.setHint("Price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceInput.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Setting weight to 1, same as dynamic fields

        // Add hidden delete button to maintain consistent layout
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.delete);
        deleteButton.setBackground(null);  // Remove the default background
        deleteButton.setVisibility(View.INVISIBLE);  // Hide the button

        // Set the same size for the button to keep the layout consistent
        int sizeInDp = 50;  // Adjust based on desired size
        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(8, 8, 8, 8);  // Adding some margin for better visibility
        deleteButton.setLayoutParams(params);

        // Add the size and price input fields to the layout
        sizePriceLayout.addView(sizeInput);
        sizePriceLayout.addView(priceInput);
        sizePriceLayout.addView(deleteButton);  // Add hidden delete button to the layout

        // Add the layout to the container
        sizeContainer.addView(sizePriceLayout);

        // Store the reference to the first size and price pair (this one can't be deleted)
        sizePriceList.add(new SizePricePair(sizeInput, priceInput));
    }



    // Open image file chooser
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
            ImageView selectedImageView = findViewById(R.id.selectedImageView);
            selectedImageView.setVisibility(View.VISIBLE);
            selectedImageView.setImageURI(imageUri);
        }
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
        // Remove the default background to prevent the gray background
        deleteButton.setBackground(null);

        // Set a larger size for better visibility (try 32dp instead of 24dp)
        int sizeInDp = 50;  // Adjust based on desired size
        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

        // Set layout parameters for the button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(8, 8, 8, 8); // Adding some margin for better visibility
        deleteButton.setLayoutParams(params);

        // Set the scale type to ensure the image fits well
        deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Handle delete action
        deleteButton.setOnClickListener(v -> addonContainer.removeView(addonLayout));

        addonLayout.addView(addonNameInput);
        addonLayout.addView(addonPriceInput);
        addonLayout.addView(deleteButton);  // Add delete button to layout
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
        // Remove the default background to prevent the gray background
        deleteButton.setBackground(null);

        // Set a larger size for better visibility (try 32dp instead of 24dp)
        int sizeInDp = 50;  // Adjust based on desired size
        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp, getResources().getDisplayMetrics());

        // Set layout parameters for the button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(8, 8, 8, 8); // Adding some margin for better visibility
        deleteButton.setLayoutParams(params);

        // Set the scale type to ensure the image fits well
        deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Handle delete action
        deleteButton.setOnClickListener(v -> sizeContainer.removeView(sizePriceLayout));

        sizePriceLayout.addView(sizeInput);
        sizePriceLayout.addView(priceInput);
        sizePriceLayout.addView(deleteButton);  // Add delete button to layout
        sizeContainer.addView(sizePriceLayout);

        sizePriceList.add(new SizePricePair(sizeInput, priceInput));
    }


    // Save product to Firestore
    private void saveProduct() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String enteredProductName = productName.getText().toString().trim();
            String descriptionStr = description.getText().toString().trim();
            String initialSize = sizePriceList.get(0).getSize();
            String initialPrice = sizePriceList.get(0).getPrice();

            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String category = selectedId != -1 ? ((RadioButton) findViewById(selectedId)).getText().toString() : "";

            if (enteredProductName.isEmpty()  || category.isEmpty()  || category.isEmpty()  || initialSize.isEmpty() || initialPrice.isEmpty() ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Saving product...");
            progressDialog.setCancelable(false); // Prevent dialog from being dismissed by tapping outside
            progressDialog.show();

            List<Map<String, String>> addOns = new ArrayList<>();
            for (int i = 0; i < addonContainer.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) addonContainer.getChildAt(i);
                String addonName = ((EditText) layout.getChildAt(0)).getText().toString().trim();
                String addonPrice = ((EditText) layout.getChildAt(1)).getText().toString().trim();
                if (!addonName.isEmpty() && !addonPrice.isEmpty()) {
                    Map<String, String> addonData = new HashMap<>();
                    addonData.put("addonName", addonName);
                    addonData.put("addonPrice", addonPrice);
                    addOns.add(addonData);
                }
            }

            List<Map<String, String>> sizesAndPrices = new ArrayList<>();
            for (SizePricePair pair : sizePriceList) {
                String size = pair.getSize();
                String price = pair.getPrice();
                if (!size.isEmpty() && !price.isEmpty()) {
                    Map<String, String> sizePriceMap = new HashMap<>();
                    sizePriceMap.put("size", size);
                    sizePriceMap.put("price", price);
                    sizesAndPrices.add(sizePriceMap);
                }
            }

            // Create product data without userId
            Map<String, Object> productData = new HashMap<>();
            productData.put("name", enteredProductName);
            productData.put("description", descriptionStr);
            productData.put("category", category);
            productData.put("addons", addOns);
            productData.put("sizesAndPrices", sizesAndPrices);

            // Save product to user's sub-collection in Firestore
            db.collection("users")
                    .document(currentUser.getUid()) // Get the user's document
                    .collection("products") // Create a sub-collection for products
                    .add(productData)
                    .addOnSuccessListener(documentReference -> {
                        if (imageUri != null) {
                            uploadImage(documentReference.getId(), progressDialog); // Pass the progressDialog here
                        } else {
                            clearFields();
                            Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                            closeSideMenu();

                            // Dismiss loading dialog
                            progressDialog.dismiss();

                            // Navigate to the home activity
                            Intent intent = new Intent(this, home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss(); // Dismiss loading dialog on failure
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Clear the fields after product is added
    private void clearFields() {
        productName.setText("");
        description.setText("");
        addonContainer.removeAllViews();
        sizeContainer.removeAllViews();
        sizePriceList.clear(); // Clear the list of size and price pairs
        imageUri = null;
        ImageView selectedImageView = findViewById(R.id.selectedImageView);
        selectedImageView.setVisibility(View.GONE);
    }

    // Upload image to Firebase Storage
// Upload image to Firebase Storage
    private void uploadImage(String productId, ProgressDialog progressDialog) {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("product_images/" + productId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Update Firestore with the image URL
                    db.collection("users").document(currentUser.getUid())
                            .collection("products").document(productId)
                            .update("imageUrl", uri.toString())
                            .addOnSuccessListener(aVoid -> {
                                clearFields();
                                Toast.makeText(this, "Product added with image!", Toast.LENGTH_SHORT).show();
                                closeSideMenu();

                                // Dismiss the loading dialog
                                progressDialog.dismiss();

                                // Navigate to the home activity
                                Intent intent = new Intent(this, home.class); // 'this' context since we're in an Activity
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // Close the current activity
                            }).addOnFailureListener(e -> {
                                progressDialog.dismiss(); // Dismiss the dialog if there's an error
                                Toast.makeText(this, "Error updating image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss the dialog if image upload fails
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Method to close the side menu
// Method to close the side menu only if it is open
    private void closeSideMenu() {
        FragmentManager fm = getSupportFragmentManager();
        SideMenuFragment fragment = (SideMenuFragment) fm.findFragmentById(R.id.sideMenus);

    }


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
