package com.example.berenang10; // Change to your actual package name

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;

public class EventDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    // New Feature Extras
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_LOCATION = "extra_location";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    private String eventTitle = "Activity Detail";
    private String eventLocation = "Unknown Location";
    private double eventLat = 0.0;
    private double eventLon = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make Status Bar transparent and allow content to draw behind it (Full-bleed image effect)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_event_detail);

        // --- Intent Data Retrieval ---
        Intent intent = getIntent();
        eventTitle = intent.getStringExtra(EXTRA_TITLE);
        String subtitle = intent.getStringExtra(EXTRA_SUBTITLE);
        String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);
        String date = intent.getStringExtra(EXTRA_DATE);
        eventLocation = intent.getStringExtra(EXTRA_LOCATION);
        eventLat = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0);
        eventLon = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0);


        // --- UI Initialization and Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar_dummy);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView imageView = findViewById(R.id.event_detail_image);
        ImageButton backButton = findViewById(R.id.btn_back);
        ImageButton shareButton = findViewById(R.id.btn_share);
        MaterialButton bookButton = findViewById(R.id.btn_book_now);
        TextView dateText = findViewById(R.id.tv_detail_date);
        TextView locationText = findViewById(R.id.tv_detail_location);
        TextView descriptionText = findViewById(R.id.event_detail_description);
        TextView subtitleText = findViewById(R.id.event_detail_subtitle);
        TextView mapButton = findViewById(R.id.btn_view_map); // New map button view


        // --- Data Population ---
        collapsingToolbar.setTitle(eventTitle != null ? eventTitle : "Activity Detail");
        subtitleText.setText(subtitle != null ? subtitle : "No sub-details provided.");
        dateText.setText(date != null ? date : "Date not specified");
        locationText.setText(eventLocation != null ? eventLocation : "Location data unavailable");
        descriptionText.setText(description != null && !description.isEmpty() ? description : "Experience the best! Book now for an unforgettable event.");

        // Load image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).centerCrop().into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_placeholder); // Ensure ic_placeholder exists
        }

        // --- Feature Listeners ---

        // 1. Back Button
        backButton.setOnClickListener(v -> finish());

        // 2. Book Now Button
        bookButton.setOnClickListener(v -> {
            Toast.makeText(this, "Booking for " + eventTitle + " initiated!", Toast.LENGTH_SHORT).show();
            // Implement actual navigation to booking screen here
        });

        // 3. Share Button
        shareButton.setOnClickListener(v -> shareEventDetails());

        // 4. View Map Button
        mapButton.setOnClickListener(v -> viewLocationOnMap());
    }

    /**
     * Creates and executes an Intent to share the event's title and location.
     */
    private void shareEventDetails() {
        String shareText = "Check out this amazing event: " + eventTitle +
                " at " + eventLocation + ". Book now!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Event Recommendation: " + eventTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(shareIntent, "Share Event via"));
    }

    /**
     * Launches a map application to show the event location using Latitude/Longitude.
     */
    private void viewLocationOnMap() {
        if (eventLat != 0.0 && eventLon != 0.0) {
            // Standard Geo URI format for map applications
            Uri gmmIntentUri = Uri.parse("geo:" + eventLat + "," + eventLon + "?q=" + Uri.encode(eventLocation));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Tries to open in Google Maps first

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to general map view if Google Maps is not installed
                mapIntent.setPackage(null);
                startActivity(mapIntent);
            }
        } else {
            Toast.makeText(this, "Location coordinates unavailable for mapping.", Toast.LENGTH_SHORT).show();
        }
    }
}