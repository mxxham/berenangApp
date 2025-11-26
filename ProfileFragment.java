package com.example.berenang10;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList; // 👈 Import required
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.button.MaterialButton; // 👈 Import required (Best practice)

// Change Button to MaterialButton for better compatibility with backgroundTintList
public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private SharedPreferences prefs;

    // Corrected View References for XML layout
    private ShapeableImageView profileImage;
    private View profileImageContainer;

    private TextView userNameText;
    private TextView userEmailText;
    private Button editProfileBtn;
    private Button settingsBtn;
    private MaterialButton logoutBtn; // 👈 Changed type to MaterialButton

    // ActivityResultLauncher for permission handling
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupPermissionLauncher();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Initialize SharedPreferences
        prefs = requireActivity().getSharedPreferences("BerenangPrefs", Context.MODE_PRIVATE);

        // 2. Initialize views
        profileImage = view.findViewById(R.id.profile_image);
        profileImageContainer = view.findViewById(R.id.profile_image_container);

        userNameText = view.findViewById(R.id.user_name);
        userEmailText = view.findViewById(R.id.user_email);
        editProfileBtn = view.findViewById(R.id.edit_profile_btn);
        settingsBtn = view.findViewById(R.id.settings_btn);
        logoutBtn = view.findViewById(R.id.logout_btn); // Now cast to MaterialButton

        // 3. Display profile data and setup buttons
        displayUserProfile();
        setupActionButtons();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserProfile();
    }

    // --- Setup the permission handling logic ---
    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImageChooser();
                    } else {
                        Toast.makeText(getContext(), "Permission denied. Cannot select photo.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // --- New entry point for photo change ---
    private void checkAndOpenImageChooser() {
        if (!prefs.getBoolean("is_logged_in", false)) {
            Toast.makeText(getContext(), "Please login to set a profile picture.", Toast.LENGTH_SHORT).show();
            return;
        }

        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else { // Android 12 and below
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openImageChooser();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }


    // --- Core Method for image selection (No change here) ---
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // --- Override to handle image selection result (No change here) ---
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK
                && data != null && data.getData() != null) {

            Uri imageUri = data.getData();

            prefs.edit()
                    .putString("profile_image_uri_" + prefs.getString("current_user_email", ""), imageUri.toString())
                    .apply();

            Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(profileImage);

            Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUserProfile() {
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            String userEmail = prefs.getString("current_user_email", "user@example.com");
            String userName = prefs.getString("user_name", "User");

            userNameText.setText(userName);
            userEmailText.setText(userEmail);
            logoutBtn.setText("Logout");
            editProfileBtn.setVisibility(View.VISIBLE);
            settingsBtn.setVisibility(View.VISIBLE);

            // 🎨 Set Logout button color to RED
            int redColor = ContextCompat.getColor(requireContext(), R.color.error);
            logoutBtn.setBackgroundTintList(ColorStateList.valueOf(redColor));
            logoutBtn.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_logout));


            // Image Loading Logic
            String profileUriString = prefs.getString("profile_image_uri_" + userEmail, null);
            Uri profileUri = (profileUriString != null) ? Uri.parse(profileUriString) : null;

            Glide.with(requireContext())
                    .load(profileUri)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(profileImage);

            profileImageContainer.setVisibility(View.VISIBLE);


        } else {
            // Guest state
            userNameText.setText("Guest User");
            userEmailText.setText("Tap 'Login' to manage your account");
            logoutBtn.setText("Login");
            editProfileBtn.setVisibility(View.GONE);
            settingsBtn.setVisibility(View.GONE);

            // 🎨 Set Login button color to BLUE (primary color)
            int blueColor = ContextCompat.getColor(requireContext(), R.color.primary); // Using primary for blue
            logoutBtn.setBackgroundTintList(ColorStateList.valueOf(blueColor));
            logoutBtn.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_login)); // Assuming you have an ic_login drawable

            // Guest Image State
            profileImage.setImageResource(R.drawable.ic_default_avatar);
            profileImageContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setupActionButtons() {
        editProfileBtn.setOnClickListener(v ->
                Toast.makeText(getContext(), "Edit Profile feature coming soon!", Toast.LENGTH_SHORT).show());

        settingsBtn.setOnClickListener(v ->
                Toast.makeText(getContext(), "Settings feature coming soon!", Toast.LENGTH_SHORT).show());

        profileImageContainer.setOnClickListener(v -> checkAndOpenImageChooser());

        logoutBtn.setOnClickListener(v -> {
            boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
            if (isLoggedIn) {
                showLogoutDialog();
            } else {
                // Navigate to LoginActivity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform Logout
                    prefs.edit()
                            .putBoolean("is_logged_in", false)
                            .remove("current_user_email")
                            .apply();

                    Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

                    // Refresh fragment to show guest state
                    displayUserProfile();
                })
                .setNegativeButton("No", null)
                .show();
    }
}