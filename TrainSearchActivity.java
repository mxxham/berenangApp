package com.example.berenang10;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch; // Import required for Switch
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TrainSearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialButton searchTrainsButton;
    private EditText dateDisplayInput;
    private TextView passengerCountText;
    private MaterialCardView dateCardView;
    private MaterialCardView passengerCardView;
    private EditText fromStationInput;
    private EditText toStationInput;
    private Switch roundTripSwitch; // 🆕 Added Switch variable

    private Calendar selectedDate;
    private int passengerCount = 1;
    private boolean isRoundTrip = false; // 🆕 State for Round Trip

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Full-bleed Header Fix (Draw content behind status bar)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_train_search);

        // 2. Initialize Views
        toolbar = findViewById(R.id.toolbar);
        searchTrainsButton = findViewById(R.id.search_trains_button);
        dateCardView = findViewById(R.id.date_card_view);
        passengerCardView = findViewById(R.id.passenger_card_view);
        dateDisplayInput = findViewById(R.id.date_display_input);
        fromStationInput = findViewById(R.id.from_station_input);
        toStationInput = findViewById(R.id.to_station_input);
        passengerCountText = findViewById(R.id.passenger_count_text);

        // 🆕 Initialize the round trip switch based on your XML structure
        roundTripSwitch = findViewById(R.id.date_card_view).findViewById(R.id.switch_round_trip); // Assuming an ID 'switch_round_trip'
        // FIX: If the switch does not have an ID, you need to navigate to it or assign one in XML.
        // For now, let's try to find it within the date_card_view structure.
        if (roundTripSwitch == null) {
            // Attempt a general search if the ID is missing
            // This is fragile, better to ensure it has an ID in XML
            roundTripSwitch = findViewById(R.id.date_card_view).findViewWithTag("round_trip_switch");
        }


        // 3. Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 4. Initialize default state and mock data
        selectedDate = Calendar.getInstance();
        updateDateDisplay();
        updatePassengerDisplay();

        fromStationInput.setText("Jakarta (GMR)");
        toStationInput.setText("Surabaya (SGU)");


        // 5. Setup Click Listeners
        View.OnClickListener stationClickListener = v -> openStationSelection();

        if (fromStationInput != null) {
            fromStationInput.setOnClickListener(stationClickListener);
        }
        if (toStationInput != null) {
            toStationInput.setOnClickListener(stationClickListener);
        }

        // 🆕 Setup Swap Icon Listener
        View swapIcon = findViewById(R.id.swap_icon_container);
        if (swapIcon != null) {
            swapIcon.setOnClickListener(v -> swapStations());
        }

        // Setup the date picker click listener
        View.OnClickListener dateClickListener = v -> showMaterialDatePicker();
        if (dateCardView != null) {
            dateCardView.setOnClickListener(dateClickListener);
        }
        if (dateDisplayInput != null) {
            dateDisplayInput.setOnClickListener(dateClickListener);
        }

        // Setup the passenger selection click listener
        if (passengerCardView != null) {
            passengerCardView.setOnClickListener(v -> openPassengerSelection());
        }

        // 🆕 Setup Round Trip Switch Listener
        if (roundTripSwitch != null) {
            roundTripSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isRoundTrip = isChecked;
                Toast.makeText(this, isChecked ? "Round trip selected" : "One-way selected", Toast.LENGTH_SHORT).show();
                // If you had a return date field, you would show/hide it here
            });
        }

        if (searchTrainsButton != null) {
            searchTrainsButton.setOnClickListener(v -> performTrainSearch());
        }
    }

    // --- Date Picker Method (Using MaterialDatePicker) ---

    private void showMaterialDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Departure Date");
        builder.setSelection(selectedDate.getTimeInMillis());
        builder.setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        materialDatePicker.addOnPositiveButtonClickListener(
                (MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
                    Calendar selectedUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    selectedUtc.setTimeInMillis(selection);

                    Calendar localCal = Calendar.getInstance();
                    localCal.set(
                            selectedUtc.get(Calendar.YEAR),
                            selectedUtc.get(Calendar.MONTH),
                            selectedUtc.get(Calendar.DAY_OF_MONTH)
                    );
                    selectedDate = localCal;

                    updateDateDisplay();
                });

        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    // --- Passenger Selection Method (Using AlertDialog with NumberPicker) ---

    private void openPassengerSelection() {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        // Assumes dialog_passenger_picker.xml exists
        View dialogView = inflater.inflate(R.layout.dialog_passenger_picker, null);
        d.setView(dialogView);

        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(1);
        numberPicker.setValue(passengerCount);
        numberPicker.setWrapSelectorWheel(false);

        d.setTitle("Select Number of Passengers (1-10)");
        d.setPositiveButton("Set", (dialogInterface, i) -> {
            passengerCount = numberPicker.getValue();
            updatePassengerDisplay();
        });
        d.setNegativeButton("Cancel", null);
        d.show();
    }

    // --- New Method: Swap Stations ---
    private void swapStations() {
        String fromText = fromStationInput.getText().toString();
        String toText = toStationInput.getText().toString();

        fromStationInput.setText(toText);
        toStationInput.setText(fromText);

        Toast.makeText(this, "Stations swapped!", Toast.LENGTH_SHORT).show();
    }
    // ---------------------------------

    private void openStationSelection() {
        Toast.makeText(this, "Open Station Selection Screen", Toast.LENGTH_SHORT).show();
    }

    private void updateDateDisplay() {
        if (dateDisplayInput != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
            dateDisplayInput.setText(dateFormat.format(selectedDate.getTime()));
        }
    }

    private void updatePassengerDisplay() {
        if (passengerCountText != null) {
            passengerCountText.setText(String.format(Locale.getDefault(), "%d Passenger%s",
                    passengerCount, passengerCount > 1 ? "s" : ""));
        }
    }

    private void performTrainSearch() {
        String fromStation = fromStationInput.getText().toString();
        String toStation = toStationInput.getText().toString();

        if (fromStation.isEmpty() || toStation.isEmpty()) {
            Toast.makeText(this, "Please select 'From' and 'To' stations.", Toast.LENGTH_SHORT).show();
            return;
        }

        // NOTE: TrainSearchParams must be a Parcelable class defined elsewhere in your project
        TrainSearchParams searchParams = new TrainSearchParams(
                fromStation,
                toStation,
                selectedDate.getTimeInMillis(),
                passengerCount,
                isRoundTrip // 🆕 Pass the Round Trip status
        );

        Intent intent = new Intent(this, TrainResultsActivity.class);
        intent.putExtra("search_params", searchParams);

        startActivity(intent);
    }
}