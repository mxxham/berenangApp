package com.example.berenang10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HotelGuestDetailsActivity extends AppCompatActivity {

    // Keys for saving and restoring state
    private static final String STATE_HOTEL = "saved_hotel_data";
    private static final String STATE_SEARCH_PARAMS = "saved_search_params_data";

    private Hotel hotel;
    private HotelSearchParams searchParams;
    private EditText nameInput, emailInput, phoneInput, idNumberInput;
    private TextView hotelSummaryText, totalPriceText;
    private Button continueButton;
    private ImageView backButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_guest_details);

        prefs = getSharedPreferences("BerenangPrefs", MODE_PRIVATE);

        // --- FIX START: Data Restoration Logic ---
        // 1. Try to restore data if the activity was recreated (e.g., after returning from Login)
        if (savedInstanceState != null) {
            hotel = savedInstanceState.getParcelable(STATE_HOTEL);
            searchParams = savedInstanceState.getParcelable(STATE_SEARCH_PARAMS);
        }

        // 2. If data is still null, retrieve it from the Intent (initial launch)
        if (hotel == null || searchParams == null) {
            hotel = getIntent().getParcelableExtra("hotel");
            searchParams = getIntent().getParcelableExtra("search_params");
        }

        // Safety check: if data is still missing, we cannot proceed
        if (hotel == null || searchParams == null) {
            Toast.makeText(this, "Initial booking data could not be retrieved. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // --- FIX END ---


        initializeViews();
        displaySummary();
        setupContinueButton();
    }

    // --- NEW METHOD: Save Data on Lifecycle Change ---
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the Parcelable objects so they can be restored in onCreate if the activity is destroyed
        outState.putParcelable(STATE_HOTEL, hotel);
        outState.putParcelable(STATE_SEARCH_PARAMS, searchParams);
    }
    // --- END NEW METHOD ---

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        idNumberInput = findViewById(R.id.id_number_input);
        hotelSummaryText = findViewById(R.id.hotel_summary_text);
        totalPriceText = findViewById(R.id.total_price_text);
        continueButton = findViewById(R.id.continue_button);

        backButton.setOnClickListener(v -> finish());
    }

    private void displaySummary() {
        String summary = hotel.getName() + "\n" +
                hotel.getRoomType() + "\n" +
                searchParams.getCheckInDate() + " - " + searchParams.getCheckOutDate() + "\n" +
                searchParams.getGuests() + " Guest(s), " + searchParams.getRooms() + " Room(s)";
        hotelSummaryText.setText(summary);

        // Calculate total nights
        int nights = calculateNights(searchParams.getCheckInDate(), searchParams.getCheckOutDate());
        double totalPrice = hotel.getPricePerNight() * nights * searchParams.getRooms();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        totalPriceText.setText("Total (" + nights + " nights): " + currencyFormat.format(totalPrice));
    }

    private int calculateNights(String checkIn, String checkOut) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            Date dateCheckIn = sdf.parse(checkIn);
            Date dateCheckOut = sdf.parse(checkOut);
            long diff = dateCheckOut.getTime() - dateCheckIn.getTime();
            return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            return 1;
        }
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
        String idNumber = idNumberInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter guest name", Toast.LENGTH_SHORT).show();
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
        if (idNumber.isEmpty()) {
            Toast.makeText(this, "Please enter ID number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkAuthenticationStatus() {
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            proceedToPayment();
        } else {
            showAuthenticationDialog();
        }
    }

    private void showAuthenticationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Authentication Required");
        builder.setMessage("Please login or register to continue with your booking.");
        builder.setPositiveButton("Login", (dialog, which) -> {
            Intent intent = new Intent(HotelGuestDetailsActivity.this, LoginActivity.class);
            startActivityForResult(intent, 100);
        });
        builder.setNegativeButton("Register", (dialog, which) -> {
            Intent intent = new Intent(HotelGuestDetailsActivity.this, RegisterActivity.class);
            startActivityForResult(intent, 101);
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void proceedToPayment() {
        // Safety check is now less critical but still good practice
        if (hotel == null || searchParams == null) {
            Toast.makeText(this, "Booking data lost during transition. Please restart booking.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, HotelPaymentActivity.class);
        intent.putExtra("hotel", hotel);
        intent.putExtra("search_params", searchParams);
        intent.putExtra("guest_name", nameInput.getText().toString().trim());
        intent.putExtra("guest_email", emailInput.getText().toString().trim());
        intent.putExtra("guest_phone", phoneInput.getText().toString().trim());
        intent.putExtra("guest_id", idNumberInput.getText().toString().trim());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // When Login/Register returns successfully, we proceed to payment.
            proceedToPayment();
        }
    }
}