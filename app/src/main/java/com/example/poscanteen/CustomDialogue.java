package com.example.poscanteen;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomDialogue extends Dialog {

    private Context context;
    private Product product;
    private TextView quantityValue;
    private int quantity = 1; // Default quantity
    private boolean toastShown = false;

    // Lists for add-ons and sizes
    private List<AddOn> addOnList = new ArrayList<>();
    private List<Size> sizeList = new ArrayList<>();

    public CustomDialogue(@NonNull Context context, Product product) {
        super(context);
        this.context = context;
        this.product = product;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_order);

        // Initialize the views
        Button confirmButton = findViewById(R.id.button_confirm);
        Button cancelButton = findViewById(R.id.button_cancel);
        Button decreaseQuantity = findViewById(R.id.decrease_quantity);
        Button increaseQuantity = findViewById(R.id.increase_quantity);
        quantityValue = findViewById(R.id.quantity_value);
        TextView productNameTextView = findViewById(R.id.productNameTextView);
        productNameTextView.setText(product.getName());

        // Initialize quantity
        quantityValue.setText(String.valueOf(quantity));

        // Set click listeners for quantity buttons
        decreaseQuantity.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityValue.setText(String.valueOf(quantity));
                toastShown = false;
            } else if (!toastShown) {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                toastShown = true;
            }
        });

        increaseQuantity.setOnClickListener(v -> {
            quantity++;
            quantityValue.setText(String.valueOf(quantity));
        });

        // Setup confirm and cancel button listeners
        confirmButton.setOnClickListener(v -> {
            passDataToCheckout();
            dismiss();
        });
        cancelButton.setOnClickListener(v -> dismiss());

        // Fetch add-ons and sizes from Firestore
        fetchAddOns();
        fetchSizes();
    }

    private void fetchAddOns() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        String productId = product.getId();
        if (productId == null || productId.isEmpty()) {
            Log.e("CustomDialogue", "Product ID is null or empty");
            Toast.makeText(context, "Invalid product selected.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = currentUser.getUid();

        db.collection("users").document(userId).collection("products").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> addOnMaps = (List<Map<String, Object>>) documentSnapshot.get("addons");
                        if (addOnMaps != null && !addOnMaps.isEmpty()) {
                            for (Map<String, Object> addOnMap : addOnMaps) {
                                String name = (String) addOnMap.get("addonName");
                                Object priceObj = addOnMap.get("addonPrice");
                                Double price = null;
                                if (priceObj instanceof Double) {
                                    price = (Double) priceObj;
                                } else if (priceObj instanceof String) {
                                    try {
                                        price = Double.parseDouble((String) priceObj);
                                    } catch (NumberFormatException e) {
                                        Log.e("CustomDialogue", "Error parsing add-on price: " + priceObj, e);
                                    }
                                }

                                if (name != null && price != null) {
                                    addOnList.add(new AddOn(name, price));
                                }
                            }
                        } else {
                            Log.e("CustomDialogue", "No add-ons found for this product.");
                        }
                        setupRecyclerViews(); // Call setupRecyclerViews after both data fetches
                    } else {
                        Log.e("CustomDialogue", "No such document: " + productId);
                        Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CustomDialogue", "Error fetching add-ons", e);
                    Toast.makeText(context, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSizes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        String productId = product.getId();
        if (productId == null || productId.isEmpty()) {
            Log.e("CustomDialogue", "Product ID is null or empty");
            Toast.makeText(context, "Invalid product selected.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = currentUser.getUid();

        db.collection("users").document(userId).collection("products").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> sizeMaps = (List<Map<String, Object>>) documentSnapshot.get("sizesAndPrices");
                        if (sizeMaps != null) {
                            for (Map<String, Object> sizeMap : sizeMaps) {
                                String name = (String) sizeMap.get("size");
                                Object priceObj = sizeMap.get("price");
                                Double price = null;
                                if (priceObj instanceof Double) {
                                    price = (Double) priceObj;
                                } else if (priceObj instanceof String) {
                                    try {
                                        price = Double.parseDouble((String) priceObj);
                                    } catch (NumberFormatException e) {
                                        Log.e("CustomDialogue", "Error parsing size price: " + priceObj, e);
                                    }
                                }

                                if (name != null && price != null) {
                                    sizeList.add(new Size(name, price));
                                }
                            }
                            setupRecyclerViews(); // Call this only once after both data fetches
                        }
                    } else {
                        Log.e("CustomDialogue", "No such document: " + productId);
                        Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CustomDialogue", "Error fetching sizes", e);
                    Toast.makeText(context, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupRecyclerViews() {
        // Setup RecyclerView for add-ons
        RecyclerView recyclerAddsOn = findViewById(R.id.recycler_adds_on);
        AddOnAdapter addOnAdapter = new AddOnAdapter(context, addOnList);
        recyclerAddsOn.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerAddsOn.setAdapter(addOnAdapter);

        // Setup RecyclerView for sizes
        RecyclerView recyclerSizes = findViewById(R.id.recycler_sizes);
        SizeAdapter sizeAdapter = new SizeAdapter(context, sizeList);
        recyclerSizes.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerSizes.setAdapter(sizeAdapter);
    }

    private void passDataToCheckout() {
        double sizePrice = 0.0;
        double addOnsPrice = 0.0;

        // Get the selected size price
        Size selectedSize = null;
        for (Size size : sizeList) {
            if (size.isSelected()) {
                selectedSize = size;
                sizePrice = size.getPrice();
                break;
            }
        }

        if (selectedSize == null) {
            Toast.makeText(context, "Please select a size before proceeding.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate add-ons price
        List<AddOn> selectedAddOns = new ArrayList<>();
        for (AddOn addOn : addOnList) {
            if (addOn.isSelected()) {
                selectedAddOns.add(addOn);
                addOnsPrice += addOn.getPrice();
            }
        }

        // Calculate the unit price (size price + add-ons price)
        double unitPrice = sizePrice + addOnsPrice;

        // Create a Product object to add to the cart
        Product checkoutProduct = new Product(
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getCategory(),
                product.getDescription(),
                product.getPrice(),
                selectedAddOns,
                sizeList
        );

        checkoutProduct.setSelectedSize(selectedSize);
        checkoutProduct.setSelectedAddOns(selectedAddOns);
        checkoutProduct.calculateUnitPrice(); // Update unit price with selected values
        checkoutProduct.setQuantity(quantity);

        // Add product to CartManager
        CartManager.getInstance().addItemToCart(checkoutProduct);
        Toast.makeText(context, "Product added to cart successfully", Toast.LENGTH_SHORT).show();

        // Close the dialogue
        dismiss();

        // Open the Checkout activity
        try {
            Intent intent = new Intent(context, Checkout.class);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("CustomDialogue", "Error while opening checkout: " + e.getMessage(), e);
            Toast.makeText(context, "An error occurred while proceeding to checkout. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

}
