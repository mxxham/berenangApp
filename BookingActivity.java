package com.example.berenang10;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri; // <-- 1. ADD THIS IMPORT
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    // Constants for data passed from EventActivity
    public static final String EXTRA_EVENT_TITLE = "extra_event_title";
    public static final String EXTRA_EVENT_SUBTITLE = "extra_event_subtitle";
    public static final String EXTRA_EVENT_PRICE = "extra_event_price";
    public static final String EXTRA_EVENT_DATE_HINT = "extra_event_date_hint";
    public static final String EXTRA_SCHEDULES = "extra_schedules";

    private TextView tvEventTitle;
    private TextView tvPricePerItem;
    private TextInputEditText etBookingDate;
    private TextInputEditText etQuantity;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private MaterialButton btnConfirmBooking;
    private ImageButton btnBack;

    private String eventTitle;
    private String eventSubtitle;
    private double eventPrice;

    private List<EventSchedule> schedules;
    private EventSchedule selectedSchedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // --- Retrieve Event Details from Intent ---
        eventTitle = getIntent().getStringExtra(EXTRA_EVENT_TITLE);
        eventSubtitle = getIntent().getStringExtra(EXTRA_EVENT_SUBTITLE);
        eventPrice = getIntent().getDoubleExtra(EXTRA_EVENT_PRICE, 0.0);
        schedules = (List<EventSchedule>) getIntent().getSerializableExtra(EXTRA_SCHEDULES);
        if (schedules == null) {
            schedules = new ArrayList<>();
        }
        String eventDateHint = getIntent().getStringExtra(EXTRA_EVENT_DATE_HINT);

        // --- Initialize Views ---
        tvEventTitle = findViewById(R.id.tv_booking_event_title);
        tvPricePerItem = findViewById(R.id.tv_price_per_item);
        etBookingDate = findViewById(R.id.et_booking_date);
        etQuantity = findViewById(R.id.et_quantity);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        btnConfirmBooking = findViewById(R.id.btn_confirm_booking);
        btnBack = findViewById(R.id.btn_back_booking);

        // --- Populate Event Title and Price ---
        tvEventTitle.setText(eventTitle != null ? "Book: " + eventTitle : "Book Event");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        tvPricePerItem.setText(String.format("Price per item: %s", currencyFormat.format(eventPrice)));

        // --- Schedule Selection Setup ---
        if (eventDateHint != null && !eventDateHint.isEmpty()) {
            etBookingDate.setText(eventDateHint);
        } else {
            etBookingDate.setText("Tap to Select Date & Location");
        }
        selectedSchedule = null;
        etBookingDate.setOnClickListener(v -> showScheduleSelectionDialog());
        etBookingDate.setFocusable(false);
        etBookingDate.setClickable(true);

        // --- Button Listeners ---
        btnConfirmBooking.setOnClickListener(v -> attemptBooking());
        btnBack.setOnClickListener(v -> finish());
    }

    private void showScheduleSelectionDialog() {
        if (schedules.isEmpty()) {
            Toast.makeText(this, "No dates or locations available for booking.", Toast.LENGTH_LONG).show();
            return;
        }
        String[] scheduleDisplay = new String[schedules.size()];
        for (int i = 0; i < schedules.size(); i++) {
            EventSchedule schedule = schedules.get(i);
            scheduleDisplay[i] = String.format("%s at %s", schedule.getDate(), schedule.getLocation());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Event Date & Location");
        builder.setItems(scheduleDisplay, (dialog, which) -> {
            selectedSchedule = schedules.get(which);
            updateScheduleInView();
            dialog.dismiss();
        });
        builder.show();
    }

    private void updateScheduleInView() {
        if (selectedSchedule != null) {
            etBookingDate.setText(String.format("%s - %s", selectedSchedule.getDate(), selectedSchedule.getLocation()));
        }
    }

    private void attemptBooking() {
        // (This entire validation method remains unchanged)
        etBookingDate.setError(null);
        etQuantity.setError(null);
        etFullName.setError(null);
        etEmail.setError(null);
        if (etPhone != null) etPhone.setError(null);

        String quantityStr = etQuantity.getText().toString();
        String fullName = etFullName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone != null ? etPhone.getText().toString() : "N/A";

        int quantity = 0;
        boolean cancel = false;
        View focusView = null;

        if (selectedSchedule == null) {
            etBookingDate.setError("Please select a date and location");
            focusView = etBookingDate;
            cancel = true;
        }
        try {
            if (TextUtils.isEmpty(quantityStr)) {
                etQuantity.setError("Please enter quantity");
                focusView = etQuantity;
                cancel = true;
            } else {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    etQuantity.setError("Quantity must be at least 1");
                    focusView = etQuantity;
                    cancel = true;
                }
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid quantity");
            focusView = etQuantity;
            cancel = true;
        }
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            focusView = etFullName;
            cancel = true;
        }
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            etEmail.setError("Enter a valid email address");
            focusView = etEmail;
            cancel = true;
        }
        if (eventPrice <= 0.0) {
            Toast.makeText(this, "Warning: Booking price is zero.", Toast.LENGTH_SHORT).show();
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            proceedToPayment(fullName, email, phone, quantity);
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // v-- 2. ADD THIS ENTIRE NEW METHOD --v
    /**
     * Creates and launches an email intent with the booking details.
     */
    private void composeAndSendEmail(String fullName, String recipientEmail, String phone, int quantity) {
        double totalPrice = eventPrice * quantity;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedTotalPrice = currencyFormat.format(totalPrice);

        String subject = "Booking Details for: " + eventTitle;
        String body = "Hello " + fullName + ",\n\n" +
                "Here are the details for your booking:\n\n" +
                "Event: " + eventTitle + "\n" +
                "Date: " + selectedSchedule.getDate() + "\n" +
                "Location: " + selectedSchedule.getLocation() + "\n" +
                "Quantity: " + quantity + "\n" +
                "Price per Item: " + currencyFormat.format(eventPrice) + "\n" +
                "--------------------\n" +
                "Total Price: " + formattedTotalPrice + "\n\n" +
                "Your Contact Details:\n" +
                "Email: " + recipientEmail + "\n" +
                "Phone: " + phone + "\n\n" +
                "Thank you!";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            // We use a chooser to let the user pick their email app
            startActivity(Intent.createChooser(emailIntent, "Send booking email via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            // This toast will show if the user has no email apps installed
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Packages the event data and user data and launches PaymentActivity.
     */
    private void proceedToPayment(String fullName, String email, String phone, int quantity) {

        // <-- 3. ADD THIS CALL to trigger the email
        // Automatically compose and trigger the email intent upon successful booking
        composeAndSendEmail(fullName, email, phone, quantity);

        // The rest of the method remains the same
        String idNumberPlaceholder = "EVENT_GUEST";
        Passenger passengerData = new Passenger(fullName, phone, email, idNumberPlaceholder);
        Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);

        intent.putExtra("booking_type", "event");
        intent.putExtra("event_title", eventTitle);
        intent.putExtra("event_subtitle", eventSubtitle);
        intent.putExtra("event_booking_date", selectedSchedule.getDate());
        intent.putExtra("event_location", selectedSchedule.getLocation());
        intent.putExtra("event_price_per_item", eventPrice);
        intent.putExtra("event_item_count", quantity);
        intent.putExtra("passenger_data", passengerData);
        intent.putExtra("passenger_count", quantity);

        startActivity(intent);
        finish();
    }
}