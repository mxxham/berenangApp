package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_PRICE = "extra_price";
    public static final String EXTRA_SCHEDULES = "extra_schedules";

    private String title;
    private String subtitle;
    private String imageUrl;
    private String description;
    private double price;
    private List<EventSchedule> schedules;
    private LinearLayout scheduleListContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Make Status Bar transparent and allow content to draw behind it (Full-bleed image effect)
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_event);

        // Initialize the dummy Toolbar for CollapsingToolbarLayout and set as action bar
        Toolbar toolbar = findViewById(R.id.toolbar_dummy);
        setSupportActionBar(toolbar);

        // Setup Collapsing Toolbar
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        // Get the data passed from the Intent and store it in instance variables
        title = getIntent().getStringExtra(EXTRA_TITLE);
        subtitle = getIntent().getStringExtra(EXTRA_SUBTITLE);
        imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        description = getIntent().getStringExtra(EXTRA_DESCRIPTION);

        // Retrieve the price
        price = getIntent().getDoubleExtra(EXTRA_PRICE, 0.0);

        // Retrieve schedule data
        schedules = (List<EventSchedule>) getIntent().getSerializableExtra(EXTRA_SCHEDULES);


        // --- TEMPORARY FIX & HARDCODED DATA FOR TESTING ---
        // 🛑 CRITICAL CHANGE: Schedules are now created based on the title if none are passed.
        if (schedules == null || schedules.isEmpty()) {
            schedules = createDummySchedules();
        }

        if (price <= 0.0) {
            price = 50000.0;
            Toast.makeText(this, "WARNING: Price defaulted to Rp 50,000 for testing.", Toast.LENGTH_LONG).show();
        }
        // --- END TEMPORARY FIX ---


        // Initialize Views
        TextView subtitleText = findViewById(R.id.event_detail_subtitle);
        TextView descriptionText = findViewById(R.id.event_detail_description);
        ImageView imageView = findViewById(R.id.event_detail_image);
        ImageButton backButton = findViewById(R.id.btn_back);
        MaterialButton bookButton = findViewById(R.id.btn_book_now);

        scheduleListContainer = findViewById(R.id.schedule_list_container);


        // Populate Views
        if (title != null) {
            collapsingToolbar.setTitle(title);
        } else {
            collapsingToolbar.setTitle("Activity Detail");
        }

        if (subtitle != null) {
            subtitleText.setText(subtitle);
        } else {
            subtitleText.setVisibility(View.GONE);
        }

        if (description != null && !description.isEmpty()) {
            descriptionText.setText(description);
        } else {
            descriptionText.setText("This is a detailed description of the event or activity. Book your spot now to enjoy this experience!");
        }

        // Load image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_placeholder);
        }

        // Dynamic schedule population
        populateScheduleList();


        // 2. Set click listener for the custom back button
        backButton.setOnClickListener(v -> finish());

        // 🚀 Set click listener to launch BookingActivity
        bookButton.setOnClickListener(v -> {
            launchBookingActivity();
        });

        // 3. Disable the standard Action Bar controls
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * METHOD TO CREATE DUMMY DATA - NOW BASED on the title from SearchActivity.
     */
    private List<EventSchedule> createDummySchedules() {
        List<EventSchedule> dummy = new ArrayList<>();

        if (title == null) return dummy;

        switch (title) {
            case "Bird Paradise":
                dummy.add(new EventSchedule("Every Day, 9:00 AM - 6:00 PM", "Singapore Zoo Complex, Mandai Lake Rd"));
                break;
            case "Dunia Fantasi (Dufan) Ancol":
                dummy.add(new EventSchedule("Mon - Sun, 10:00 AM - 6:00 PM", "Ancol Dreamland, North Jakarta"));
                break;
            case "Taman Safari Bogor":
                dummy.add(new EventSchedule("Daily, 8:30 AM - 5:00 PM", "Cisarua, West Java"));
                break;
            case "Prambanan Temple":
                dummy.add(new EventSchedule("Daily, 6:00 AM - 5:00 PM", "Sleman Regency, Yogyakarta"));
                break;
            case "Jakarta Music Fest":
                // Multiple days for an event
                dummy.add(new EventSchedule("Friday, Nov 15, 2025 - 7:00 PM", "JIExpo Kemayoran, Central Jakarta"));
                dummy.add(new EventSchedule("Saturday, Nov 16, 2025 - 7:00 PM", "JIExpo Kemayoran, Central Jakarta"));
                dummy.add(new EventSchedule("Sunday, Nov 17, 2025 - 6:00 PM", "JIExpo Kemayoran, Central Jakarta"));
                break;
            case "Art Exhibition 2024":
                dummy.add(new EventSchedule("Dec 1 - Dec 31, 2025", "Museum MACAN, West Jakarta"));
                break;
            case "Hiking Bromo":
                dummy.add(new EventSchedule("All Year Round", "Bromo Tengger Semeru National Park, East Java"));
                break;
            case "Kiddie Rides":
                dummy.add(new EventSchedule("Mall Operating Hours", "Mall Taman Anggrek, West Jakarta"));
                break;
            case "Luxury Spa Bali":
                dummy.add(new EventSchedule("Mon - Sun, 9am - 8pm (By Appointment)", "Ubud, Bali, Indonesia"));
                break;
            default:
                // Default fallback for any other item
                dummy.add(new EventSchedule("Saturday, Jan 1, 2026", "Global Event Center"));
                break;
        }
        return dummy;
    }

    // METHOD TO DYNAMICALLY POPULATE THE SCHEDULE LIST
    private void populateScheduleList() {
        if (schedules == null || schedules.isEmpty() || scheduleListContainer == null) {
            // Since schedule is empty, we hide the list container.
            if (scheduleListContainer != null) {
                scheduleListContainer.setVisibility(View.GONE);
            }
            return;
        }

        // Clear any placeholder views in the container
        scheduleListContainer.removeAllViews();

        // Determine if we have a single, fixed location (like an attraction) or a multi-day event
        boolean isSingleSchedule = schedules.size() == 1;

        for (int i = 0; i < schedules.size(); i++) {
            EventSchedule schedule = schedules.get(i);

            LayoutInflater inflater = LayoutInflater.from(this);

            // --- Date Row ---
            View dateRow = inflater.inflate(R.layout.template_detail_row, scheduleListContainer, false);

            TextView dateLabel = dateRow.findViewById(R.id.DetailLabel);
            TextView dateValue = dateRow.findViewById(R.id.DetailValue);

            // Set data for Date/Hours
            if (isSingleSchedule) {
                dateLabel.setText("Open Hours & Date");
            } else {
                dateLabel.setText(String.format("Date (%d)", i + 1));
            }
            dateValue.setText(schedule.getDate());

            scheduleListContainer.addView(dateRow);

            // --- Location Row ---
            View locationRow = inflater.inflate(R.layout.template_detail_row, scheduleListContainer, false);
            TextView locationLabel = locationRow.findViewById(R.id.DetailLabel);
            TextView locationValue = locationRow.findViewById(R.id.DetailValue);

            // Set data for Location
            locationLabel.setText("Location");
            locationValue.setText(schedule.getLocation());

            // Add location row to the container
            scheduleListContainer.addView(locationRow);

            // Add a separator space if it's not the last item
            if (i < schedules.size() - 1) {
                View separator = new View(this);
                separator.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) (getResources().getDisplayMetrics().density * 16) // 16dp spacing
                ));
                scheduleListContainer.addView(separator);
            }
        }
    }


    /**
     * Creates an Intent to launch BookingActivity, passing necessary data including the price and schedules.
     */
    private void launchBookingActivity() {
        if (title == null) {
            Toast.makeText(this, "Cannot book: Event data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the Intent to launch the BookingActivity
        Intent intent = new Intent(EventActivity.this, BookingActivity.class);

        // Pass event details required by BookingActivity (using its specific constants)
        intent.putExtra(BookingActivity.EXTRA_EVENT_TITLE, title);
        intent.putExtra(BookingActivity.EXTRA_EVENT_SUBTITLE, subtitle);
        intent.putExtra(BookingActivity.EXTRA_EVENT_PRICE, price);

        // Tell BookingActivity to prompt selection from the list
        intent.putExtra(BookingActivity.EXTRA_EVENT_DATE_HINT, "Select a date and location below");

        // PASS THE LIST OF SCHEDULES
        intent.putExtra(BookingActivity.EXTRA_SCHEDULES, (ArrayList<EventSchedule>) schedules);

        startActivity(intent);
    }
}