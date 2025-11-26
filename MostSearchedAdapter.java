package com.example.berenang10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView; // Import MaterialCardView (assuming CardView is actually MaterialCardView)
import java.util.List;

public class MostSearchedAdapter extends RecyclerView.Adapter<MostSearchedAdapter.MostSearchedViewHolder> {

    private List<MostSearchedItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MostSearchedItem item);
    }

    public MostSearchedAdapter(List<MostSearchedItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MostSearchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_most_searched_horizontal, parent, false);
        return new MostSearchedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MostSearchedViewHolder holder, int position) {
        MostSearchedItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MostSearchedViewHolder extends RecyclerView.ViewHolder {
        // Changed CardView type to MaterialCardView to match XML,
        // though CardView might also work depending on your setup.
        private MaterialCardView cardView;
        private ImageView itemImage;
        private TextView itemName;
        private TextView searchCount;

        public MostSearchedViewHolder(@NonNull View itemView) {
            super(itemView);

            // --- FIX APPLIED HERE: Using IDs from the corrected XML layout ---
            cardView = itemView.findViewById(R.id.most_searched_card);
            itemImage = itemView.findViewById(R.id.item_image);        // Changed from R.id.most_searched_image to R.id.item_image
            itemName = itemView.findViewById(R.id.most_searched_name);
            searchCount = itemView.findViewById(R.id.search_count);
            // ------------------------------------------------------------------
        }

        public void bind(MostSearchedItem item) {
            // NOTE: item.getImageResId() suggests you are loading local drawables (int ID).
            // If you intend to load images from the web (like in previous steps),
            // you should use an image loading library (Glide/Picasso) with a URL string
            // and update MostSearchedItem to return a URL string instead of an int ID.
            itemImage.setImageResource(item.getImageResId());

            itemName.setText(item.getName());
            searchCount.setText(item.getSearchCount());

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}