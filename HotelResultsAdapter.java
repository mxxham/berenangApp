package com.example.berenang10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import com.bumptech.glide.Glide;

public class HotelResultsAdapter extends RecyclerView.Adapter<HotelResultsAdapter.ViewHolder> {

    private List<Hotel> hotels;
    private OnHotelClickListener listener;
    private final NumberFormat currencyFormat; // Use final

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public HotelResultsAdapter(List<Hotel> hotels, OnHotelClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
        // Initializes formatter for Indonesian Rupiah (IDR)
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.bind(hotel, listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    /**
     * Updates the data list and notifies the RecyclerView to refresh.
     * @param newHotels The new list of filtered or loaded hotels.
     */
    public void updateHotels(List<Hotel> newHotels) {
        this.hotels = newHotels;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView hotelImage;
        TextView nameText, locationText, ratingText, reviewCountText;
        TextView roomTypeText, priceText, amenitiesText;
        View wifiIcon, breakfastIcon, poolIcon, parkingIcon;

        // Static array of image URLs to randomly choose from
        private static final String[] IMAGE_URLS = {
                "https://plus.unsplash.com/premium_photo-1675616563084-63d1f129623d?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1169",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1170",
                "https://images.unsplash.com/photo-1549638441-b787d2e11f14?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1170",
                "https://images.unsplash.com/photo-1559841644-08984562005a?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1074",
                "https://images.unsplash.com/photo-1587985064135-0366536eab42?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1170",
                "https://images.unsplash.com/photo-1631049421450-348ccd7f8949?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1170"
        };
        private static final Random random = new Random();

        ViewHolder(View itemView) {
            super(itemView);
            // View Initialization (IDs are assumed correct)
            cardView = itemView.findViewById(R.id.hotel_card);
            hotelImage = itemView.findViewById(R.id.hotel_image);
            nameText = itemView.findViewById(R.id.hotel_name);
            locationText = itemView.findViewById(R.id.hotel_location);
            ratingText = itemView.findViewById(R.id.rating_text);
            reviewCountText = itemView.findViewById(R.id.review_count_text);
            roomTypeText = itemView.findViewById(R.id.room_type_text);
            priceText = itemView.findViewById(R.id.price_text);
            amenitiesText = itemView.findViewById(R.id.amenities_text);
            wifiIcon = itemView.findViewById(R.id.wifi_icon);
            breakfastIcon = itemView.findViewById(R.id.breakfast_icon);
            poolIcon = itemView.findViewById(R.id.pool_icon);
            parkingIcon = itemView.findViewById(R.id.parking_icon);
        }

        void bind(Hotel hotel, OnHotelClickListener listener, NumberFormat currencyFormat) {
            // Data Binding
            nameText.setText(hotel.getName());
            locationText.setText(hotel.getAddress());
            ratingText.setText(String.format(Locale.getDefault(), "%.1f", hotel.getRating()));
            reviewCountText.setText(String.format("(%d reviews)", hotel.getReviewCount()));
            roomTypeText.setText(hotel.getRoomType());
            priceText.setText(currencyFormat.format(hotel.getPricePerNight()) + "/night");
            // amenitiesText.setText(hotel.getAmenities()); // Usually better to rely on icons

            // 1. Select a random image URL from the list
            int randomIndex = random.nextInt(IMAGE_URLS.length);
            String randomImageUrl = IMAGE_URLS[randomIndex];

            // 2. CRITICAL STEP: Store the chosen URL in the Hotel object.
            // This URL will be retrieved by the HotelDetailActivity.
            hotel.setImageUrl(randomImageUrl);

            // 3. Use Glide to load the image
            Glide.with(itemView.getContext())
                    .load(randomImageUrl)
                    .placeholder(android.R.color.darker_gray) // Placeholder for loading state
                    .into(hotelImage);

            // Show/hide amenity icons
            wifiIcon.setVisibility(hotel.hasWifi() ? View.VISIBLE : View.GONE);
            breakfastIcon.setVisibility(hotel.hasBreakfast() ? View.VISIBLE : View.GONE);
            poolIcon.setVisibility(hotel.hasPool() ? View.VISIBLE : View.GONE);
            parkingIcon.setVisibility(hotel.hasParking() ? View.VISIBLE : View.GONE);

            // Set the click listener for the whole card
            cardView.setOnClickListener(v -> listener.onHotelClick(hotel));
        }
    }
}