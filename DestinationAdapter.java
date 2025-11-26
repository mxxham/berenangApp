package com.example.berenang10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private final List<Destination> destinations;
    private final OnDestinationClickListener listener;

    public interface OnDestinationClickListener {
        void onDestinationClick(Destination destination);
    }

    public DestinationAdapter(List<Destination> destinations, OnDestinationClickListener listener) {
        this.destinations = destinations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destination destination = destinations.get(position);
        holder.bind(destination, listener);
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView destinationImage;
        TextView nameText;
        TextView countryText;
        TextView priceText;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.destination_card);
            destinationImage = itemView.findViewById(R.id.destination_image);
            nameText = itemView.findViewById(R.id.destination_name);
            countryText = itemView.findViewById(R.id.destination_country);
            priceText = itemView.findViewById(R.id.destination_price);
        }

        void bind(Destination destination, OnDestinationClickListener listener) {
            nameText.setText(destination.getName());
            countryText.setText(destination.getCountry());
            priceText.setText(destination.getPrice());

            // ⭐ CRITICAL GLIDE LOGIC RESTORED ⭐
            Glide.with(itemView.getContext())
                    .load(destination.getImageUrl())
                    .placeholder(R.color.image_placeholder)
                    .error(R.drawable.ic_back)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(destinationImage);

            cardView.setOnClickListener(v -> listener.onDestinationClick(destination));
        }
    }
}