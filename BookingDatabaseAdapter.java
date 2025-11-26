package com.example.berenang10;

import android.content.Context; // Added missing import
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Added missing import for ContextCompat
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingDatabaseAdapter extends RecyclerView.Adapter<BookingDatabaseAdapter.ViewHolder> {

    private List<BookingData> bookings;
    private OnBookingClickListener listener;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public interface OnBookingClickListener {
        void onBookingClick(BookingData booking);
    }

    public BookingDatabaseAdapter(List<BookingData> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_database, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingData booking = bookings.get(position);
        holder.bind(booking, listener, currencyFormat, dateFormat);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView pnrText, typeText, titleText, detailsText;
        TextView dateText, statusText, amountText;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.booking_card);
            pnrText = itemView.findViewById(R.id.pnr_text);
            typeText = itemView.findViewById(R.id.type_text);
            titleText = itemView.findViewById(R.id.title_text);
            detailsText = itemView.findViewById(R.id.details_text);
            dateText = itemView.findViewById(R.id.date_text);
            statusText = itemView.findViewById(R.id.status_text);
            amountText = itemView.findViewById(R.id.amount_text);
        }

        void bind(BookingData booking, OnBookingClickListener listener,
                  NumberFormat currencyFormat, SimpleDateFormat dateFormat) {

            Context context = itemView.getContext(); // Get context for resource lookup
            String status = booking.getStatus();

            pnrText.setText("PNR: " + booking.getPnr());
            typeText.setText(booking.getType().toUpperCase());
            titleText.setText(booking.getTitle());

            // Show first line of details only
            String[] detailLines = booking.getDetails().split("\n");
            if (detailLines.length > 0) {
                detailsText.setText(detailLines[0]);
            }

            Date bookingDate = new Date(booking.getDate());
            dateText.setText(dateFormat.format(bookingDate));

            statusText.setText(status);
            amountText.setText(currencyFormat.format(booking.getAmount()));

            // --- Corrected Status Styling Logic ---
            if ("Cancelled".equalsIgnoreCase(status)) {
                // Apply RED text color AND CANCELLED background drawable
                statusText.setTextColor(ContextCompat.getColor(context, R.color.error));
                statusText.setBackgroundResource(R.drawable.status_bg_cancelled);
                cardView.setAlpha(0.7f);
            } else if ("Confirmed".equalsIgnoreCase(status)) {
                // Apply GREEN text color AND CONFIRMED background drawable
                statusText.setTextColor(ContextCompat.getColor(context, R.color.success));
                statusText.setBackgroundResource(R.drawable.status_bg);
                cardView.setAlpha(1.0f);
            } else if ("Pending".equalsIgnoreCase(status)) {
                // Apply ORANGE text color AND PENDING background drawable
                statusText.setTextColor(ContextCompat.getColor(context, R.color.warning));
                statusText.setBackgroundResource(R.drawable.status_bg_pending);
                cardView.setAlpha(1.0f);
            } else {
                // Default style
                statusText.setTextColor(ContextCompat.getColor(context, R.color.info));
                statusText.setBackgroundResource(R.drawable.status_bg_pending);
                cardView.setAlpha(1.0f);
            }

            cardView.setOnClickListener(v -> listener.onBookingClick(booking));
        }
    }
}