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
import androidx.activity.OnBackPressedCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;

public class SideMenuFragment extends Fragment {

    private boolean isMenuVisible = false;
    private LinearLayout sideMenu;
    FrameLayout sideMenuLayout;
    LinearLayout home;
    MaterialTextView transactionHistory;
    RelativeLayout profile, addProduct;
    private View blockingView;  // The view to block clicks behind the menu

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_menu, container, false);

        // Initialize UI components
        sideMenu = view.findViewById(R.id.sideMenu);
        sideMenuLayout = getActivity().findViewById(R.id.sideMenus); // Accessing the activity's layout
        ImageButton sideBtn = view.findViewById(R.id.sideBtn);
        addProduct = view.findViewById(R.id.addProduct);
        home = view.findViewById(R.id.home);
        transactionHistory = view.findViewById(R.id.transactionId);
        profile = view.findViewById(R.id.profile);
        blockingView = view.findViewById(R.id.blockingView);  // Initialize blockingView

        // Set the sideMenuLayout visibility to GONE initially
        sideMenuLayout.setVisibility(View.GONE);
        blockingView.setVisibility(View.GONE);  // Hide blocking view initially

        // Toggle side menu visibility on sideBtn click
        sideBtn.setOnClickListener(v -> toggleSideMenu());

        // Navigation without piling up fragments in the back stack
        addProduct.setOnClickListener(v -> {
            if (!isCurrentActivity(AddProductActivity.class)) {
                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                startActivity(intent);
            }
        });

        transactionHistory.setOnClickListener(v -> {
            if (!isCurrentActivity(transactionHistory.class)) {
                Intent intent = new Intent(getActivity(), transactionHistory.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(v -> {
            if (!isCurrentActivity(home.class)) {
                // Navigate without adding to the back stack (to prevent piling)
                Intent intent = new Intent(getActivity(), home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(v -> {
            if (!isCurrentActivity(com.example.poscanteen.profile.class)) {
                Intent intent = new Intent(getActivity(), com.example.poscanteen.profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
                if (isMenuVisible) {
                    toggleSideMenu();  // Close the side menu if it is open
                } else {
                    toggleSideMenu();  // Open the side menu if it is closed
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
