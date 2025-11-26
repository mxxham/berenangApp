package com.example.berenang10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.Random;
import android.widget.RadioButton;
import android.widget.ProgressBar;

public class HotelPaymentActivity extends AppCompatActivity {

    private Hotel hotel;
    private HotelSearchParams searchParams;
    private String guestName, guestEmail, guestPhone, guestId;

    private TextView hotelNameSummaryText, hotelDurationSummaryText, totalAmountText;
    private RadioGroup paymentMethodGroup;
    private Button payButton;
    private ImageView backButton;
    private ProgressBar progressBar;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_payment);

        // Retrieve data from the previous activity
        Intent intent = getIntent();
        hotel = intent.getParcelableExtra("hotel");
        searchParams = intent.getParcelableExtra("search_params");
        guestName = intent.getStringExtra("guest_name");
        guestEmail = intent.getStringExtra("guest_email");
        guestPhone = intent.getStringExtra("guest_phone");
        guestId = intent.getStringExtra("guest_id"); // guestId is the ID Number

        initializeViews();
        displaySummaryAndPrice();
        setupPayButton();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        hotelNameSummaryText = findViewById(R.id.hotel_name_summary);
        hotelDurationSummaryText = findViewById(R.id.hotel_duration_summary);
        totalAmountText = findViewById(R.id.total_amount_text);
        paymentMethodGroup = findViewById(R.id.payment_method_group);
        payButton = findViewById(R.id.pay_now_button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        backButton.setOnClickListener(v -> finish());
    }

    private void displaySummaryAndPrice() {
        if (hotel == null || searchParams == null) {
            Toast.makeText(this, "Booking data is missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 1. Calculate Nights and Total Price
        int nights = calculateNights(searchParams.getCheckInDate(), searchParams.getCheckOutDate());
        totalAmount = hotel.getPricePerNight() * nights * searchParams.getRooms();

        // Use Indonesian Rupiah format
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice = currencyFormat.format(totalAmount);

        // 2. Build and Set Summary Text
        String nameSummary = String.format("%s - %s", hotel.getName(), hotel.getRoomType());
        hotelNameSummaryText.setText(nameSummary);

        String durationSummary = String.format(
                "%d Room(s), %d Guest(s) | %s - %s (%d Night(s))",
                searchParams.getRooms(),
                searchParams.getGuests(),
                searchParams.getCheckInDate(),
                searchParams.getCheckOutDate(),
                nights
        );
        hotelDurationSummaryText.setText(durationSummary);

        // 3. Set Total Amount and update the Pay Button text
        totalAmountText.setText(formattedPrice);
        payButton.setText(String.format("Pay %s Now", formattedPrice));
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

    private void setupPayButton() {
        payButton.setOnClickListener(v -> {
            int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Initiating payment via " + paymentMethod + "...", Toast.LENGTH_LONG).show();

        long paymentDelay = 3000;

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            payButton.setEnabled(true);

            Random random = new Random();
            boolean isSuccess = random.nextFloat() > 0.3;

            if (isSuccess) {
                // The save logic now calls the streamlined method
                String bookingId = saveHotelBooking(paymentMethod);
                showSuccessDialog(bookingId, paymentMethod);
            } else {
                showFailureDialog();
            }

        }, paymentDelay);
    }

    // REMOVED saveBookingToDatabase: The logic is moved into saveHotelBooking using BookingManager.addHotelBooking()

    /**
     * Saves the hotel booking record using the unified BookingManager,
     * which handles persistence to the SQLite Database.
     * @param paymentMethod The selected payment method.
     * @return The unique booking ID generated by BookingManager.
     */
    private String saveHotelBooking(String paymentMethod) {
        SharedPreferences prefs = getSharedPreferences("BerenangPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("current_user_email", "");

        // FIX APPLIED HERE: Calling getInstance(this)
        // BookingManager.addHotelBooking() handles ID generation and database persistence
        String bookingId = BookingManager.getInstance(this).addHotelBooking(
                userEmail,
                hotel,
                searchParams,
                guestName,
                guestEmail,
                guestPhone,
                guestId,
                paymentMethod,
                totalAmount,
                System.currentTimeMillis(),
                "Confirmed"
        );

        return bookingId;
    }

    // REMOVED generatePNR() as BookingManager.addHotelBooking() handles the ID generation

    private void showSuccessDialog(String bookingId, String paymentMethod) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✓ Hotel Booking Confirmed!");

        String message = "Your room booking has been confirmed!\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Payment Method: " + paymentMethod + "\n\n" +
                "Confirmation details will be sent to:\n" + guestEmail + "\n\n" +
                "You can view your booking status in 'My Bookings'.";

        builder.setMessage(message);
        builder.setPositiveButton("View Booking", (dialog, which) -> {
            Intent intent = new Intent(HotelPaymentActivity.this, MainActivity.class);
            intent.putExtra("navigate_to", "bookings");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Done", (dialog, which) -> {
            Intent intent = new Intent(HotelPaymentActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void showFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("❌ Payment Failed");
        builder.setMessage("Your transaction was declined. Please try again with a different payment method.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Dismisses the dialog
        });
        builder.setCancelable(true);
        builder.show();
    }
}