package com.example.berenang10;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns; // <-- New Import
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView loginLinkText;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private PassengerData passengerData;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefs = getSharedPreferences("BerenangPrefs", MODE_PRIVATE);
        passengerData = getIntent().getParcelableExtra("passenger_data");
        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        prefillData();
        setupRegisterButton();
        setupLoginLink();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        loginLinkText = findViewById(R.id.login_link);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void prefillData() {
        if (passengerData != null) {
            nameInput.setText(passengerData.getFullName());
            emailInput.setText(passengerData.getEmail());
        }
    }

    private void setupRegisterButton() {
        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (validateInputs(name, email, password, confirmPassword)) {
                // The plain text password is passed here.
                // It will be hashed inside DatabaseHelper.registerUser.
                performRegistration(name, email, password);
            }
        });
    }

    private void setupLoginLink() {
        loginLinkText.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // --- UPDATED: Use Patterns.EMAIL_ADDRESS for robust validation ---
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        // -----------------------------------------------------------------

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performRegistration(String name, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        new Handler().postDelayed(() -> {
            // Check if user already exists
            if (databaseHelper.checkUserExists(email)) {
                progressBar.setVisibility(View.GONE);
                registerButton.setEnabled(true);
                Toast.makeText(this, "Email already registered. Please login.", Toast.LENGTH_LONG).show();
                return;
            }

            // Register new user in database (DatabaseHelper will hash the password here)
            long result = databaseHelper.registerUser(name, email, password);

            if (result != -1) {
                // Save session to SharedPreferences
                prefs.edit()
                        .putString("user_name", name)
                        .putString("user_email", email)
                        .putBoolean("is_logged_in", true)
                        .putString("current_user_email", email)
                        .apply();

                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                registerButton.setEnabled(true);
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }
}