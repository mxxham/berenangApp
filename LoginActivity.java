package com.example.berenang10;

import android.content.Intent; // NEW: Added this import
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLinkText;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences("BerenangPrefs", MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupLoginButton();
        setupRegisterLink();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerLinkText = findViewById(R.id.register_link);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (validateInputs(email, password)) {
                performLogin(email, password);
            }
        });
    }

    // --- UPDATED METHOD ---
    private void setupRegisterLink() {
        registerLinkText.setOnClickListener(v -> {
            // Explicitly start the RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);

            // Close the current LoginActivity
            finish();
        });
    }
    // -----------------------

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        new Handler().postDelayed(() -> {
            // Validate user from database
            if (databaseHelper.validateUser(email, password)) {
                // Get user name from database
                String userName = databaseHelper.getUserName(email);

                // Save session to SharedPreferences
                prefs.edit()
                        .putBoolean("is_logged_in", true)
                        .putString("current_user_email", email)
                        .putString("user_name", userName)
                        .apply();

                Toast.makeText(this, "Login successful! Welcome back, " + userName, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                // Login failed
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }
}