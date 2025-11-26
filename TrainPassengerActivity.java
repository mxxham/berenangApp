package com.example.berenang10;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton; // ADDED: Import for ImageButton
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Locale;

public class TrainPassengerActivity extends AppCompatActivity {

    private static final String TAG = "TrainPassengerActivity";

    // Declare variables for UI elements
    private ImageButton backButtonImage; // CHANGED: Now targeting the inner ImageButton for clicks
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText idNumberEditText;
    private MaterialButton continueButton;

    // CRITICAL FIX: TextViews to display the selected Train information in the summary card
    private TextView summaryTitleText;
    private TextView summaryRouteTimeText;
    private TextView summaryTotalText;

    // Variable to hold the selected Train object
    private Train selectedTrain;
    // Variable to hold the passenger count
    private int passengerCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_passenger);

        // 1. Initialize UI elements
        // CHANGED: Initializing the ImageButton with its ID (R.id.back_button)
        backButtonImage = findViewById(R.id.back_button);

        // CRITICAL FIX: Initialize the summary card TextViews (Assuming these IDs exist in your XML)
        summaryTitleText = findViewById(R.id.text_summary_title);
        summaryRouteTimeText = findViewById(R.id.text_summary_route_time);
        summaryTotalText = findViewById(R.id.text_summary_total);

        fullNameEditText = findViewById(R.id.full_name_input);
        emailEditText = findViewById(R.id.email_input);
        phoneEditText = findViewById(R.id.phone_input);
        idNumberEditText = findViewById(R.id.id_number_input);
        continueButton = findViewById(R.id.continue_button);

        // 2. Set up back button click listener
        backButtonImage.setOnClickListener(v -> { // Listener attached directly to the ImageButton
            onBackPressed();
        });


        // 3. Retrieve data from the calling activity (TrainResultsActivity)
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                selectedTrain = intent.getParcelableExtra("selected_train", Train.class);
            } else {
                selectedTrain = (Train) intent.getParcelableExtra("selected_train");
            }

            this.passengerCount = intent.getIntExtra("passenger_count", 1);

            if (selectedTrain != null) {
                Log.d(TAG, "Selected Train: " + selectedTrain.getTrainName() + ", Count: " + passengerCount);
                // CRITICAL FIX: Update UI with actual Train data
                updateSummaryUi();
            } else {
                Toast.makeText(this, "Error: Selected train data is missing.", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error: Missing intent data.", Toast.LENGTH_LONG).show();
            finish();
        }

        // 4. Set up continue button click listener
        continueButton.setOnClickListener(v -> {
            if (selectedTrain != null) {
                collectAndValidatePassengerData();
            } else {
                Toast.makeText(this, "Cannot continue, train data is missing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the summary card with the correct train details and calculated price.
     */
    private void updateSummaryUi() {
        if (selectedTrain == null) return;

        // 1. Update Title to show Train Name and Class
        if (summaryTitleText != null) {
            summaryTitleText.setText(String.format("Selected Train: %s (%s)",
                    selectedTrain.getTrainName(),
                    selectedTrain.getTrainClass()));
        }

        // 2. Update Route and Time details
        String routeDetails = String.format("%s → %s\n%s - %s | %d Passenger%s",
                selectedTrain.getOrigin(),
                selectedTrain.getDestination(),
                selectedTrain.getDepartureTime(),
                selectedTrain.getArrivalTime(),
                passengerCount,
                passengerCount > 1 ? "s" : ""
        );

        if (summaryRouteTimeText != null) {
            summaryRouteTimeText.setText(routeDetails);
        }

        // 3. Update Total Price
        double totalPrice = selectedTrain.getPrice() * passengerCount;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        currencyFormat.setMinimumFractionDigits(0);
        String formattedPrice = "Total: " + currencyFormat.format(totalPrice);

        if (summaryTotalText != null) {
            summaryTotalText.setText(formattedPrice);
        }
    }


    private void collectAndValidatePassengerData() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String idNumber = idNumberEditText.getText().toString().trim();

        // 1. Check for empty fields
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || idNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all passenger details.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Basic Email Validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. KTP/ID Number Length Validation (Assuming KTP is 16 digits)
        if (idNumber.length() != 16) {
            Toast.makeText(this, "ID Card Number (KTP) must be exactly 16 digits.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If validation passes, create Passenger object
        Passenger passenger = new Passenger(fullName, email, phone, idNumber);

        // Start the next activity (PaymentActivity)
        Intent intent = new Intent(this, PaymentActivity.class);

        // Pass the train object, the new passenger object, and the count
        intent.putExtra("selected_train", selectedTrain);
        intent.putExtra("passenger_data", passenger);
        intent.putExtra("passenger_count", passengerCount);

        startActivity(intent);
    }
}