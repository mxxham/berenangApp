package com.example.berenang10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FlightResultsAdapter extends RecyclerView.Adapter<FlightResultsAdapter.ViewHolder> {

    private List<Flight> flights;
    private OnFlightClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnFlightClickListener {
        void onFlightClick(Flight flight);
    }

    public FlightResultsAdapter(List<Flight> flights, OnFlightClickListener listener) {
        this.flights = flights;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.bind(flight, listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public void updateFlights(List<Flight> newFlights) {
        this.flights = newFlights;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView airlineText, flightNumberText, routeText;
        TextView departureTimeText, arrivalTimeText, durationText;
        TextView priceText, seatsText, flightTypeText;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.flight_card);
            airlineText = itemView.findViewById(R.id.airline_text);
            flightNumberText = itemView.findViewById(R.id.flight_number_text);
            routeText = itemView.findViewById(R.id.route_text);
            departureTimeText = itemView.findViewById(R.id.departure_time_text);
            arrivalTimeText = itemView.findViewById(R.id.arrival_time_text);
            durationText = itemView.findViewById(R.id.duration_text);
            priceText = itemView.findViewById(R.id.price_text);
            seatsText = itemView.findViewById(R.id.seats_text);
            flightTypeText = itemView.findViewById(R.id.flight_type_text);
        }

        void bind(Flight flight, OnFlightClickListener listener, NumberFormat currencyFormat) {
            airlineText.setText(flight.getAirline());
            flightNumberText.setText(flight.getFlightNumber());
            routeText.setText(flight.getOrigin() + " → " + flight.getDestination());
            departureTimeText.setText(flight.getDepartureTime());
            arrivalTimeText.setText(flight.getArrivalTime());
            durationText.setText(flight.getDuration());
            priceText.setText(currencyFormat.format(flight.getPrice()));
            seatsText.setText(flight.getAvailableSeats() + " seats left");

            if (flight.isDirect()) {
                flightTypeText.setText("Direct Flight");
                flightTypeText.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                flightTypeText.setText(flight.getLayoverInfo());
                flightTypeText.setTextColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
            }

            cardView.setOnClickListener(v -> listener.onFlightClick(flight));
        }
    }
}