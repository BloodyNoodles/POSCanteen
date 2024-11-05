package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.widget.ImageButton;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.FacebookException;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DIALOGUE = 1;
    private Button login;
    private TextView register;
    private EditText username, password;
    private LinearLayout productClickLayout;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.activity_main);

        // Firebase and Firestore instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Facebook Login setup
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(new Intent(MainActivity.this, home.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // Handle login cancel
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // Handle login error
                    }
                });

        ImageButton btnFacebookLogin = findViewById(R.id.btn_facebook_login);
        btnFacebookLogin.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
        });

        // Firebase authentication
        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.regtxt);

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

        // Setup product click listener
        setupProductClickListener();
    }

    private void setupProductClickListener() {
        View productView = getLayoutInflater().inflate(R.layout.recycler_newproduct, null);
        productClickLayout = productView.findViewById(R.id.productclick);

        if (productClickLayout != null) {
            productClickLayout.setOnClickListener(v -> {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Access the 'products' subcollection of the current user
                    db.collection("users")
                            .document(userId)
                            .collection("products")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Convert document to Product object
                                        Product product = document.toObject(Product.class);

                                        // Check if product is not null and contains addOns and sizes
                                        if (product != null) {
                                            CustomDialogue customDialogue = new CustomDialogue(MainActivity.this, product);
                                            customDialogue.show();
                                            break; // Show only one product's dialog for this example
                                        }
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DIALOGUE && resultCode == RESULT_OK) {
            if (data != null) {
                int selectedQuantity = data.getIntExtra("selected_quantity", 1);
                Toast.makeText(this, "Selected Quantity: " + selectedQuantity, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Validation and login methods
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
                        if (user != null && user.isEmailVerified()) {
                            startActivity(new Intent(MainActivity.this, home.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
