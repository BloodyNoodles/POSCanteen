package com.example.poscanteen;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private static final String TAG = "TransactionHistoryActivity";

    private RecyclerView recyclerViewHistory;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private SearchView searchView;
    private TextView totalPriceTextView;

    private SideMenuFragment sideMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history);

        // Initialize Firebase Firestore and Authentication
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Handle case when user is not logged in
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity as there is no valid user session
            return;
        }

        // Initialize UI components
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        searchView = findViewById(R.id.searchView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        if (recyclerViewHistory == null) {
            Log.e(TAG, "RecyclerView is null. Please ensure the correct ID is used in transaction_history.xml");
            return; // Prevent further execution if recyclerView is not found
        }

        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        // Side menu button
        ImageButton menuBtn = findViewById(R.id.menubtn);
        if (menuBtn == null) {
            Log.e(TAG, "Menu button is null. Please check the XML ID.");
        } else {
            menuBtn.setOnClickListener(v -> toggleSideMenu());
        }

        // Add the SideMenuFragment to the activity if it's not already added
        if (savedInstanceState == null) {
            sideMenuFragment = new SideMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sideMenus, sideMenuFragment)
                    .commit();
        } else {
            sideMenuFragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.sideMenus);
        }

        // Initialize Transaction List and Adapter
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList, transaction -> {
            // Handle clicking on a transaction if needed
            Toast.makeText(this, "Transaction clicked: " + transaction.getProductName(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewHistory.setAdapter(transactionAdapter);

        // Fetch transactions
        fetchTransactions();

        // Set up the SearchView to filter transactions by date
        setupSearchView();
    }

    // Method to fetch transactions from Firestore
    private void fetchTransactions() {
        String userId = currentUser.getUid();

        db.collection("users")
                .document(userId)
                .collection("transactions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            transactionList.clear();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                try {
                                    Transaction transaction = document.toObject(Transaction.class);
                                    transactionList.add(transaction);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing transaction document", e);
                                }
                            }

                            // Notify the adapter of data changes to refresh the RecyclerView
                            transactionAdapter.notifyDataSetChanged();

                            // Calculate the total price
                            calculateTotalPrice();
                        } else {
                            Toast.makeText(this, "No transactions found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error getting transactions", task.getException());
                        Toast.makeText(this, "Error fetching transactions. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore fetch failed", e);
                    Toast.makeText(this, "Failed to fetch transactions. Please check your connection.", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to calculate the total price of transactions
    private void calculateTotalPrice() {
        double totalPrice = 0.0;
        for (Transaction transaction : transactionList) {
            totalPrice += transaction.getTotalPrice();
        }
        totalPriceTextView.setText(String.format("Total Price: ₱ %.2f", totalPrice));
    }

    // Method to set up the SearchView for filtering transactions by date
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTransactionsByDate(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If search text is cleared, reload the full transaction list
                    fetchTransactions();
                } else {
                    filterTransactionsByDate(newText);
                }
                return true;
            }
        });
    }

    // Method to filter transactions based on the entered date
    private void filterTransactionsByDate(String date) {
        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getDate() != null && transaction.getDate().contains(date)) {
                filteredList.add(transaction);
            }
        }

        // Update the adapter with the filtered list
        transactionAdapter.updateList(filteredList);

        // Update the total price for the filtered transactions
        calculateFilteredTotalPrice(filteredList);
    }



    // Method to calculate the total price of the filtered transactions
    private void calculateFilteredTotalPrice(List<Transaction> filteredList) {
        double totalPrice = 0.0;
        for (Transaction transaction : filteredList) {
            totalPrice += transaction.getTotalPrice();
        }
        totalPriceTextView.setText(String.format("Total Price: ₱ %.2f", totalPrice));
    }

    // Method to toggle side menu fragment visibility
    private void toggleSideMenu() {
        if (sideMenuFragment != null) {
            sideMenuFragment.toggleSideMenu();
        } else {
            Log.e(TAG, "SideMenuFragment is not initialized");
        }
    }
}
