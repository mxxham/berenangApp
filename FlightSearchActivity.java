package com.example.berenang10;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FlightSearchActivity extends AppCompatActivity {

    private AutoCompleteTextView originInput, destinationInput;
    private EditText departureDateInput, returnDateInput;
    private Spinner passengersSpinner, seatClassSpinner;
    private Button searchButton;
    private ImageView backButton;
    private ImageView swapButton;
    private Calendar departureCalendar, returnCalendar;
    private SimpleDateFormat dateFormatter;

    private List<AirportInfo> allAirportsList;
    private List<String> allCountryNames;
    // Removed: private List<String> allDomesticRegions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_search);

        initializeAirportData();
        initializeCountryNames();
        // Removed: initializeDomesticRegions();
        initializeViews();
        setupSpinners();
        setupDatePickers();
        setupSearchButton();
        setupBackButton();
        setupSwapButton();
        setupAutocomplete();
    }

// ----------------------------------------------------------------------
// --- REMOVED METHOD: initializeDomesticRegions() ---
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
// --- NEW METHOD: INITIALIZE COUNTRY NAMES ---
// ----------------------------------------------------------------------
    /**
     * Extracts all unique country names from the airport data for country-level searching.
     */
    private void initializeCountryNames() {
        Set<String> uniqueCountries = new HashSet<>();
        for (AirportInfo airport : allAirportsList) {
            uniqueCountries.add(airport.getCountry());
        }
        allCountryNames = new ArrayList<>(uniqueCountries);
    }
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
// --- REVISED METHOD 1: INITIALIZE AIRPORT DATA (GLOBAL COVERAGE) ---
// ----------------------------------------------------------------------
    /**
     * Initializes a comprehensive list of airports for the autocomplete adapter,
     * covering Indonesia, major Asian hubs, and key global (including European) hubs.
     */
    private void initializeAirportData() {
        allAirportsList = new ArrayList<>();

        // ===================================
        // === INDONESIA DOMESTIC HUBS ===
        // ===================================

        // JAVA & BALI
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("CGK", "Soekarno-Hatta International Airport", "Jakarta", "Indonesia"),
                new AirportInfo("HLP", "Halim Perdanakusuma Airport", "Jakarta", "Indonesia"),
                new AirportInfo("DPS", "Ngurah Rai International Airport", "Denpasar, Bali", "Indonesia"),
                new AirportInfo("SUB", "Juanda International Airport", "Surabaya", "Indonesia"),
                new AirportInfo("SRG", "Ahmad Yani International Airport", "Semarang", "Indonesia"),
                new AirportInfo("JOG", "Yogyakarta International Airport", "Yogyakarta", "Indonesia"),
                new AirportInfo("BDO", "Husein Sastranegara International Airport", "Bandung", "Indonesia"),
                new AirportInfo("MLG", "Abdul Rachman Saleh Airport", "Malang", "Indonesia")
        ));

        // SUMATRA ISLAND
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("KNO", "Kualanamu International Airport", "Medan", "Indonesia"),
                new AirportInfo("PLM", "Sultan Mahmud Badaruddin II Airport", "Palembang", "Indonesia"),
                new AirportInfo("PDG", "Minangkabau International Airport", "Padang", "Indonesia"),
                new AirportInfo("PKU", "Sultan Syarif Kasim II Airport", "Pekanbaru", "Indonesia"),
                new AirportInfo("BTH", "Hang Nadim International Airport", "Batam", "Indonesia"),
                new AirportInfo("BTJ", "Sultan Iskandar Muda International Airport", "Banda Aceh", "Indonesia")
        ));

        // BORNEO (KALIMANTAN) ISLAND
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("BPN", "Sultan Aji Muhammad Sulaiman Airport", "Balikpapan", "Indonesia"),
                new AirportInfo("BDJ", "Syamsudin Noor International Airport", "Banjarmasin", "Indonesia"),
                new AirportInfo("PNK", "Supadio International Airport", "Pontianak", "Indonesia"),
                new AirportInfo("TRK", "Juwata International Airport", "Tarakan", "Indonesia")
        ));

        // SULAWESI ISLAND
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("UPG", "Sultan Hasanuddin International Airport", "Makassar", "Indonesia"),
                new AirportInfo("MDC", "Sam Ratulangi International Airport", "Manado", "Indonesia"),
                new AirportInfo("PLW", "Mutiara SIS Al-Jufrie Airport", "Palu", "Indonesia")
        ));

        // NUSA TENGGARA (EAST INDONESIA)
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("LOP", "Lombok International Airport", "Lombok", "Indonesia"),
                new AirportInfo("KOE", "El Tari Airport", "Kupang", "Indonesia"),
                new AirportInfo("LBJ", "Komodo International Airport", "Labuan Bajo", "Indonesia")
        ));

        // MALUKU (MOLUCCAS) ISLANDS & PAPUA ISLAND
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("AMQ", "Pattimura International Airport", "Ambon", "Indonesia"),
                new AirportInfo("TTE", "Sultan Babullah Airport", "Ternate", "Indonesia"),
                new AirportInfo("DJJ", "Sentani International Airport", "Jayapura", "Indonesia"),
                new AirportInfo("MKQ", "Mopah International Airport", "Merauke", "Indonesia"),
                new AirportInfo("SOQ", "Dominique Edward Osok Airport", "Sorong", "Indonesia")
        ));

        // ===================================
        // === ASIA & MIDDLE EAST HUBS ===
        // ===================================

        // JAPAN 🇯🇵
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("HND", "Haneda Airport", "Tokyo", "Japan"),
                new AirportInfo("NRT", "Narita International Airport", "Tokyo", "Japan"),
                new AirportInfo("KIX", "Kansai International Airport", "Osaka", "Japan"),
                new AirportInfo("FUK", "Fukuoka Airport", "Fukuoka", "Japan"),
                new AirportInfo("CTS", "New Chitose Airport", "Sapporo", "Japan")
        ));

        // SOUTH KOREA 🇰🇷
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("ICN", "Incheon International Airport", "Seoul", "South Korea"),
                new AirportInfo("GMP", "Gimpo International Airport", "Seoul", "South Korea"),
                new AirportInfo("PUS", "Gimhae International Airport", "Busan", "South Korea"),
                new AirportInfo("CJU", "Jeju International Airport", "Jeju", "South Korea")
        ));

        // CHINA & HONG KONG 🇨🇳🇭🇰
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("PEK", "Beijing Capital International Airport", "Beijing", "China"),
                new AirportInfo("PVG", "Shanghai Pudong International Airport", "Shanghai", "China"),
                new AirportInfo("CAN", "Guangzhou Baiyun International Airport", "Guangzhou", "China"),
                new AirportInfo("SZX", "Shenzhen Bao'an International Airport", "Shenzhen", "China"),
                new AirportInfo("HKG", "Hong Kong International Airport", "Hong Kong", "China")
        ));

        // SOUTHEAST ASIA
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("SIN", "Changi Airport", "Singapore", "Singapore"),
                new AirportInfo("KUL", "Kuala Lumpur International Airport", "Kuala Lumpur", "Malaysia"),
                new AirportInfo("BKK", "Suvarnabhumi Airport", "Bangkok", "Thailand"),
                new AirportInfo("DMK", "Don Mueang International Airport", "Bangkok", "Thailand"),
                new AirportInfo("MNL", "Ninoy Aquino International Airport", "Manila", "Philippines"),
                new AirportInfo("SGN", "Tan Son Nhat International Airport", "Ho Chi Minh City", "Vietnam"),
                new AirportInfo("HAN", "Noi Bai International Airport", "Hanoi", "Vietnam"),
                new AirportInfo("TPE", "Taiwan Taoyuan International Airport", "Taipei", "Taiwan")
        ));

        // INDIA & BANGLADESH 🇮🇳🇧🇩
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("DEL", "Indira Gandhi International Airport", "Delhi", "India"),
                new AirportInfo("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India"),
                new AirportInfo("BLR", "Kempegowda International Airport", "Bangalore", "India"),
                new AirportInfo("MAA", "Chennai International Airport", "Chennai", "India"),
                new AirportInfo("DAC", "Hazrat Shahjalal International Airport", "Dhaka", "Bangladesh")
        ));

        // MIDDLE EAST
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("DOH", "Hamad International Airport", "Doha", "Qatar"), // Adding DOH for realism
                new AirportInfo("DXB", "Dubai International Airport", "Dubai", "United Arab Emirates"), // Adding DXB for realism
                new AirportInfo("JED", "King Abdulaziz International Airport", "Jeddah", "Saudi Arabia"),
                new AirportInfo("RUH", "King Khalid International Airport", "Riyadh", "Saudi Arabia"),
                new AirportInfo("DMM", "King Fahd International Airport", "Dammam", "Saudi Arabia"),
                new AirportInfo("MED", "Prince Mohammad Bin Abdulaziz Airport", "Medina", "Saudi Arabia")
        ));

        // ===================================
        // === EUROPE MAJOR HUBS (Expanded) ===
        // ===================================

        // UNITED KINGDOM 🇬🇧
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("LHR", "Heathrow Airport", "London", "United Kingdom"),
                new AirportInfo("LGW", "Gatwick Airport", "London", "United Kingdom"),
                new AirportInfo("MAN", "Manchester Airport", "Manchester", "United Kingdom")
        ));

        // GERMANY 🇩🇪
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("FRA", "Frankfurt Airport", "Frankfurt", "Germany"),
                new AirportInfo("MUC", "Munich Airport", "Munich", "Germany")
        ));

        // FRANCE 🇫🇷
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("CDG", "Charles de Gaulle Airport", "Paris", "France"),
                new AirportInfo("ORY", "Orly Airport", "Paris", "France")
        ));

        // NETHERLANDS 🇳🇱
        allAirportsList.add(new AirportInfo("AMS", "Amsterdam Airport Schiphol", "Amsterdam", "Netherlands"));

        // SPAIN 🇪🇸
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("MAD", "Adolfo Suárez Madrid–Barajas Airport", "Madrid", "Spain"),
                new AirportInfo("BCN", "Barcelona–El Prat Airport", "Barcelona", "Spain")
        ));

        // ITALY 🇮🇹
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("FCO", "Leonardo da Vinci–Fiumicino Airport", "Rome", "Italy"),
                new AirportInfo("MXP", "Malpensa Airport", "Milan", "Italy")
        ));

        // OTHER MAJOR EUROPEAN HUBS
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("IST", "Istanbul Airport", "Istanbul", "Turkey"), // Major Euro-Asia Hub
                new AirportInfo("ZRH", "Zurich Airport", "Zurich", "Switzerland"),
                new AirportInfo("VIE", "Vienna International Airport", "Vienna", "Austria"),
                new AirportInfo("CPH", "Copenhagen Airport", "Copenhagen", "Denmark"),
                new AirportInfo("DUB", "Dublin Airport", "Dublin", "Ireland")
        ));


        // ===================================
        // === AMERICA & AUSTRALIA HUBS ===
        // ===================================

        // USA 🇺🇸
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("JFK", "John F. Kennedy International Airport", "New York", "United States"),
                new AirportInfo("LAX", "Los Angeles International Airport", "Los Angeles", "United States"),
                new AirportInfo("SFO", "San Francisco International Airport", "San Francisco", "United States")
        ));

        // AUSTRALIA 🇦🇺
        allAirportsList.addAll(Arrays.asList(
                new AirportInfo("SYD", "Sydney Airport", "Sydney", "Australia"),
                new AirportInfo("MEL", "Melbourne Airport", "Melbourne", "Australia")
        ));
    }
