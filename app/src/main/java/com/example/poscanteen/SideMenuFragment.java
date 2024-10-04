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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;

public class SideMenuFragment extends Fragment {

    private boolean isMenuVisible = false;
    private LinearLayout sideMenu;                                     // WAG NYO TOH GALAWINNNN
    FrameLayout sideMenuLayout;                                        // WAG NYO TOH GALAWINNNN
    LinearLayout home;
    MaterialTextView transactionHistory;
    RelativeLayout profile, addProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_menu, container, false);

        sideMenu = view.findViewById(R.id.sideMenu);
        sideMenuLayout = getActivity().findViewById(R.id.sideMenus); // Accessing the activity's layout
        ImageButton sideBtn = view.findViewById(R.id.sideBtn);
        addProduct = view.findViewById(R.id.addProduct);
        home = view.findViewById(R.id.home);
        transactionHistory = view.findViewById(R.id.transactionId);
        profile =view.findViewById(R.id.profile);
        // Set the sideMenuLayout visibility to GONE initially
        sideMenuLayout.setVisibility(View.GONE);

        // Toggle side menu visibility on sideBtn click
        sideBtn.setOnClickListener(v -> toggleSideMenu());

        addProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class); // Use your correct class name
            startActivity(intent);
        });

        transactionHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), transactionHistory.class); // Correct class name
            startActivity(intent);
        });


        home.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), home.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.poscanteen.profile.class); // Correct class name for profile
            startActivity(intent);
        });


        return view;
    }

    // Method to toggle the side menu's visibility
    public void toggleSideMenu() {
        if (!isMenuVisible) {
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
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            isMenuVisible = false;
        }
    }
}