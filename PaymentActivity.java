package com.example.berenang10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity {

    private Train selectedTrain;
    private Flight selectedFlight; // Variable to hold Flight data

    // NEW: Variables for Event booking data
    private String bookingType; // Stores "train", "flight", or "event"
    private String eventTitle;
    private String eventSubtitle;
    private String eventBookingDate;
    private int eventItemCount;
    private double eventPricePerItem;
    // END NEW

    private int passengerCount;
    private Passenger passengerData;
    private TextView bookingSummaryText, totalAmountText;
    private RadioGroup paymentMethodGroup;
    private Button payButton;
    private ProgressBar progressBar;
    private double totalAmount;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();

        // --- 1. DATA RETRIEVAL ---
        bookingType = intent.getStringExtra("booking_type"); // Retrieve the new type key

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            selectedTrain = intent.getParcelableExtra("selected_train", Train.class);
            selectedFlight = intent.getParcelableExtra("selected_flight", Flight.class);
            passengerData = intent.getParcelableExtra("passenger_data", Passenger.class);
        } else {
            selectedTrain = (Train) intent.getParcelableExtra("selected_train");
            selectedFlight = (Flight) intent.getParcelableExtra("selected_flight");
            passengerData = (Passenger) intent.getParcelableExtra("passenger_data");
        }
        passengerCount = intent.getIntExtra("passenger_count", 1);


        // --- NEW: Retrieve Event Data if applicable ---
        if ("event".equals(bookingType)) {
            eventTitle = intent.getStringExtra("event_title");
            eventSubtitle = intent.getStringExtra("event_subtitle");
            eventBookingDate = intent.getStringExtra("event_booking_date");
            eventItemCount = intent.getIntExtra("event_item_count", 1);
            eventPricePerItem = intent.getDoubleExtra("event_price_per_item", 0.0);

            // For event, use eventItemCount as the main count
            passengerCount = eventItemCount;
        }

        // --- 3. Basic check: Allow Train, Flight, or Event to proceed ---
        boolean isTrainOrFlight = selectedTrain != null || selectedFlight != null;
        boolean isEvent = "event".equals(bookingType) && eventTitle != null;

        if (passengerData == null || (!isTrainOrFlight && !isEvent)) {
            Toast.makeText(this, "Booking data missing or unrecognized type. Cannot proceed.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        displayBookingSummary();
        setupPayButton();
    }

    private void initializeViews() {
        bookingSummaryText = findViewById(R.id.booking_summary_text);
        totalAmountText = findViewById(R.id.total_amount_text);
        paymentMethodGroup = findViewById(R.id.payment_method_group);
        payButton = findViewById(R.id.pay_button);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void displayBookingSummary() {
        String summary = "Guest/Primary Passenger: " + passengerData.getFullName() + "\n";
        double pricePerItem = 0;
        int count = passengerCount; // Item/Guest count

        // --- DYNAMIC SUMMARY BASED ON BOOKING TYPE (UPDATED) ---
        if (selectedTrain != null) {
            bookingType = "train";
            summary += "Train: " + selectedTrain.getTrainName() + " (" + selectedTrain.getTrainClass() + ")" + "\n" +
                    "Route: " + selectedTrain.getOrigin() + " → " + selectedTrain.getDestination() + "\n" +
                    "Departure Time: " + selectedTrain.getDepartureTime() + "\n" +
                    "Arrival Time: " + selectedTrain.getArrivalTime() + "\n";
            pricePerItem = selectedTrain.getPrice();

        } else if (selectedFlight != null) {
            bookingType = "flight";
            summary += "Flight: " + selectedFlight.getAirline() + " " + selectedFlight.getFlightNumber() + " (" + selectedFlight.getSeatClass() + ")" + "\n" +
                    "Route: " + selectedFlight.getOrigin() + " → " + selectedFlight.getDestination() + "\n" +
                    "Departure Time: " + selectedFlight.getDepartureTime() + "\n" +
                    "Arrival Time: " + selectedFlight.getArrivalTime() + "\n";
            pricePerItem = selectedFlight.getPrice();
        } else if ("event".equals(bookingType)) {
            // NEW: Event logic
            summary += "Activity: " + eventTitle + "\n" +
                    "Details: " + eventSubtitle + "\n" +
                    "Booking Date: " + eventBookingDate + "\n";
            pricePerItem = eventPricePerItem;
            count = eventItemCount;
        }

        summary += "Items/Guests: " + count;
        bookingSummaryText.setText(summary);

        // Calculate total amount
        totalAmount = pricePerItem * count;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        totalAmountText.setText(currencyFormat.format(totalAmount));
    }

    private void setupPayButton() {
        payButton.setOnClickListener(v -> {
            int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedMethod = findViewById(selectedId);
            String paymentMethod = selectedMethod.getText().toString();

            processPayment(paymentMethod);
        });
    }

    private void processPayment(String paymentMethod) {
        progressBar.setVisibility(View.VISIBLE);
        payButton.setEnabled(false);

        // Phase 1: Payment Authorization (2 seconds)
        new Handler().postDelayed(() -> {
            Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show();

            // Phase 2: Inventory Hold & PNR Generation (2 seconds)
            new Handler().postDelayed(() -> {
                String pnr = generatePNR();

                // Phase 3: Booking Commitment (1 second)
                new Handler().postDelayed(() -> {
                    // This now attempts to save the data using the provided DatabaseHelper
                    boolean saved = saveBookingToDatabase(pnr, paymentMethod);

                    if (saved) {
                        // Show success dialog
                        showSuccessDialog(pnr);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        payButton.setEnabled(true);
                        Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }, 2000);
        }, 2000);
    }

    private String generatePNR() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder pnr = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            pnr.append(chars.charAt(random.nextInt(chars.length())));
        }
        return pnr.toString();
    }

    /**
     * Saves the confirmed booking details to the SQLite database, handling all booking types (Train, Flight, Event).
     */
    private boolean saveBookingToDatabase(String pnr, String paymentMethod) {
        SharedPreferences prefs = getSharedPreferences("BerenangPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("current_user_email", "");

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "User session error. Cannot save booking.", Toast.LENGTH_LONG).show();
            return false;
        }

        String type;
        String title;
        String details;

        // --- DYNAMIC DATA EXTRACTION AND FORMATTING (UPDATED) ---
        if (selectedTrain != null) {
            type = "train";
            title = selectedTrain.getTrainName() + " (" + selectedTrain.getTrainClass() + ")";
            details = "Route: " + selectedTrain.getOrigin() + " → " + selectedTrain.getDestination() + "\n" +
                    "Time: " + selectedTrain.getDepartureTime() + " - " + selectedTrain.getArrivalTime() + "\n" +
                    "Duration: " + selectedTrain.getDuration() + "\n" +
                    "Passenger: " + passengerData.getFullName() + "\n" +
                    "Email: " + passengerData.getEmail() + "\n" +
                    "Phone: " + passengerData.getPhone() + "\n" +
                    "Class: " + selectedTrain.getTrainClass() + "\n" +
                    "Passengers: " + passengerCount;
        } else if (selectedFlight != null) {
            type = "flight";
            title = selectedFlight.getAirline() + " " + selectedFlight.getFlightNumber() + " (" + selectedFlight.getSeatClass() + ")";
            details = "Route: " + selectedFlight.getOrigin() + " → " + selectedFlight.getDestination() + "\n" +
                    "Time: " + selectedFlight.getDepartureTime() + " - " + selectedFlight.getArrivalTime() + "\n" +
                    "Passenger: " + passengerData.getFullName() + "\n" +
                    "Email: " + passengerData.getEmail() + "\n" +
                    "Phone: " + passengerData.getPhone() + "\n" +
                    "Class: " + selectedFlight.getSeatClass() + "\n" +
                    "Passengers: " + passengerCount;
        } else if ("event".equals(bookingType)) {
            // NEW: Event Save Logic
            type = "event";
            title = eventTitle;
            details = "Booking Date: " + eventBookingDate + "\n" +
                    "Details: " + eventSubtitle + "\n" +
                    "Guest: " + passengerData.getFullName() + "\n" +
                    "Email: " + passengerData.getEmail() + "\n" +
                    "Phone: " + passengerData.getPhone() + "\n" +
                    "Quantity: " + eventItemCount;
        } else {
            // Fallback
            return false;
        }

        // Save to database
        long result = databaseHelper.addBooking(
                pnr,
                type, // Use the determined type
                userEmail,
                title,
                details,
                totalAmount,
                "Confirmed",
                System.currentTimeMillis(),
                paymentMethod
        );

        return result != -1;
    }

    private void showSuccessDialog(String pnr) {
        progressBar.setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✓ Booking Confirmed!");

        String message = "Your booking has been confirmed!\n\n" +
                "PNR: " + pnr + "\n" +
                "Booking ID: BRG-" + pnr + "\n\n" +
                "E-ticket will be sent to:\n" + passengerData.getEmail() + "\n\n" +
                "Please check your email within 60 minutes. If not received, you can check your booking status in 'My Bookings' section.";

        builder.setMessage(message);
        builder.setPositiveButton("View Booking", (dialog, which) -> {
            // Since Event, Train, and Flight bookings all use the same PNR/Booking ID structure
            // we can confidently launch the detail screen, assuming it exists:
            Intent intent = new Intent(PaymentActivity.this, BookingDatabaseDetailActivity.class);
            intent.putExtra("pnr", pnr);
            // Clear the stack and start the detail activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Done", (dialog, which) -> {
            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }
}