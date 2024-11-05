package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.AppCompatButton;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Checkout extends AppCompatActivity implements CheckoutAdapter.OnQuantityChangeListener {

    private RecyclerView recyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<Product> productList;

    // Fields for Order Number and Date
    private TextView orderNumberTextView;
    private TextView orderDateTextView;
    private TextView totalAmountTextView;

    private double totalAmount = 0.0;
    private SideMenuFragment sideMenuFragment;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private int currentOrderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get cart items from CartManager
        productList = CartManager.getInstance().getCartItems();

        if (productList == null || productList.isEmpty()) {
            Toast.makeText(this, "No items in cart", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up sidebar/menu button
        ImageButton menuBtn = findViewById(R.id.menubtn);
        menuBtn.setOnClickListener(v -> toggleSideMenu());

        // Set up RecyclerView
        recyclerView = findViewById(R.id.item_List);
        if (recyclerView == null) {
            Log.e("Checkout", "RecyclerView is null. Check your XML layout for id 'item_List'");
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutAdapter(this, productList, this);
        recyclerView.setAdapter(checkoutAdapter);

        // Initialize order number and date TextViews
        orderNumberTextView = findViewById(R.id.order_number);
        orderDateTextView = findViewById(R.id.order_date);
        totalAmountTextView = findViewById(R.id.total_amount);

        if (orderNumberTextView == null || orderDateTextView == null || totalAmountTextView == null) {
            Log.e("Checkout", "One of the TextViews is null. Ensure all IDs are correct in your XML.");
            finish();
            return;
        }

        // Fetch the current order number from Firestore
        fetchUserSpecificOrderNumber();

        // Add Items Button
        AppCompatButton addItemsButton = findViewById(R.id.additionalbtn);
        addItemsButton.setOnClickListener(v -> {
            // Start Home activity to add items
            Intent intent = new Intent(Checkout.this, home.class);
            startActivity(intent);
        });

        // Pay Button
        AppCompatButton payButton = findViewById(R.id.paybtn);
        payButton.setOnClickListener(v -> {
            if (totalAmount <= 0) {
                Toast.makeText(Checkout.this, "Total amount must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start TransactionActivity and pass the total amount and order number
            Intent intent = new Intent(Checkout.this, TransactionActivity.class);
            intent.putExtra("TOTAL_AMOUNT", totalAmount);
            intent.putExtra("ORDER_NUMBER", currentOrderNumber);
            intent.putExtra("CURRENT_ORDER_NUMBER", currentOrderNumber);
            CartManager.getInstance().saveProductsToIntent(intent, productList);
            startActivity(intent);
        });

        // Cancel Button
        AppCompatButton cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> showCancelConfirmationDialog());

        // Calculate initial total amount
        updateTotalAmount();

        // Add the SideMenuFragment to the activity if it's not already added
        if (savedInstanceState == null) {
            sideMenuFragment = new SideMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sideMenus, sideMenuFragment)
                    .commit();
        } else {
            sideMenuFragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.sideMenus);
        }
    }

    // Method to show a confirmation dialog for cancelling the order
    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Order");
        builder.setMessage("Are you sure you want to cancel the order? All items in your cart will be discarded.");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User confirmed to cancel the order
            CartManager.getInstance().clearCart();  // Clear the cart items
            Intent intent = new Intent(Checkout.this, home.class);
            startActivity(intent);
            finish();  // Close the checkout activity
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // User chose not to cancel - just dismiss the dialog
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Fetch the current order number from Firestore for the specific user
    private void fetchUserSpecificOrderNumber() {
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference orderRef = db.collection("users").document(userId).collection("orderTracking").document("orderNumberTracker");

        orderRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Long lastOrderNumber = document.getLong("lastOrderNumber");
                    currentOrderNumber = (lastOrderNumber != null) ? lastOrderNumber.intValue() + 1 : 1;
                } else {
                    // If no document exists, initialize with order number 1
                    currentOrderNumber = 1;
                    // Create the order tracker document
                    orderRef.set(new HashMap<String, Object>() {{
                                put("lastOrderNumber", 0);
                            }}).addOnSuccessListener(aVoid -> Log.d("Checkout", "Initialized order number in Firestore"))
                            .addOnFailureListener(e -> Log.e("Checkout", "Error initializing order number in Firestore", e));
                }
                orderNumberTextView.setText(String.format("Order: #%d", currentOrderNumber));
            } else {
                Log.e("Checkout", "Error getting order number", task.getException());
            }
        });

        // Set the date directly for the order
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        orderDateTextView.setText(String.format("Date: %s", currentDate));
    }

    // Method to update the total amount displayed
    private void updateTotalAmount() {
        totalAmount = 0.0;

        for (Product product : productList) {
            totalAmount += product.getTotalPrice();
        }

        totalAmountTextView.setText(String.format("₱ %.2f", totalAmount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh product list in case new items were added
        productList = CartManager.getInstance().getCartItems();
        checkoutAdapter.updateProductList(productList);
        updateTotalAmount();
    }

    // Callback for when quantity changes
    @Override
    public void onQuantityChanged() {
        // Update the total amount whenever quantity changes
        updateTotalAmount();
    }

    // Method to toggle side menu fragment visibility
    private void toggleSideMenu() {
        if (sideMenuFragment != null) {
            sideMenuFragment.toggleSideMenu();
        }
    }
}