// ----------------------------------------------------------------------


    // ----------------------------------------------------------------------
// --- REVISED METHOD: setupAutocomplete() ---
// ----------------------------------------------------------------------
    private void setupAutocomplete() {
        // Now only passing the country list (masterRegionList is replaced with an empty list or null)
        AirportInfoAdapter adapter = new AirportInfoAdapter(this, allAirportsList, allCountryNames);

        originInput.setAdapter(adapter);
        destinationInput.setAdapter(adapter);

        // Listener confirms the selection and provides feedback
        AdapterView.OnItemClickListener listener = (parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);

            if (selectedItem instanceof AirportInfo) {
                // User selected a specific airport
                AirportInfo selectedAirport = (AirportInfo) selectedItem;
                Toast.makeText(FlightSearchActivity.this,
                        "Departure/Arrival set to: " + selectedAirport.getIataCode(),
                        Toast.LENGTH_SHORT).show();
            } else if (selectedItem instanceof String) {
                // User selected a Country name
                String selectedLocation = (String) selectedItem;
                Toast.makeText(FlightSearchActivity.this,
                        "Displaying airports in " + selectedLocation + ". Select an airport to continue.",
                        Toast.LENGTH_LONG).show();
            }
        };

        originInput.setOnItemClickListener(listener);
        destinationInput.setOnItemClickListener(listener);
    }
