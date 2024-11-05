package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
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
        setContentView(R.layout.home);

        // Initialize UI components
        AppCompatButton checkoutButton = findViewById(R.id.checkoutBtn);
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
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        // Fetch all products initially if the user is logged in
        if (currentUser != null) {
            fetchProducts(null); // Show all categories by default
        }

        // Checkout button listener to reopen the cart
        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, Checkout.class);
            // Start the Checkout activity to reopen the cart
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

        // Add the SideMenuFragment to the activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sideMenus, new SideMenuFragment())
                    .commit();
        }
    }

    // Method to fetch products from Firestore
    private void fetchProducts(String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("products")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            productList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                product.setId(document.getId());

                                if (category == null || product.getCategory().equalsIgnoreCase(category)) {
                                    productList.add(product);
                                }
                            }
                            // Notify adapter of data changes
                            runOnUiThread(() -> productAdapter.notifyDataSetChanged());
                        }
                    });
        }
    }
}
