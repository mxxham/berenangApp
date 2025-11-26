package com.example.berenang10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView; // Added for QR Code
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.WriterException; // Added for QR Code

import java.text.NumberFormat;
import java.util.Locale;

// NOTE: This file assumes the existence of:
// - BookingRecord, Flight, Hotel, PassengerData, HotelSearchParams, FlightSearchParams classes
// - BookingManager class (with getInstance(Context) and getBookingById/cancelBooking methods)
// - QRCodeGenerator.java (needed for QR code generation)

public class BookingDetailActivity extends AppCompatActivity {

    private BookingRecord booking;

    // Core info
    private TextView bookingTypeText, pnrText, bookingIdText, statusText;

    // Details common to both, or used for specific sections
    private TextView infoTitleText, routeTitleText, passengerTitleText;
    private TextView flightInfoText, routeText, dateTimeText;
    private TextView passengerNameText, passengerEmailText, passengerPhoneText;
    private TextView paymentMethodText, totalAmountText;

    // NEW: ImageView for QR Code
    private ImageView qrCodeImage;

    // Buttons
    private Button cancelButton, printTicketButton, checkInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // --- UPDATE 1: Retrieve the full Parcelable BookingRecord object ---
        booking = getIntent().getParcelableExtra("booking_record");

        // Fallback for old logic or if passing ID:
        if (booking == null) {
            String id = getIntent().getStringExtra("booking_id");
            if (id != null) {
                // NOTE: BookingManager.getInstance() now requires Context
                booking = BookingManager.getInstance(this).getBookingById(id);
            }
        }

