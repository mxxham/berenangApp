package com.example.berenang10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Booking> bookings;

    public BookingAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the list item layout file
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView pnrText;
        TextView titleText;
        TextView typeText;
        TextView dateText;
        TextView statusText;

        ViewHolder(View itemView) {
            super(itemView);
            // Map views using the IDs from list_item_booking.xml
            cardView = itemView.findViewById(R.id.booking_card);
            pnrText = itemView.findViewById(R.id.pnr_text);
            titleText = itemView.findViewById(R.id.title_text);
            typeText = itemView.findViewById(R.id.type_text);
            dateText = itemView.findViewById(R.id.date_text);
            statusText = itemView.findViewById(R.id.status_text);
        }

        void bind(Booking booking) {
            Context context = itemView.getContext();
            String status = booking.getStatus();

            // Populate Text Data
            pnrText.setText("PNR: " + booking.getPnr());
            titleText.setText(booking.getTitle());
            typeText.setText(booking.getType());
            dateText.setText(booking.getDate());
            statusText.setText(status);

            // --- Dynamic Status Styling Logic ---
            if ("Cancelled".equalsIgnoreCase(status)) {
                // Apply RED color and CANCELLED background
                statusText.setTextColor(ContextCompat.getColor(context, R.color.error));
                statusText.setBackgroundResource(R.drawable.status_bg_cancelled);
                cardView.setAlpha(0.7f); // Fade the card for cancelled items
            } else if ("Confirmed".equalsIgnoreCase(status)) {
                // Apply GREEN color and CONFIRMED background
                statusText.setTextColor(ContextCompat.getColor(context, R.color.success));
                statusText.setBackgroundResource(R.drawable.status_bg_confirmed); // Changed to use confirmed drawable
                cardView.setAlpha(1.0f);
            } else if ("Pending".equalsIgnoreCase(status)) {
                // Apply ORANGE/WARNING color and PENDING background
                statusText.setTextColor(ContextCompat.getColor(context, R.color.warning));
                statusText.setBackgroundResource(R.drawable.status_bg_pending);
                cardView.setAlpha(1.0f);
            } else {
                // Default style for unknown statuses
                statusText.setTextColor(ContextCompat.getColor(context, R.color.info));
                statusText.setBackgroundResource(R.drawable.status_bg_pending);
                cardView.setAlpha(1.0f);
            }
        }
    }
}