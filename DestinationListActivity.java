package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// ⭐ NOTE: Replace 'DestinationListActivity' with the actual name of your Activity
public class DestinationListActivity extends AppCompatActivity
        implements DestinationAdapter.OnDestinationClickListener { // 1. Implement the interface

    private RecyclerView destinationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_list);

        destinationRecyclerView = findViewById(R.id.destination_recycler_view);
        destinationRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        List<Destination> destinations = getMockDestinations();

        // Pass 'this' as the listener
        DestinationAdapter adapter = new DestinationAdapter(destinations, this);
        destinationRecyclerView.setAdapter(adapter);
    }

    // ----------------------------------------------------------------------

    /**
     * Handles the click event from the DestinationAdapter.
     * @param destination The Destination object that was clicked.
     */
    @Override
    public void onDestinationClick(Destination destination) {
        // A. Generate default check-in/out dates
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String checkInDate = dateFormat.format(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, 3); // Check out 3 days later
        String checkOutDate = dateFormat.format(cal.getTime());

        // B. Create HotelSearchParams
        HotelSearchParams searchParams = new HotelSearchParams(
                destination.getName(), // ⭐ Pass the clicked destination's name
                checkInDate,
                checkOutDate,
                2, // Default 2 guests
                1  // Default 1 room
        );

        // C. Start the HotelResultsActivity
        Intent intent = new Intent(this, HotelResultsActivity.class);
        intent.putExtra("search_params", searchParams);
        startActivity(intent);
    }

    // Example: Mock data source (ensure destination names match expected results, e.g., "Bali")
    private List<Destination> getMockDestinations() {
        List<Destination> list = new ArrayList<>();
        list.add(new Destination("Bali", "Indonesia", "IDR 500k", "url_for_bali_image"));
        list.add(new Destination("Jakarta", "Indonesia", "IDR 600k", "url_for_jakarta_image"));
        list.add(new Destination("Surabaya", "Indonesia", "IDR 400k", "url_for_surabaya_image"));
        return list;
    }
}