        if (booking == null) {
            Toast.makeText(this, "Booking details not found or failed to load.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        displayBookingDetails();
        setupButtons();
    }

    private void initializeViews() {
        // Core Info
        bookingTypeText = findViewById(R.id.booking_type_text);
        pnrText = findViewById(R.id.pnr_text);
        bookingIdText = findViewById(R.id.booking_id_text);
        statusText = findViewById(R.id.status_text);

        // NEW: Initialize QR Code ImageView
        qrCodeImage = findViewById(R.id.qr_code_image);

        // Section Titles (for conditional text)
        infoTitleText = findViewById(R.id.flight_info_title);
        routeTitleText = findViewById(R.id.route_title);
        passengerTitleText = findViewById(R.id.passenger_details_title);

        // Detail Fields
        flightInfoText = findViewById(R.id.flight_info_text);
        routeText = findViewById(R.id.route_text);
        dateTimeText = findViewById(R.id.date_time_text);
        passengerNameText = findViewById(R.id.passenger_name_text);
        passengerEmailText = findViewById(R.id.passenger_email_text);
        passengerPhoneText = findViewById(R.id.passenger_phone_text);

        // Payment/Amount
        paymentMethodText = findViewById(R.id.payment_method_text);
        totalAmountText = findViewById(R.id.total_amount_text);

        // Buttons
        cancelButton = findViewById(R.id.cancel_button);
        printTicketButton = findViewById(R.id.print_ticket_button); // REFERENCES CORRECT ID NOW
        checkInButton = findViewById(R.id.checkin_button);
    }

    private void displayBookingDetails() {

        bookingTypeText.setText(booking.getBookingType() + " Booking");
        bookingIdText.setText("Booking ID: " + booking.getBookingId());
        pnrText.setText(booking.getBookingType().equals("Flight") ?
                "PNR: " + booking.getPnr() :
                "ID: " + booking.getBookingId());
        statusText.setText(booking.getStatus());

        // Set status color
        int statusColor;
        if (booking.getStatus().equals("Confirmed")) {
            statusColor = getColor(android.R.color.holo_green_dark);
        } else if (booking.getStatus().equals("Cancelled")) {
            statusColor = getColor(android.R.color.holo_red_dark);
        } else {
            statusColor = getColor(android.R.color.holo_orange_dark);
        }
        statusText.setTextColor(statusColor);

        // Hide QR code whenever details are refreshed
        qrCodeImage.setVisibility(View.GONE);

        // --- UPDATE 2: Conditional Display for FLIGHT vs HOTEL ---
        if ("Flight".equals(booking.getBookingType())) {
            Flight flight = booking.getFlight();
            PassengerData passenger = booking.getPassengerData();

            // Titles
            infoTitleText.setText("Flight Details");
            routeTitleText.setText("Route");

            // Flight Details
            flightInfoText.setText(flight.getAirline() + " " + flight.getFlightNumber());
            routeText.setText(flight.getOrigin() + " → " + flight.getDestination());
            dateTimeText.setText(booking.getSearchParams().getDepartureDate() + " • " +
                    flight.getDepartureTime() + " - " + flight.getArrivalTime());

            // Passenger Details
            passengerTitleText.setText("Passenger Details");
            passengerNameText.setText(passenger.getFullName());
            passengerEmailText.setText(passenger.getEmail());
            passengerPhoneText.setText(passenger.getPhone());

            // Show Check-in button for flights
            checkInButton.setVisibility(View.VISIBLE);

        } else if ("Hotel".equals(booking.getBookingType())) {
            Hotel hotel = booking.getHotel();
            HotelSearchParams search = booking.getHotelSearchParams();

            // Titles
            infoTitleText.setText("Hotel & Room Details");
            routeTitleText.setText("Check-in / Check-out");

            // Hotel Details
            flightInfoText.setText(hotel.getName() + " (" + hotel.getRating() + " Star)");

            // --- FIX APPLIED HERE ---
            routeText.setText(hotel.getArea() + " | " + hotel.getRoomType());

            dateTimeText.setText(search.getCheckInDate() + " - " + search.getCheckOutDate() +
                    " (" + search.getNights() + " Nights)");

            // Guest Details
            passengerTitleText.setText("Guest Details");
            passengerNameText.setText(booking.getGuestName());
            passengerEmailText.setText(booking.getGuestEmail());
            passengerPhoneText.setText(booking.getGuestPhone());

            // Hide Check-in button for hotels (or change text/functionality)
            checkInButton.setVisibility(View.GONE);
        }

        // Common Details
        paymentMethodText.setText(booking.getPaymentMethod());
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        totalAmountText.setText(currencyFormat.format(booking.getTotalAmount()));
    }

    private void setupButtons() {

        // --- NOTE: BookingManager.getInstance() now requires Context ---

        // NEW: Call the QR code generation method when "Print E-Ticket/Voucher" is tapped
        printTicketButton.setOnClickListener(v -> {
            generateAndShowQrCode();
        });

        checkInButton.setOnClickListener(v -> {
            if (booking.getBookingType().equals("Flight")) {
                Toast.makeText(this, "Online check-in will be available 24 hours before departure",
                        Toast.LENGTH_LONG).show();
            } else {
                // Should be hidden, but here for completeness
                Toast.makeText(this, "Check-in at hotel front desk.", Toast.LENGTH_LONG).show();
            }
        });

        cancelButton.setOnClickListener(v -> showCancelDialog());

        // Disable buttons if cancelled
        if (booking.getStatus().equals("Cancelled")) {
            cancelButton.setEnabled(false);
            printTicketButton.setEnabled(false);
            checkInButton.setEnabled(false);
        }
    }

    /**
     * Generates a QR code and displays it using the hypothetical QRCodeGenerator.
     */
    private void generateAndShowQrCode() {
        try {
            // Content for the QR code uses PNR or Booking ID
            String qrContent = "BERENANG_BOOKING:" +
                    (booking.getPnr() != null ? booking.getPnr() : booking.getBookingId());

            // Generate the Bitmap (assuming QRCodeGenerator.java is available)
            Bitmap qrBitmap = QRCodeGenerator.generateQRCodeBitmap(qrContent, 400);

            // Display the QR code in the ImageView
            qrCodeImage.setImageBitmap(qrBitmap);
            qrCodeImage.setVisibility(View.VISIBLE);

            // Show a success message
            String contactEmail = booking.getBookingType().equals("Flight") ?
                    booking.getPassengerData().getEmail() :
                    booking.getGuestEmail();
            Toast.makeText(this, "QR Code generated! E-ticket/Voucher is now visible and has been sent to " + contactEmail,
                    Toast.LENGTH_LONG).show();

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR Code. Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "An unexpected error occurred during QR code generation.",
                    Toast.LENGTH_LONG).show();
        }
    }


    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Booking");

        String cancellationMessage = booking.getBookingType().equals("Flight") ?
                "Are you sure you want to cancel this flight? Cancellation fees may apply according to airline policy." :
                "Are you sure you want to cancel this hotel booking? Check the hotel's cancellation policy.";

        builder.setMessage(cancellationMessage);

        builder.setPositiveButton("Yes, Cancel", (dialog, which) -> {
            String idToCancel = booking.getPnr() != null ? booking.getPnr() : booking.getBookingId();

            BookingManager.getInstance(this).cancelBooking(idToCancel);
            Toast.makeText(this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();

            // Re-fetch the record to update the screen status immediately
            booking = BookingManager.getInstance(this).getBookingById(idToCancel);
            if (booking != null) {
                displayBookingDetails();
                setupButtons();
            } else {
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}