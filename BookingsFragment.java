package com.example.berenang10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingDatabaseAdapter adapter;
    private TextView emptyStateText;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        recyclerView = view.findViewById(R.id.bookings_recycler);
        emptyStateText = view.findViewById(R.id.empty_state_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());

        loadBookings();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        SharedPreferences prefs = getActivity().getSharedPreferences("BerenangPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("current_user_email", "");

        if (userEmail.isEmpty()) {
            // User not logged in
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("Please login to view your bookings");
            return;
        }

        List<BookingData> bookings = databaseHelper.getAllBookings(userEmail);

        if (bookings.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No bookings yet\n\nStart exploring and book your first trip!");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);

            adapter = new BookingDatabaseAdapter(bookings, booking -> {
                Intent intent = new Intent(getContext(), BookingDatabaseDetailActivity.class);
                intent.putExtra("pnr", booking.getPnr());
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }
    }
}