package com.example.berenang10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.WriterException;
import com.google.android.material.bottomsheet.BottomSheetBehavior; // Import for BottomSheetBehavior
import android.widget.FrameLayout; // Import for FrameLayout

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// NOTE: This file assumes the existence of DatabaseHelper, BookingData, and QRCodeGenerator classes.

public class BookingDatabaseDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private BookingData booking;
    private ImageView backButton;
    private TextView pnrText, bookingIdText, typeText, statusText;
    private TextView titleText, detailsText, dateText;
    private TextView paymentMethodText, totalAmountText;
    private Button cancelButton, printTicketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_database_detail);

        String pnr = getIntent().getStringExtra("pnr");
        databaseHelper = new DatabaseHelper(this);
        booking = databaseHelper.getBookingByPNR(pnr);

        if (booking == null) {
            Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        displayBookingDetails();
        setupButtons();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        pnrText = findViewById(R.id.pnr_text);
        bookingIdText = findViewById(R.id.booking_id_text);
        typeText = findViewById(R.id.type_text);
        statusText = findViewById(R.id.status_text);
        titleText = findViewById(R.id.title_text);
        detailsText = findViewById(R.id.details_text);
        dateText = findViewById(R.id.date_text);
        paymentMethodText = findViewById(R.id.payment_method_text);
        totalAmountText = findViewById(R.id.total_amount_text);
        cancelButton = findViewById(R.id.cancel_button);
        printTicketButton = findViewById(R.id.download_ticket_button);

        backButton.setOnClickListener(v -> finish());
    }

    private void displayBookingDetails() {
        pnrText.setText("PNR: " + booking.getPnr());
        bookingIdText.setText("Booking ID: " + booking.getBookingId());
        typeText.setText(booking.getType().toUpperCase() + " BOOKING");
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

        titleText.setText(booking.getTitle());
        detailsText.setText(booking.getDetails());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        Date bookingDate = new Date(booking.getDate());
        dateText.setText("Booked on: " + dateFormat.format(bookingDate));

        paymentMethodText.setText(booking.getPaymentMethod());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        totalAmountText.setText(currencyFormat.format(booking.getAmount()));
    }

    private void setupButtons() {
        // Update button text to reflect its new action
        printTicketButton.setText("Show E-Ticket/Voucher");

        printTicketButton.setOnClickListener(v -> {
            showTicketBottomSheet();
        });

        cancelButton.setOnClickListener(v -> showCancelDialog());

        // Disable buttons if cancelled
        if (booking.getStatus().equals("Cancelled")) {
            cancelButton.setEnabled(false);
            cancelButton.setAlpha(0.5f);
            printTicketButton.setEnabled(false);
        }
    }

    /**
     * Creates and displays the Bottom Sheet Dialog with the QR code and details,
     * sliding up from the bottom.
     */
    private void showTicketBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // Inflate the dedicated bottom sheet layout
        View bottomSheetView = LayoutInflater.from(this).inflate(
                R.layout.bottom_sheet_ticket, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // --- FIX FOR CONSISTENT SHAPE ACROSS THEMES (Dark/Light) ---

        // 1. Get the behavior and set to fit content
        BottomSheetBehavior<FrameLayout> behavior = bottomSheetDialog.getBehavior();
        behavior.setFitToContents(true);

        // **NEW FIX:** Set expanded offset to 0 to prevent the default Material shape
        // theme (which causes extra rounding in some modes) from applying.
        behavior.setExpandedOffset(0);

        // 2. Find the FrameLayout container that holds the Bottom Sheet content
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        // 3. Clear the default background of the container, allowing our custom drawable (which you set in XML) to control the shape.
        if (bottomSheet != null) {
            bottomSheet.setBackground(null);
        }
        // --- END FIX ---

        // 1. Initialize Views from the Bottom Sheet Layout
        ImageView qrCodeImage = bottomSheetView.findViewById(R.id.bottom_sheet_qr_code_image);
        TextView passengerInfoText = bottomSheetView.findViewById(R.id.bottom_sheet_passenger_info_text);
        Button closeButton = bottomSheetView.findViewById(R.id.bottom_sheet_close_button);

        // 2. Generate and Display QR Code
        try {
            String qrContent = "BERENANG_BOOKING_DB:" + booking.getPnr();
            // Assuming QRCodeGenerator.java class exists and works
            Bitmap qrBitmap = QRCodeGenerator.generateQRCodeBitmap(qrContent, 400);
            qrCodeImage.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            // Fallback if QR code generation fails
            qrCodeImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            Toast.makeText(this, "Failed to generate QR Code.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // 3. Populate Booking Details
        String passengerDetails = String.format(
                "Booking Type: %s\n" +
                        "PNR / ID: %s\n" +
                        "Title: %s\n" +
                        "Status: %s\n" +
                        "Date Booked: %s\n" +
                        "Total Paid: %s",
                booking.getType().toUpperCase(Locale.ROOT),
                booking.getPnr(),
                booking.getTitle(),
                booking.getStatus(),
                dateText.getText().toString().replace("Booked on: ", ""),
                totalAmountText.getText().toString()
        );
        passengerInfoText.setText(passengerDetails);

        // 4. Setup Close Button
        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // 5. Show the Dialog
        bottomSheetDialog.show();
    }


    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Booking");
        builder.setMessage("Are you sure you want to cancel this booking?\n\nCancellation fees may apply.");
        builder.setPositiveButton("Yes, Cancel", (dialog, which) -> {
            int result = databaseHelper.updateBookingStatus(booking.getPnr(), "Cancelled");
            if (result > 0) {
                Toast.makeText(this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();

                // Re-fetch to update status immediately
                booking = databaseHelper.getBookingByPNR(booking.getPnr());
                if (booking != null) {
                    displayBookingDetails();
                    setupButtons();
                } else {
                    finish();
                }

            } else {
                Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}