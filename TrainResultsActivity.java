package com.example.berenang10;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrainResultsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView headerRouteTextView;
    private TextView headerDetailsTextView;
    private RecyclerView recyclerView;


    // Holds the passenger count received from the previous activity
    private int passengerCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_results);

        // --- UPDATED IDs to match activity_train_results.xml ---
        toolbar = findViewById(R.id.toolbar);

        // FIX 1: Using the correct RecyclerView ID from the XML
        recyclerView = findViewById(R.id.recycler_train_results);

        // FIX 2: Using the correct TextView IDs from the XML
        headerRouteTextView = findViewById(R.id.header_route);
        headerDetailsTextView = findViewById(R.id.header_details);
        // ----------------------------------------------------------------------

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 1. Get Search Parameters and Passenger Count
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("search_params")) {
            Toast.makeText(this, "Search parameters missing. Cannot display results.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TrainSearchParams searchParams;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Retrieve Parcelable using the key "search_params"
            searchParams = intent.getParcelableExtra("search_params", TrainSearchParams.class);
        } else {
            searchParams = (TrainSearchParams) intent.getParcelableExtra("search_params");
        }

        if (searchParams == null) {
            Toast.makeText(this, "Search parameters invalid. Cannot display results.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Using the correct getter from the updated TrainSearchParams class
        this.passengerCount = searchParams.getPassengerCount();

        // Update UI with search info
        String routeText = String.format(Locale.getDefault(),
                "%s → %s",
                searchParams.getFromStation(),
                searchParams.getToStation()
        );

        String detailsText = String.format(Locale.getDefault(),
                "%s | %d Passenger%s",
                // You will need to convert searchParams.getDateMillis() to a readable date string here
                "Date Placeholder",
                passengerCount,
                passengerCount > 1 ? "s" : ""
        );

        if (headerRouteTextView != null) {
            headerRouteTextView.setText(routeText);
        }
        if (headerDetailsTextView != null) {
            headerDetailsTextView.setText(detailsText);
        }

        // 2. Load and Display Data
        List<Train> trainList = generateMockTrainData(searchParams);

        // Check if the RecyclerView was found successfully
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Pass passenger count to the adapter
            recyclerView.setAdapter(new TrainAdapter(this, trainList, this.passengerCount));
        } else {
            // Updated toast message for the new ID
            Toast.makeText(this, "RecyclerView not found in layout (Check if ID is 'recycler_train_results')!", Toast.LENGTH_LONG).show();
        }
    }

    private List<Train> generateMockTrainData(TrainSearchParams params) {
        List<Train> trains = new ArrayList<>();
        // Price is the BASE PRICE PER PERSON
        trains.add(new Train("KA 1", "Argo Wilis", "Executive", params.getFromStation(), params.getToStation(), "05:00", "15:00", "10h 00m", 450000.00, 50, "AC, Dining"));
        trains.add(new Train("KA 7", "Gajayana", "Executive", params.getFromStation(), params.getToStation(), "09:30", "19:30", "10h 00m", 480000.00, 45, "AC, Reclining Seat"));
        trains.add(new Train("KA 10", "Jayabaya", "Economy", params.getFromStation(), params.getToStation(), "14:00", "23:50", "9h 50m", 350000.00, 100, "AC, Power Outlet"));
        trains.add(new Train("KA 4", "Bima", "Executive", params.getFromStation(), params.getToStation(), "17:30", "03:30", "10h 00m", 465000.00, 55, "AC, Dining"));
        return trains;
    }

    // --- TrainAdapter Class (Inner Class) ---

    private static class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {

        private final Context context;
        private final List<Train> trainList;
        private final int passengerCount;

        public TrainAdapter(Context context, List<Train> trainList, int passengerCount) {
            this.context = context;
            this.trainList = trainList;
            this.passengerCount = passengerCount;
        }

        @Override
        public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_train_result, parent, false);
            return new TrainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TrainViewHolder holder, int position) {
            holder.bind(trainList.get(position), passengerCount);
        }

        @Override
        public int getItemCount() {
            return trainList.size();
        }

        // --- TrainViewHolder Class - FIXED ---
        public class TrainViewHolder extends RecyclerView.ViewHolder {
            // 1. Declare all views based on the new item_train_result.xml
            private final TextView textTrainNameClass;
            private final TextView textTrainPrice;
            private final TextView textDepartureTime;
            private final TextView textDepartureStation;
            private final TextView textDuration;
            private final TextView textArrivalTime;
            private final TextView textArrivalStation;
            private final MaterialButton btnSelectTrain;

            public TrainViewHolder(View itemView) {
                super(itemView);

                // 2. Initialize ALL views using the correct IDs from item_train_result.xml
                // NOTE: Using the new IDs provided in your XML
                textTrainNameClass = itemView.findViewById(R.id.text_train_name_class);
                textTrainPrice = itemView.findViewById(R.id.text_train_price);
                textDepartureTime = itemView.findViewById(R.id.text_departure_time);
                textDepartureStation = itemView.findViewById(R.id.text_departure_station);
                textDuration = itemView.findViewById(R.id.text_duration);
                textArrivalTime = itemView.findViewById(R.id.text_arrival_time);
                textArrivalStation = itemView.findViewById(R.id.text_arrival_station);
                btnSelectTrain = itemView.findViewById(R.id.btn_select_train);
            }

            public void bind(Train train, int count) {
                // 3. Bind data to all the new TextViews (This fixes the NPE at line 179)

                // Top row: Train Name, Class, and Price
                textTrainNameClass.setText(String.format("%s (%s)", train.getTrainName(), train.getTrainClass()));

                double finalPrice = train.getPrice() * count;
                String formattedPrice = formatCurrency(finalPrice);
                textTrainPrice.setText(formattedPrice);

                // Middle Section: Departure, Duration, Arrival
                textDepartureTime.setText(train.getDepartureTime());

                // FIX: Changed from getFromStation() to getOrigin() to resolve 'cannot find symbol'
                // NOTE: If your Train class uses a different getter (e.g., getDepartureStationCode()), use that instead.
                textDepartureStation.setText(train.getOrigin());

                textDuration.setText(train.getDuration());

                textArrivalTime.setText(train.getArrivalTime());

                // FIX: Changed from getToStation() to getDestination() to resolve 'cannot find symbol'
                // NOTE: If your Train class uses a different getter (e.g., getArrivalStationCode()), use that instead.
                textArrivalStation.setText(train.getDestination());

                btnSelectTrain.setOnClickListener(v -> {
                    // Navigate to next screen, passing selected train data (Train is Parcelable now)
                    Intent intent = new Intent(context, TrainPassengerActivity.class);
                    // Pass the Parcelable Train object
                    intent.putExtra("selected_train", train);
                    // ADDED: Pass the passenger count for calculation in PaymentActivity
                    intent.putExtra("passenger_count", count);
                    context.startActivity(intent);
                });
            }

            // Helper method to format currency (IDR)
            private String formatCurrency(double price) {
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                format.setMinimumFractionDigits(0);
                return format.format(price);
            }
        }
    }
}