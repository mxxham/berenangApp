package com.example.berenang10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingRecordAdapter extends RecyclerView.Adapter<BookingRecordAdapter.ViewHolder> {

    private List<BookingRecord> bookings;
    private OnBookingClickListener listener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public interface OnBookingClickListener {
        void onBookingClick(BookingRecord booking);
    }

    public BookingRecordAdapter(List<BookingRecord> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_record, parent, false); // Assuming this layout works for both
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRecord booking = bookings.get(position);
        holder.bind(booking, listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView pnrText, flightInfoText, routeText, dateText;
        TextView passengerText, statusText, amountText;
        TextView bookingTypeText; // NEW

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.booking_record_card);
            pnrText = itemView.findViewById(R.id.pnr_text);
            flightInfoText = itemView.findViewById(R.id.flight_info_text);
            routeText = itemView.findViewById(R.id.route_text);
            dateText = itemView.findViewById(R.id.date_text);
            passengerText = itemView.findViewById(R.id.passenger_text);
            statusText = itemView.findViewById(R.id.status_text);
            amountText = itemView.findViewById(R.id.amount_text);
            bookingTypeText = itemView.findViewById(R.id.booking_type_text); // Assumed new ID in XML
        }

        void bind(BookingRecord booking, OnBookingClickListener listener,
                  NumberFormat currencyFormat) {

            // 1. Common Fields
            statusText.setText(booking.getStatus());
            amountText.setText(currencyFormat.format(booking.getTotalAmount()));
            bookingTypeText.setText(booking.getBookingType() + " Booking");

            // 2. Type-Specific Fields
            if ("Flight".equals(booking.getBookingType())) {
                Flight flight = booking.getFlight();
                pnrText.setText("PNR: " + booking.getPnr());
                flightInfoText.setText(flight.getAirline() + " " + flight.getFlightNumber());
                routeText.setText(flight.getOrigin() + " → " + flight.getDestination());
                dateText.setText(booking.getSearchParams().getDepartureDate() +
                        " • " + flight.getDepartureTime());
                passengerText.setText(booking.getPassengerData().getFullName());
            } else if ("Hotel".equals(booking.getBookingType())) {
                Hotel hotel = booking.getHotel();
                HotelSearchParams search = booking.getHotelSearchParams();

                pnrText.setText("ID: " + booking.getBookingId());
                flightInfoText.setText(hotel.getName()); // Hotel Name
                routeText.setText(hotel.getRoomType()); // Room Type
                dateText.setText(search.getCheckInDate() + " - " + search.getCheckOutDate());
                passengerText.setText(booking.getGuestName() + " (" + search.getGuests() + " Guests)");
            }

            // 3. Status Color
            int statusColor;
            if (booking.getStatus().equals("Confirmed")) {
                statusColor = itemView.getContext().getColor(android.R.color.holo_green_dark);
            } else if (booking.getStatus().equals("Cancelled")) {
                statusColor = itemView.getContext().getColor(android.R.color.holo_red_dark);
            } else {
                statusColor = itemView.getContext().getColor(android.R.color.holo_orange_dark);
            }
            statusText.setTextColor(statusColor);

            cardView.setOnClickListener(v -> listener.onBookingClick(booking));
        }
    }
}