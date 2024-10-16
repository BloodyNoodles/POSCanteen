package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home); // Ensure this layout file exists

        // Initialize UI components
        AppCompatButton checkout = findViewById(R.id.checkoutBtn);
        ImageButton menuBtn = findViewById(R.id.menubtn);
        AppCompatButton allCategoriesButton = findViewById(R.id.allCategoriesButton);
        AppCompatButton drinkMenuButton = findViewById(R.id.drinkMenuButton);
        AppCompatButton snackMenuButton = findViewById(R.id.snackMenuButton);
        AppCompatButton essentialsMenuButton = findViewById(R.id.essentialsMenuButton);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            throw new RuntimeException("RecyclerView is null. Ensure the ID matches the layout.");
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        // Prepare the product list and adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this); // Use a request code of your choice
        recyclerView.setAdapter(productAdapter);

        // Fetch all products initially if the user is logged in
        if (currentUser != null) {
            fetchProducts(null); // Show all categories by default
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Add the SideMenuFragment to the activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sideMenus, new SideMenuFragment())
                    .commit();
        }

        // Checkout button listener
        checkout.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, checkout.class); // Ensure checkout activity exists
            startActivity(intent);
        });

        // Menu button click listener (toggles the fragment visibility)
        menuBtn.setOnClickListener(v -> {
            SideMenuFragment fragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.sideMenus);
            if (fragment != null) {
                fragment.toggleSideMenu();
            }
        });

        // Set click listeners for menu category buttons
        allCategoriesButton.setOnClickListener(v -> fetchProducts(null)); // Show all products
        drinkMenuButton.setOnClickListener(v -> fetchProducts("Drinks")); // Show drinks only
        snackMenuButton.setOnClickListener(v -> fetchProducts("Snacks")); // Show snacks only
        essentialsMenuButton.setOnClickListener(v -> fetchProducts("Essentials")); // Show essentials only
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Fetch data from Firebase when the activity starts or resumes
        fetchProducts(null);
    }

    // Fetch products from Firestore, with optional filtering by category
    private void fetchProducts(String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("products")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            productList.clear(); // Clear the list before adding new products
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);

                                // Filter products by category if specified
                                if (category == null || product.getCategory().equalsIgnoreCase(category)) {
                                    productList.add(product);
                                }
                            }
                            // Notify the adapter of data changes on the main thread
                            runOnUiThread(() -> productAdapter.notifyDataSetChanged());
                        } else {
                            Toast.makeText(this, "Error fetching products", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}