package com.example.poscanteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SideMenuFragment extends Fragment {

    private boolean isMenuVisible = false;
    private LinearLayout sideMenu;
    FrameLayout sideMenuLayout;
    LinearLayout home;
    RelativeLayout profile, addProduct, transactionId;
    private View blockingView;  // The view to block clicks behind the menu
    private FirebaseAuth mAuth;  // Declare FirebaseAuth

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_menu, container, false);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        sideMenu = view.findViewById(R.id.sideMenu);
        sideMenuLayout = getActivity().findViewById(R.id.sideMenus); // Accessing the activity's layout
        ImageButton sideBtn = view.findViewById(R.id.sideBtn);
        addProduct = view.findViewById(R.id.addProduct);
        home = view.findViewById(R.id.home);
        transactionId = view.findViewById(R.id.transactionId);
        profile = view.findViewById(R.id.profile);
        blockingView = view.findViewById(R.id.blockingView);  // Initialize blockingView

        // Set the sideMenuLayout visibility to GONE initially
        sideMenuLayout.setVisibility(View.GONE);
        blockingView.setVisibility(View.GONE);  // Hide blocking view initially

        // Toggle side menu visibility on sideBtn click
        sideBtn.setOnClickListener(v -> toggleSideMenu());

        blockingView.setOnClickListener(v -> {
            if (isMenuVisible) {
                toggleSideMenu();
            }
        });


        home.setOnClickListener(v -> {
            if (isCurrentActivity(home.class)) {
                toggleSideMenu();  // Close side menu if already on Home
            } else {
                toggleSideMenu();  // Close the side menu
                // Delay the start of the new activity until the menu is closed
                home.postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }, 300);  // Delay in milliseconds to allow animation to finish
            }
        });

        profile.setOnClickListener(v -> {
            if (isCurrentActivity(com.example.poscanteen.profile.class)) {
                toggleSideMenu();  // Close side menu if already on Profile
            } else {
                toggleSideMenu();  // Close the side menu
                profile.postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), com.example.poscanteen.profile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }, 300);  // Delay in milliseconds to allow animation to finish
            }
        });

        addProduct.setOnClickListener(v -> {
            if (isCurrentActivity(AddProductActivity.class)) {
                toggleSideMenu();  // Close side menu if already on Add Product
            } else {
                toggleSideMenu();  // Close the side menu
                addProduct.postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), AddProductActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }, 300);  // Delay in milliseconds to allow animation to finish
            }
        });

        transactionId.setOnClickListener(v -> {
            if (isCurrentActivity(com.example.poscanteen.transactionHistory.class)) {
                toggleSideMenu();  // Close side menu if already on Transaction History
            } else {
                toggleSideMenu();  // Close the side menu
                transactionId.postDelayed(() -> {
                    Intent intent = new Intent(getActivity(), com.example.poscanteen.transactionHistory.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }, 300);  // Delay in milliseconds to allow animation to finish
            }
        });



        // Logout button
        View logoutButton = view.findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(v -> {
            if (mAuth != null) {
                mAuth.signOut();  // Firebase sign out
                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);  // Redirect to LoginActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear back stack
                startActivity(intent);
                getActivity().finish();  // Close the current activity
            } else {
                Toast.makeText(getContext(), "FirebaseAuth is null", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Define the activities where side menu can be toggled
                if (isCurrentActivity(home.class) || isCurrentActivity(profile.class) || isCurrentActivity(transactionHistory.class) || isCurrentActivity(AddProductActivity.class)) {
                    // If in one of the allowed activities, toggle the side menu
                    if (isMenuVisible) {
                        toggleSideMenu();  // Close the side menu if it is open
                    } else {
                        toggleSideMenu();  // Open the side menu if it is closed
                    }
                } else if (isCurrentActivity(checkout.class)) {
                    // If in the CheckoutActivity, go back to the home screen
                    Intent intent = new Intent(getActivity(), home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // No need to call finish(); let Android manage the activity lifecycle
                } else {
                    // Default back behavior for all other activities
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    // Method to check if the current activity is the same as the one intended to navigate to
    private boolean isCurrentActivity(Class<?> activityClass) {
        return getActivity() != null && getActivity().getClass().equals(activityClass);
    }

    // Method to toggle the side menu's visibility
    public void toggleSideMenu() {
        if (!isMenuVisible) {
            // Show blocking view to prevent interaction with background
            blockingView.setVisibility(View.VISIBLE);

            // Slide in the menu
            sideMenuLayout.setVisibility(View.VISIBLE);
            Animation slideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.menu_slide_in);
            sideMenu.startAnimation(slideIn);
            sideMenu.setVisibility(View.VISIBLE);
            isMenuVisible = true;
        } else {
            // Slide out the menu
            Animation slideOut = AnimationUtils.loadAnimation(getActivity(), R.anim.menu_slide_out);
            sideMenu.startAnimation(slideOut);
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    sideMenuLayout.setVisibility(View.GONE);
                    blockingView.setVisibility(View.GONE);  // Hide blocking view after menu closes
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            isMenuVisible = false;
        }
    }
}
