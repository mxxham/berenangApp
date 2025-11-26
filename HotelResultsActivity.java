package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast; // Added for error messages
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HotelResultsActivity extends AppCompatActivity {

    public static final String EXTRA_SEARCH_PARAMS = "search_params"; // Define a constant

    private RecyclerView recyclerView;
    private HotelResultsAdapter adapter;
    private ProgressBar progressBar;
    private TextView searchInfoText, noResultsText;
    private ImageView backButton;
    private HotelSearchParams searchParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_results);

        // --- FIX: Use the defined constant and perform a null check ---
        searchParams = getIntent().getParcelableExtra(EXTRA_SEARCH_PARAMS);

        if (searchParams == null) {
            // Handle the critical error: cannot run without search parameters
            Toast.makeText(this, "Error: Missing hotel search parameters.", Toast.LENGTH_LONG).show();
            finish();
            return; // Stop execution
        }
        // -------------------------------------------------------------

        initializeViews();
        setupRecyclerView();
        performSearch();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.hotels_recycler);
        progressBar = findViewById(R.id.progress_bar);
        searchInfoText = findViewById(R.id.search_info_text);
        noResultsText = findViewById(R.id.no_results_text);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        // --- FIX: The searchParams is guaranteed not null here because of the check in onCreate() ---
        // If the activity hadn't crashed, this line would be correct:
        String searchInfo = searchParams.getDestination()
                + " • " + searchParams.getCheckInDate() + " - " + searchParams.getCheckOutDate()
                + " • " + searchParams.getGuests() + " guest(s) • " + searchParams.getRooms() + " room(s)";
        searchInfoText.setText(searchInfo);
        // -----------------------------------------------------------------------------------------
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HotelResultsAdapter(new ArrayList<>(), hotel -> {
            Intent intent = new Intent(HotelResultsActivity.this, HotelDetailActivity.class);
            intent.putExtra("hotel", hotel);
            // Use the constant for consistency
            intent.putExtra(EXTRA_SEARCH_PARAMS, searchParams);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void performSearch() {
        // Since we checked for null in onCreate, we assume searchParams is valid here
        if (searchParams == null) return;

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);

        new Handler().postDelayed(() -> {
            List<Hotel> hotels = generateMockHotels();

            progressBar.setVisibility(View.GONE);

            if (hotels.isEmpty()) {
                noResultsText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateHotels(hotels);
            }
        }, 2000);
    }

    // This method correctly relies on the searchParams being non-null (checked in onCreate)
    private List<Hotel> generateMockHotels() {
        List<Hotel> hotels = new ArrayList<>();
        String destination = searchParams.getDestination();

        // The constructor now correctly uses 15 arguments:
        // Hotel(id, name, location, address, rating, reviews, roomType, price, amenities, hasWifi, hasPool, isPetFriendly, freeCancellation, tagline, imageResId)

        hotels.add(new Hotel(
                "H001", "Grand Hyatt " + destination, destination,
                "Jl. MH Thamrin No. 28-30", 4.8, 1250,
                "Deluxe Room", 1500000.0, // FIX: Changed to double
                "Pool, Spa, Gym, Restaurant",
                true, true, true, true,
                "Luxury Stay", // Tagline
                R.drawable.placeholder_hotel_1 // FIX: Added 15th argument
        ));

        hotels.add(new Hotel(
                "H002", "The Ritz-Carlton " + destination, destination,
                "Pacific Place, Jl. Jenderal Sudirman", 4.9, 980,
                "Executive Suite", 2800000.0, // FIX: Changed to double
                "Pool, Spa, Gym, Bar, Restaurant",
                true, true, true, true,
                "Five-Star Experience",
                R.drawable.placeholder_hotel_1 // Added 15th argument
        ));

        hotels.add(new Hotel(
                "H003", destination + " Marriott Hotel", destination,
                "Jl. Dr. Ide Anak Agung Gde Agung", 4.6, 756,
                "Superior Room", 1200000.0, // FIX: Changed to double
                "Pool, Gym, Restaurant",
                true, true, true, true,
                "Business Comfort",
                R.drawable.placeholder_hotel_1 // Added 15th argument
        ));

        hotels.add(new Hotel(
                "H004", "Four Seasons " + destination, destination,
                "Jl. Gatot Subroto", 4.9, 1100,
                "Premier Room", 3200000.0, // FIX: Changed to double
                "Pool, Spa, Gym, Bar, Restaurant, Concierge",
                true, true, true, true,
                "Unmatched Elegance",
                R.drawable.placeholder_hotel_1 // Added 15th argument
        ));

        hotels.add(new Hotel(
                "H005", "Aloft " + destination, destination,
                "Jl. TB Simatupang", 4.4, 620,
                "Loft Room", 800000.0, // FIX: Changed to double
                "Pool, Gym, Bar",
                true, true, true, true,
                "Modern & Lively",
                R.drawable.placeholder_hotel_1 // Added 15th argument
        ));

        hotels.add(new Hotel(
                "H006", destination + " Boutique Hotel", destination,
                "Jl. Kemang Raya", 4.7, 450,
                "Classic Room", 950000.0, // FIX: Changed to double
                "Restaurant, Cafe, Rooftop Bar",
                true, true, false, true,
                "Unique Charm",
                R.drawable.placeholder_hotel_1 // Added 15th argument
        ));

        return hotels;
    }
}