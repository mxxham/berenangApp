package com.example.berenang10;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HotelSearchActivity extends AppCompatActivity {

    private EditText destinationInput, checkInDateInput, checkOutDateInput;
    private Spinner guestsSpinner, roomsSpinner;
    private Button searchButton;
    private ImageView backButton;
    private Calendar checkInCalendar, checkOutCalendar;
    private SimpleDateFormat dateFormatter;

    // Define the key for the Intent extra
    public static final String EXTRA_SEARCH_PARAMS = "search_params";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_search);

        initializeViews();
        setupSpinners();
        setupDatePickers();
        setupSearchButton();
        setupBackButton();
    }

    private void initializeViews() {
        destinationInput = findViewById(R.id.destination_input);
        checkInDateInput = findViewById(R.id.checkin_date_input);
        checkOutDateInput = findViewById(R.id.checkout_date_input);
        guestsSpinner = findViewById(R.id.guests_spinner);
        roomsSpinner = findViewById(R.id.rooms_spinner);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_button);

        // --- FIX: Initialize dateFormatter FIRST ---
        dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        // ------------------------------------------

        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);

        // Ensure initial dates are set when the activity loads
        // These lines now execute safely because dateFormatter is initialized
        checkInDateInput.setText(dateFormatter.format(checkInCalendar.getTime()));
        checkOutDateInput.setText(dateFormatter.format(checkOutCalendar.getTime()));

        // Check for and apply incoming search parameters (if activity was launched from SearchActivity)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_SEARCH_PARAMS)) {
            // Retrieve parameters if passed from another activity (like SearchActivity)
            HotelSearchParams initialParams = (HotelSearchParams) intent.getSerializableExtra(EXTRA_SEARCH_PARAMS);
            if (initialParams != null) {
                applyInitialSearchParams(initialParams);
            }
        }
    }

    private void applyInitialSearchParams(HotelSearchParams params) {
        destinationInput.setText(params.getDestination());

        // Note: You may need helper methods to convert date strings back to Calendar objects
        // if you want the DatePickers to work correctly after setting the text.
        // For simplicity, we just set the text for now.
        checkInDateInput.setText(params.getCheckInDate());
        checkOutDateInput.setText(params.getCheckOutDate());

        // Optional: Set Spinner positions (requires finding index)
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        // Guests spinner
        String[] guests = {"1 Guest", "2 Guests", "3 Guests", "4 Guests", "5+ Guests"};
        ArrayAdapter<String> guestsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, guests);
        guestsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guestsSpinner.setAdapter(guestsAdapter);

        // Rooms spinner
        String[] rooms = {"1 Room", "2 Rooms", "3 Rooms", "4+ Rooms"};
        ArrayAdapter<String> roomsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, rooms);
        roomsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomsSpinner.setAdapter(roomsAdapter);
    }

    private void setupDatePickers() {
        checkInDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    HotelSearchActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        checkInCalendar.set(year, month, dayOfMonth);
                        checkInDateInput.setText(dateFormatter.format(checkInCalendar.getTime()));

                        // Auto-set checkout to next day, ensuring it's always after check-in
                        if (!checkOutCalendar.after(checkInCalendar)) {
                            checkOutCalendar.setTime(checkInCalendar.getTime());
                            checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                            checkOutDateInput.setText(dateFormatter.format(checkOutCalendar.getTime()));
                        }
                    },
                    checkInCalendar.get(Calendar.YEAR),
                    checkInCalendar.get(Calendar.MONTH),
                    checkInCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Allow today
            datePickerDialog.show();
        });

        checkOutDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    HotelSearchActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        checkOutCalendar.set(year, month, dayOfMonth);
                        checkOutDateInput.setText(dateFormatter.format(checkOutCalendar.getTime()));
                    },
                    checkOutCalendar.get(Calendar.YEAR),
                    checkOutCalendar.get(Calendar.MONTH),
                    checkOutCalendar.get(Calendar.DAY_OF_MONTH)
            );
            // Min date for checkout is the day AFTER check-in (86400000 ms = 24 hours)
            datePickerDialog.getDatePicker().setMinDate(checkInCalendar.getTimeInMillis() + 86400000);
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
        if (destinationInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter destination", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkInDateInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select check-in date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (checkOutDateInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select check-out date", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Final check to ensure check-out is strictly after check-in
        if (!checkOutCalendar.after(checkInCalendar)) {
            Toast.makeText(this, "Check-out must be after check-in date.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // --- NEW HELPER METHOD FOR ROBUST SPINNER VALUE EXTRACTION ---
    private int getSpinnerValue(Spinner spinner) {
        String selectedItem = (String) spinner.getSelectedItem();
        if (selectedItem == null) return 1; // Default to 1

        // Use regex to extract only the digits from the string (handles "1 Guest" or "5+ Guests")
        String numberString = selectedItem.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            return 1; // Fallback value
        }
    }
    // -------------------------------------------------------------


    private void performSearch() {
        HotelSearchParams params = new HotelSearchParams(
                destinationInput.getText().toString().trim(),
                checkInDateInput.getText().toString(),
                checkOutDateInput.getText().toString(),
                // Using the robust helper function
                getSpinnerValue(guestsSpinner),
                getSpinnerValue(roomsSpinner)
        );

        Intent intent = new Intent(this, HotelResultsActivity.class);
        intent.putExtra(EXTRA_SEARCH_PARAMS, params);
        startActivity(intent);
    }
}