package com.example.poscanteen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {

    private TextView totalValueTextView, changeValueTextView;
    private EditText paymentValueEditText;
    private double totalAmount;
    private List<Product> productList; // Product list to save transaction details
    private int currentOrderNumber; // Current order number to update in Firestore

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction);

        // Initialize the views
        totalValueTextView = findViewById(R.id.total_value);
        paymentValueEditText = findViewById(R.id.payment_value);
        changeValueTextView = findViewById(R.id.change_value);
        ImageButton exitButton = findViewById(R.id.exitButton);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Keypad buttons
        int[] buttonIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
                R.id.btn_4, R.id.btn_5, R.id.btn_6,
                R.id.btn_7, R.id.btn_8, R.id.btn_9
        };

        AppCompatButton btnBackspace = findViewById(R.id.btn_backspace);
        AppCompatButton btnEnter = findViewById(R.id.btn_enter);
        AppCompatButton btnCancel = findViewById(R.id.btn_cancel);
        AppCompatButton btnOk = findViewById(R.id.btn_ok);

        // Get total amount, order number, and product list from intent
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0);
        currentOrderNumber = getIntent().getIntExtra("CURRENT_ORDER_NUMBER", 1);
        productList = CartManager.getInstance().getProductsFromIntent(getIntent());

        // Set total amount
        totalValueTextView.setText(String.format("₱%.2f", totalAmount));

        // Add payment input text listener to calculate change automatically
        paymentValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set click listeners for numeric keypad buttons
        for (int buttonId : buttonIds) {
            AppCompatButton button = findViewById(buttonId);
            button.setOnClickListener(v -> appendToPaymentValue(((AppCompatButton) v).getText().toString()));
        }

        // Backspace button functionality
        btnBackspace.setOnClickListener(v -> {
            String currentValue = paymentValueEditText.getText().toString();
            if (!currentValue.isEmpty()) {
                paymentValueEditText.setText(currentValue.substring(0, currentValue.length() - 1));
            }
        });

        // Enter button functionality (same as calculating change)
        btnEnter.setOnClickListener(v -> calculateChange());

        // Cancel button - shows a confirmation dialog
        btnCancel.setOnClickListener(v -> showCancelConfirmationDialog());

        // OK button - validates payment and shows receipt dialog
        btnOk.setOnClickListener(v -> {
            if (validatePayment()) {
                saveTransactionToFirestore(); // Save the transaction to Firestore
                showReceiptDialog(); // Show receipt before redirecting to home
            } else {
                Toast.makeText(TransactionActivity.this, "Insufficient payment. Please enter an amount equal to or greater than the total.", Toast.LENGTH_SHORT).show();
            }
        });

        // Exit button - close activity
        exitButton.setOnClickListener(v -> showCancelConfirmationDialog());
    }

    // Append value to the payment field
    private void appendToPaymentValue(String value) {
        paymentValueEditText.append(value);
    }

    // Method to calculate and display the change
    private void calculateChange() {
        String paymentText = paymentValueEditText.getText().toString();
        if (!paymentText.isEmpty()) {
            try {
                double paymentAmount = Double.parseDouble(paymentText);
                if (paymentAmount >= totalAmount) {
                    double change = paymentAmount - totalAmount;
                    changeValueTextView.setText(String.format("₱%.2f", change));
                } else {
                    changeValueTextView.setText("₱0.00");
                }
            } catch (NumberFormatException e) {
                changeValueTextView.setText("₱0.00");
            }
        } else {
            changeValueTextView.setText("₱0.00");
        }
    }

    // Validate that the payment is sufficient
    private boolean validatePayment() {
        String paymentText = paymentValueEditText.getText().toString();
        if (!paymentText.isEmpty()) {
            try {
                double paymentAmount = Double.parseDouble(paymentText);
                return paymentAmount >= totalAmount;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    // Show confirmation dialog when cancel or exit button is clicked
    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Transaction");
        builder.setMessage("Are you sure you want to cancel the transaction? Any entered data will be lost.");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            finish(); // Close the activity
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss(); // Simply dismiss the dialog
        });

        builder.create().show();
    }

    // Show the receipt dialog after a successful transaction
    private void showReceiptDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.transaction_dialog, null);

        // Initialize dialog UI elements
        TextView productNameTextView = dialogView.findViewById(R.id.product_name);
        TextView transactionDateTextView = dialogView.findViewById(R.id.transactionDate);
        TextView transactionQuantityTextView = dialogView.findViewById(R.id.transactionQuantity);
        TextView transactionAmountTextView = dialogView.findViewById(R.id.transactionAmount);
        TextView transactionOrderNumberTextView = dialogView.findViewById(R.id.transactionOrderNumber);

        // Set receipt values (assuming only one product for simplicity, otherwise loop through productList)
        Product product = productList.get(0);
        productNameTextView.setText(product.getName());

        // Get current date
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        transactionDateTextView.setText(String.format("Date: %s", currentDate));

        // Set product quantity
        transactionQuantityTextView.setText(String.format("Quantity: %d", product.getQuantity()));

        // Handle unit price safely
        double unitPrice = product.getPrice() != null ? product.getPrice() : 0.0;

        // Handle total price safely
        double totalPrice = product.getTotalPrice() != null ? product.getTotalPrice() : 0.0;
        transactionAmountTextView.setText(String.format(Locale.getDefault(), "Total Price: ₱%.2f", totalPrice));

        // Set order number
        transactionOrderNumberTextView.setText(String.format("Order Number: %d", currentOrderNumber));

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setPositiveButton("Okay", (dialog, which) -> {
            // Clear cart and redirect to home
            CartManager.getInstance().clearCart();
            Intent intent = new Intent(TransactionActivity.this, home.class);
            startActivity(intent);
            finish();
        });

        builder.create().show();
    }




    // Method to save transaction details to Firestore
    private void saveTransactionToFirestore() {
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        for (Product product : productList) {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("productName", product.getName());
            transaction.put("orderNumber", currentOrderNumber);  // Save as an integer
            transaction.put("quantity", product.getQuantity());
            transaction.put("unitPrice", product.getPrice());
            transaction.put("totalPrice", product.getTotalPrice());
            transaction.put("date", currentDate);

            db.collection("users")
                    .document(userId)
                    .collection("transactions")
                    .add(transaction)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("TransactionActivity", "Transaction successfully recorded with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TransactionActivity", "Error adding transaction", e);
                    });
        }

        // Update the order number in Firestore only after successful transaction
        updateOrderNumberInFirestore();
    }

    // Method to update order number in Firestore
    private void updateOrderNumberInFirestore() {
        DocumentReference orderRef = db.collection("users")
                .document(currentUser.getUid())
                .collection("orderTracking")
                .document("orderNumberTracker");
        orderRef.update("lastOrderNumber", currentOrderNumber)
                .addOnSuccessListener(aVoid -> Log.d("TransactionActivity", "Order number successfully updated in Firestore"))
                .addOnFailureListener(e -> Log.e("TransactionActivity", "Error updating order number in Firestore", e));
    }
}
