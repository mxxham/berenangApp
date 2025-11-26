package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.NumberFormat;
import java.util.Locale;
import com.bumptech.glide.Glide; // <-- 1. IMPORT GLIDE

public class HotelDetailActivity extends AppCompatActivity {

    private Hotel hotel;
    private HotelSearchParams searchParams;

    private Toolbar toolbar;
    private ImageView hotelImage;
    private TextView nameText, locationText, ratingText, reviewCountText;
    private TextView roomTypeText, priceText, amenitiesText, addressText;
    private TextView checkInText, checkOutText, guestsText, roomsText;
    private Button bookNowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        // Retrieve data
        hotel = getIntent().getParcelableExtra("hotel");
        searchParams = getIntent().getParcelableExtra("search_params");

        // Note: The hotel object MUST now contain the 'imageUrl' property
        // set by the HotelResultsAdapter.

        initializeViews();
        setupToolbar();
        displayHotelDetails();
        setupBookButton();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        hotelImage = findViewById(R.id.hotel_image);
        nameText = findViewById(R.id.hotel_name);
        locationText = findViewById(R.id.location_text);
        ratingText = findViewById(R.id.rating_text);
        reviewCountText = findViewById(R.id.review_count_text);
        roomTypeText = findViewById(R.id.room_type_text);
        priceText = findViewById(R.id.price_text);
        amenitiesText = findViewById(R.id.amenities_text);
        addressText = findViewById(R.id.address_text);
        checkInText = findViewById(R.id.checkin_text);
        checkOutText = findViewById(R.id.checkout_text);
        guestsText = findViewById(R.id.guests_text);
        roomsText = findViewById(R.id.rooms_text);
        bookNowButton = findViewById(R.id.book_now_button);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayHotelDetails() {
        // Text details binding
        nameText.setText(hotel.getName());
        // FIX: Change getLocation() to getArea()
        locationText.setText(hotel.getArea());
        addressText.setText(hotel.getAddress());
        ratingText.setText(String.format(Locale.getDefault(), "%.1f", hotel.getRating()));
        reviewCountText.setText("(" + hotel.getReviewCount() + " reviews)");
        amenitiesText.setText(hotel.getAmenities());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        priceText.setText(currencyFormat.format(hotel.getPricePerNight()) + " /night");

        // --- 2. CRITICAL UPDATE: Load image using Glide ---
        String hotelImageUrl = hotel.getImageUrl();
        if (hotelImageUrl != null && !hotelImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(hotelImageUrl)
                    .placeholder(android.R.color.darker_gray) // Placeholder while loading
                    .into(hotelImage);
        } else {
            // Fallback if URL is missing or null
            // Ensure getColor() is available, otherwise use setBackgroundColor() with a literal color
            hotelImage.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark, getTheme()));
        }
        // --------------------------------------------------

        // Display booking details
        checkInText.setText("Check-in: " + searchParams.getCheckInDate());
        checkOutText.setText("Check-out: " + searchParams.getCheckOutDate());
        guestsText.setText("Guests: " + searchParams.getGuests());
        roomsText.setText("Rooms: " + searchParams.getRooms());
        roomTypeText.setText("Room Type: " + hotel.getRoomType());
    }

    private void setupBookButton() {
        bookNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HotelGuestDetailsActivity.class);
            intent.putExtra("hotel", hotel);
            intent.putExtra("search_params", searchParams);
            startActivity(intent);
        });
    }
}