// ----------------------------------------------------------------------

    private void initializeViews() {
        originInput = findViewById(R.id.origin_input);
        destinationInput = findViewById(R.id.destination_input);
        departureDateInput = findViewById(R.id.departure_date_input);
        returnDateInput = findViewById(R.id.return_date_input);
        passengersSpinner = findViewById(R.id.passengers_spinner);
        seatClassSpinner = findViewById(R.id.seat_class_spinner);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_button);
        swapButton = findViewById(R.id.swap_button);

        departureCalendar = Calendar.getInstance();
        returnCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }

    private void setupSwapButton() {
        swapButton.setOnClickListener(v -> {
            String originText = originInput.getText().toString();
            String destinationText = destinationInput.getText().toString();

            originInput.setText(destinationText, false);
            destinationInput.setText(originText, false);
        });
    }

    private void setupSpinners() {
        // Passengers spinner
        String[] passengers = {"1 Passenger", "2 Passengers", "3 Passengers", "4 Passengers", "5+ Passengers"};
        ArrayAdapter<String> passengersAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, passengers);
        passengersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        passengersSpinner.setAdapter(passengersAdapter);

        // Seat class spinner
        String[] seatClasses = {"Economy", "Premium Economy", "Business", "First Class"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, seatClasses);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatClassSpinner.setAdapter(classAdapter);
    }

    private void setupDatePickers() {
        departureDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    FlightSearchActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        departureCalendar.set(year, month, dayOfMonth);
                        departureDateInput.setText(dateFormatter.format(departureCalendar.getTime()));
                    },
                    departureCalendar.get(Calendar.YEAR),
                    departureCalendar.get(Calendar.MONTH),
                    departureCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        returnDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    FlightSearchActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        returnCalendar.set(year, month, dayOfMonth);
                        returnDateInput.setText(dateFormatter.format(returnCalendar.getTime()));
                    },
                    returnCalendar.get(Calendar.YEAR),
                    returnCalendar.get(Calendar.MONTH),
                    returnCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(departureCalendar.getTimeInMillis());
            datePickerDialog.show();
        });
    }

    private void setupSearchButton() {
        searchButton.setOnClickListener(v -> {
            if (validateInputs()) {
                performSearch();
            }
        });
    }

    private boolean validateInputs() {
        if (originInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter origin city, airport code, or country", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (destinationInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter destination city, airport code, or country", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (departureDateInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select departure date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Extracts the 3-letter IATA code from the formatted string provided by the Autocomplete.
     * Example input: "Jakarta (CGK) - Soekarno-Hatta International Airport"
     */
    private String extractAirportCode(String formattedText) {
        int startParen = formattedText.indexOf('(');
        int endParen = formattedText.indexOf(')');

        // Check for (XXX) format
        if (startParen != -1 && endParen != -1 && endParen == startParen + 4) {
            String code = formattedText.substring(startParen + 1, endParen);
            return code.toUpperCase(Locale.getDefault());
        }

        // If the user manually typed a code/city/country without selecting an item, use the raw input
        return formattedText.trim();
    }

    /**
     * Creates the FlightSearchParams Parcelable object and passes it
     * to the FlightResultsActivity via the Intent.
     */
    private void performSearch() {
        try {
            String originCode = extractAirportCode(originInput.getText().toString());
            String destinationCode = extractAirportCode(destinationInput.getText().toString());

            // Check if the user is trying to search with a Country Name (Region check removed).
            if (allCountryNames.contains(originCode) || allCountryNames.contains(destinationCode))
            {
                Toast.makeText(this, "Please select a specific airport, not a country name.", Toast.LENGTH_LONG).show();
                return;
            }

            // 1. Create the Parcelable object using airport codes
            FlightSearchParams params = new FlightSearchParams(
                    originCode, // Use extracted code
                    destinationCode, // Use extracted code
                    departureDateInput.getText().toString(),
                    returnDateInput.getText().toString(),
                    passengersSpinner.getSelectedItemPosition() + 1,
                    seatClassSpinner.getSelectedItem().toString()
            );

            // 2. Start the FlightResultsActivity and pass the Parcelable object
            Intent intent = new Intent(this, FlightResultsActivity.class);
            intent.putExtra("search_params", params);
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this, "Error during search execution: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // ====================================================================================
    // === MODIFIED CLASS: AirportInfoAdapter (Region Logic Removed) ======================
    // ====================================================================================

    /**
     * Custom ArrayAdapter for AirportInfo objects and Country names.
     */
    public static class AirportInfoAdapter extends ArrayAdapter<Object> {

        private final Context context;
        private final List<AirportInfo> masterAirportList;
        private final List<String> masterCountryList;
        // Removed: private final List<String> masterRegionList;
        private List<Object> filteredResults;

        public AirportInfoAdapter(Context context, List<AirportInfo> airportList, List<String> countryList) {
            super(context, R.layout.airport_autocomplete_dropdown);
            this.context = context;
            this.masterAirportList = airportList;
            this.masterCountryList = countryList;
            // Removed: this.masterRegionList = regionList;
            this.filteredResults = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return filteredResults.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredResults.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.airport_autocomplete_dropdown, parent, false);
            }

            TextView title = convertView.findViewById(R.id.airport_title);
            TextView subtitle = convertView.findViewById(R.id.airport_subtitle);

            Object item = getItem(position);

            if (item instanceof AirportInfo) {
                AirportInfo airport = (AirportInfo) item;
                // Format: City/Country (IATA)
                title.setText(String.format("%s (%s)", airport.getCity(), airport.getIataCode()));
                subtitle.setText(airport.getName());
            } else if (item instanceof String) {
                // Handle Country Name only (Region handling removed)
                String suggestion = (String) item;
                title.setText(suggestion);

                if (masterCountryList.contains(suggestion)) {
                    subtitle.setText("Search All Airports in " + suggestion + "...");
                } else {
                    subtitle.setText("");
                }
            }

            return convertView;
        }

        // Removed: isAirportInRegion helper method

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    List<Object> suggestions = new ArrayList<>();

                    if (constraint != null) {
                        String filterText = constraint.toString().toUpperCase(Locale.getDefault()).trim();
                        String rawConstraint = constraint.toString(); // Keep original casing for exact match check

                        // --- FILTER SCENARIO 1: User selected a Country (exact match) ---
                        if (masterCountryList.contains(rawConstraint)) {
                            String targetLocation = rawConstraint;

                            // Show all airports in that country
                            for (AirportInfo airport : masterAirportList) {
                                if (airport.getCountry().equals(targetLocation)) {
                                    suggestions.add(airport);
                                }
                            }
                        }
                        // --- FILTER SCENARIO 2: User is actively typing (General Search) ---
                        else {
                            // 1. Check for Country Match (High Priority)
                            for (String country : masterCountryList) {
                                if (country.toUpperCase(Locale.getDefault()).startsWith(filterText)) {
                                    suggestions.add(country);
                                }
                            }

                            // 2. Check for Airport/City/Code Match
                            for (AirportInfo airport : masterAirportList) {
                                if (airport.getIataCode().toUpperCase(Locale.getDefault()).contains(filterText) ||
                                        airport.getName().toUpperCase(Locale.getDefault()).contains(filterText) ||
                                        airport.getCity().toUpperCase(Locale.getDefault()).contains(filterText) ||
                                        airport.getCountry().toUpperCase(Locale.getDefault()).contains(filterText))
                                {
                                    suggestions.add(airport);
                                }
                            }
                            // Note: Domestic Region Check (Step 2) is now removed
                        }
                    }

                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredResults = (List<Object>) results.values;
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    if (resultValue instanceof AirportInfo) {
                        // When an airport is selected, format it for the input box
                        AirportInfo airport = (AirportInfo) resultValue;
                        return String.format("%s (%s) - %s", airport.getCity(), airport.getIataCode(), airport.getName());
                    } else if (resultValue instanceof String) {
                        // When a country is selected, just put the name in the input box
                        return (String) resultValue;
                    }
                    return super.convertResultToString(resultValue);
                }
            };
        }
    }
}