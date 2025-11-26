package com.example.berenang10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class PassengerDetailsActivity extends AppCompatActivity {

    private Flight selectedFlight;
    private FlightSearchParams searchParams;
    private EditText nameInput, emailInput, phoneInput, passportInput;
    private TextView flightSummaryText, totalPriceText;
    private Button continueButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_details);

        // Retrieve data using old and new methods for compatibility
        selectedFlight = getIntent().getParcelableExtra("flight");
        searchParams = getIntent().getParcelableExtra("search_params");
        prefs = getSharedPreferences("BerenangPrefs", MODE_PRIVATE);

        // Basic check to prevent crashes if coming from a bad Intent
        if (selectedFlight == null || searchParams == null) {
            Toast.makeText(this, "Internal data error. Try searching again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        displayFlightSummary();
        setupContinueButton();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        passportInput = findViewById(R.id.passport_input);
        flightSummaryText = findViewById(R.id.flight_summary_text);
        totalPriceText = findViewById(R.id.total_price_text);
        continueButton = findViewById(R.id.continue_button);
    }

    private void displayFlightSummary() {
        String summary = selectedFlight.getAirline() + " " + selectedFlight.getFlightNumber() + "\n" +
                selectedFlight.getOrigin() + " → " + selectedFlight.getDestination() + "\n" +
                searchParams.getDepartureDate() + " • " + selectedFlight.getDepartureTime();
        flightSummaryText.setText(summary);

        double totalPrice = selectedFlight.getPrice() * searchParams.getPassengers();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        totalPriceText.setText("Total: " + currencyFormat.format(totalPrice));
    }

    private void setupContinueButton() {
        continueButton.setOnClickListener(v -> {
            if (validateInputs()) {
                checkAuthenticationStatus();
            }
        });
    }

    private boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String passport = passportInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter passenger name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passport.isEmpty()) {
            Toast.makeText(this, "Please enter passport/ID number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkAuthenticationStatus() {
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            // User is logged in, proceed to payment
            proceedToPayment();
        } else {
            // Show login/register dialog
            showAuthenticationDialog();
        }
    }

    private void showAuthenticationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Authentication Required");
        builder.setMessage("Please login or register to continue with your booking. This is required for managing bookings, e-tickets, and refund requests.");
        builder.setPositiveButton("Login", (dialog, which) -> {
            Intent intent = new Intent(PassengerDetailsActivity.this, LoginActivity.class);
            intent.putExtra("passenger_data", createPassengerObject());
            startActivityForResult(intent, 100);
        });
        builder.setNegativeButton("Register", (dialog, which) -> {
            Intent intent = new Intent(PassengerDetailsActivity.this, RegisterActivity.class);
            intent.putExtra("passenger_data", createPassengerObject());
            startActivityForResult(intent, 101);
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * Creates and returns a Parcelable Passenger object, matching the expected type in PaymentActivity.
     */
    private Passenger createPassengerObject() {
        return new Passenger(
                nameInput.getText().toString().trim(),
                emailInput.getText().toString().trim(),
                phoneInput.getText().toString().trim(),
                passportInput.getText().toString().trim()
        );
    }

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);

        // FIX 1: Pass the flight data using the key "selected_flight" (as expected by PaymentActivity)
        intent.putExtra("selected_flight", selectedFlight);

        // FIX 2: Pass the passenger count using the key "passenger_count" (as expected by PaymentActivity)
        intent.putExtra("passenger_count", searchParams.getPassengers());

        // FIX 3: Pass the Passenger object using the key "passenger_data"
        intent.putExtra("passenger_data", createPassengerObject());

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // User logged in or registered successfully
            checkAuthenticationStatus();
        }
    }
}