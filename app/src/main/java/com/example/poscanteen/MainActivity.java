package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DIALOGUE = 1;
    private Button login;
    private TextView register;
    private EditText username, password;
    private LinearLayout productClickLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this matches your actual layout file

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.regtxt);

        productClickLayout = findViewById(R.id.productclick); // Make sure this ID matches in your layout file

        login.setOnClickListener(v -> {
            String email = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (validateInputs(email, pass)) {
                loginUser(email, pass);
            }
        });

        register.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, register.class);
            startActivity(intent);
        });

        // Open the custom dialogue when the product item is clicked
        if (productClickLayout != null) { // Check if productClickLayout is not null
            productClickLayout.setOnClickListener(v -> {
                db.collection("products") // Fetch the product from Firestore
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String productId = document.getId(); // Get the product ID
                                    CustomDialogue customDialogue = new CustomDialogue(MainActivity.this);
                                    customDialogue.show(); // Show the dialog with the fetched product ID
                                    break; // Remove if you want to show for each product
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            username.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            this.password.setError("Password is required");
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DIALOGUE && resultCode == RESULT_OK) {
            if (data != null) {
                int selectedQuantity = data.getIntExtra("selected_quantity", 1);
                Toast.makeText(this, "Selected Quantity: " + selectedQuantity, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
