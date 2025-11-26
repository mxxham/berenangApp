package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast; // Added for error handling
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays; // New Import
import java.util.Collections; // New Import
import java.util.Comparator; // New Import
import java.util.HashSet; // New Import
import java.util.List;
import java.util.Set; // New Import

public class FlightResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FlightResultsAdapter adapter;
    private ProgressBar progressBar;
    private TextView searchInfoText, noResultsText;
    private FlightSearchParams searchParams;

    // New: Set to store all domestic airport codes for quick lookup
    private Set<String> domesticAirportCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_results);

        // Retrieve search parameters using the key "search_params"
        searchParams = getIntent().getParcelableExtra("search_params");

        // FIX: Critical check to ensure searchParams is not null
        if (searchParams == null) {
            // The activity cannot proceed without search parameters.
            Toast.makeText(this, "Error: Search parameters are missing. Returning to search.", Toast.LENGTH_LONG).show();
            finish(); // Close this activity
            return; // Stop execution
        }

        // Initialize the set of domestic airport codes
        initializeDomesticAirportCodes();

        // Only proceed if searchParams is valid
        initializeViews();
        setupRecyclerView();
        performSearch();
    }

    // ----------------------------------------------------------------------
    // --- METHOD 1: INITIALIZE DOMESTIC CODES ---
    // ----------------------------------------------------------------------
    /**
     * Defines all relevant Indonesian (Domestic) IATA airport codes.
     */
    private void initializeDomesticAirportCodes() {
        domesticAirportCodes = new HashSet<>(Arrays.asList(
                "CGK", "HLP", "DPS", "SUB", "SRG", "JOG", "BDO", "MLG", // Java & Bali
                "KNO", "PLM", "PDG", "PKU", "BTH", "BTJ", // Sumatra
                "BPN", "BDJ", "PNK", "TRK", // Kalimantan
                "UPG", "MDC", "PLW", // Sulawesi
                "LOP", "KOE", "LBJ", // Nusa Tenggara
                "AMQ", "TTE", "DJJ", "MKQ", "SOQ" // Maluku & Papua
        ));
    }

    // ----------------------------------------------------------------------
    // --- METHOD 2: FLIGHT TYPE CHECKER ---
    // ----------------------------------------------------------------------
    /**
     * Determines if a flight is domestic or international based on IATA codes.
     */
    private boolean isDomesticFlight(String originCode, String destinationCode) {
        String origin = originCode.toUpperCase();
        String destination = destinationCode.toUpperCase();

        boolean isOriginDomestic = domesticAirportCodes.contains(origin);
        boolean isDestinationDomestic = domesticAirportCodes.contains(destination);

        // It is a DOMESTIC flight ONLY if both points are domestic.
        return isOriginDomestic && isDestinationDomestic;
    }
    // ----------------------------------------------------------------------

    private void initializeViews() {
        recyclerView = findViewById(R.id.flights_recycler);
        progressBar = findViewById(R.id.progress_bar);
        searchInfoText = findViewById(R.id.search_info_text);
        noResultsText = findViewById(R.id.no_results_text);

        // This call is now safe because we checked for null in onCreate
        String searchInfo = searchParams.getOrigin() + " → " + searchParams.getDestination()
                + " • " + searchParams.getDepartureDate()
                + " • " + searchParams.getPassengers() + " passenger(s)";
        searchInfoText.setText(searchInfo);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlightResultsAdapter(new ArrayList<>(), flight -> {
            // Navigate to passenger details
            Intent intent = new Intent(FlightResultsActivity.this, PassengerDetailsActivity.class);
            intent.putExtra("flight", flight);
            intent.putExtra("search_params", searchParams);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void performSearch() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);

        // Simulate API call with 2 second delay
        new Handler().postDelayed(() -> {
            List<Flight> flights = generateMockFlights();

            progressBar.setVisibility(View.GONE);

            if (flights.isEmpty()) {
                noResultsText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateFlights(flights);
            }
        }, 2000);
    }

    /**
     * Generates a list of mock flights, filters them, and then sorts them to prioritize direct flights.
     */
    private List<Flight> generateMockFlights() {
        // Get raw, potentially formatted, airport codes from search params
        String originCode = searchParams.getOrigin();
        String destinationCode = searchParams.getDestination();

        // Determine the type of flight search requested
        boolean isDomesticSearch = isDomesticFlight(originCode, destinationCode);

        // 1. Generate ALL possible mock flights (both domestic and international)
        List<Flight> allFlights = generateAllMockFlights(originCode, destinationCode);

        // 2. Filter the results based on the search type
        List<Flight> filteredFlights = new ArrayList<>();

        if (isDomesticSearch) {
            // If the search is domestic, show only short/direct flights (FL001-FL006)
            for (Flight flight : allFlights) {
                if (flight.getFlightId().matches("FL00[1-6]")) {
                    filteredFlights.add(flight);
                }
            }
        } else {
            // If the search is international, show all long-haul options (FL007-FL014)
            for (Flight flight : allFlights) {
                // FL007-FL014 are all international connection and direct flights
                if (flight.getFlightId().matches("FL00[7-9]|FL01[0-4]")) {
                    filteredFlights.add(flight);
                }
            }
        }

        // 3. SORTING LOGIC: Prioritize direct flights for both domestic and international results.
        Collections.sort(filteredFlights, new Comparator<Flight>() {
            @Override
            public int compare(Flight f1, Flight f2) {
                // Direct flights have isDirect = true (or numStops = 0, which is implied by the boolean)

                // If f1 is direct and f2 is not, f1 comes first (-1)
                if (f1.isDirect() && !f2.isDirect()) {
                    return -1;
                }
                // If f2 is direct and f1 is not, f2 comes first (1)
                if (!f1.isDirect() && f2.isDirect()) {
                    return 1;
                }
                // If both are the same (both direct or both connecting), maintain relative order (0)
                return 0;
            }
        });


        return filteredFlights;
    }

    /**
     * Internal method to generate the master list of all mock flights (domestic and international)
     */
    private List<Flight> generateAllMockFlights(String origin, String destination) {
        List<Flight> flights = new ArrayList<>();
        String seatClass = searchParams.getSeatClass();

        // --------------------------------------------------------
        // --- DOMESTIC/REGIONAL FLIGHTS (FL001-FL006) ---
        // FL001, FL002, FL003, FL004, FL006 are DIRECT
        // --------------------------------------------------------
        flights.add(new Flight("FL001", "Garuda Indonesia", "GA-401", origin, destination,
                "08:00", "10:30", "2h 30m", seatClass,
                1250000, 45, true, ""));

        flights.add(new Flight("FL002", "Lion Air", "JT-610", origin, destination,
                "10:15", "12:45", "2h 30m", seatClass,
                950000, 32, true, ""));

        flights.add(new Flight("FL003", "Citilink", "QG-723", origin, destination,
                "13:30", "16:15", "2h 45m", seatClass,
                850000, 28, true, ""));

        flights.add(new Flight("FL004", "Batik Air", "ID-6512", origin, destination,
                "15:45", "18:30", "2h 45m", seatClass,
                1100000, 52, true, ""));

        flights.add(new Flight("FL005", "AirAsia", "QZ-534", origin, destination,
                "06:30", "10:45", "4h 15m", seatClass,
                750000, 18, false, "1 stop in Surabaya")); // **CONNECTING**

        flights.add(new Flight("FL006", "Garuda Indonesia", "GA-405", origin, destination,
                "19:00", "21:30", "2h 30m", seatClass,
                1350000, 38, true, ""));

        // --------------------------------------------------------
        // --- INTERNATIONAL CONNECTION FLIGHTS (FL007-FL010) - All are NOT direct (false) ---
        // --------------------------------------------------------

        flights.add(new Flight("FL007", "Singapore Airlines", "SQ-965/SQ-632", origin, destination,
                "08:50", "19:10", "10h 20m", seatClass,
                18000000, 25, false, "1 stop in Singapore (SIN), 2h 45m layover"));

        flights.add(new Flight("FL008", "Korean Air", "KE-628/KE-713", origin, destination,
                "23:45", "14:15 (+1)", "15h 30m", seatClass,
                16500000, 18, false, "1 stop in Seoul (ICN), 4h 0m layover"));

        flights.add(new Flight("FL009", "Cathay Pacific", "CX-778/CX-506", origin, destination,
                "14:00", "23:55", "9h 55m", seatClass,
                15200000, 30, false, "1 stop in Hong Kong (HKG), 1h 30m layover"));

        // Long haul connection for Western routes (Europe/Africa/Americas)
        flights.add(new Flight("FL010", "Qatar Airways", "QR-955/QR-17", origin, destination,
                "16:00", "07:45 (+1)", "18h 45m", seatClass,
                14500000, 40, false, "1 stop in Doha (DOH), 3h 0m layover"));

        // --------------------------------------------------------
        // --- DIRECT INTERNATIONAL FLIGHTS (FL011-FL014) - All are direct (true) ---
        // --------------------------------------------------------

        // FL011: Direct to Asia (e.g., Tokyo, Beijing)
        flights.add(new Flight("FL011", "Garuda Indonesia", "GA-874", origin, destination,
                "09:30", "18:00", "7h 30m", seatClass,
                18500000, 15, true, "Direct Flight"));

        // FL012: Direct to Europe (e.g., Amsterdam, London)
        flights.add(new Flight("FL012", "KLM", "KL-835", origin, destination,
                "20:15", "06:25 (+1)", "15h 10m", seatClass,
                24000000, 12, true, "Direct Flight"));

        // FL013: Direct to North America (e.g., Los Angeles, Vancouver) - Very Long Haul
        flights.add(new Flight("FL013", "Air Canada", "AC-19", origin, destination,
                "10:00", "05:45 (+1)", "19h 45m", seatClass,
                30000000, 8, true, "Direct Flight"));

        // FL014: Direct to Oceania (e.g., Sydney, Melbourne)
        flights.add(new Flight("FL014", "Qantas", "QF-42", origin, destination,
                "11:45", "21:45", "7h 0m", seatClass,
                15000000, 20, true, "Direct Flight"));


        return flights;
    }
}