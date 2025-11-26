package com.example.berenang10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SearchItemHorizontalAdapter extends RecyclerView.Adapter<SearchItemHorizontalAdapter.ItemViewHolder> {

    // Interface to handle item clicks, to be implemented by SearchActivity
    public interface OnItemClickListener {
        void onItemClick(SearchActivity.SearchItemPlaceholder item);
    }

    private List<SearchActivity.SearchItemPlaceholder> items;
    private final Context context;
    private final OnItemClickListener clickListener; // New field for the click listener

    public SearchItemHorizontalAdapter(Context context, List<SearchActivity.SearchItemPlaceholder> items, OnItemClickListener clickListener) {
        this.context = context; // Initialize context
        this.items = items;
        this.clickListener = clickListener; // Initialize the click listener
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_most_searched_horizontal, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final SearchActivity.SearchItemPlaceholder item = items.get(position); // Use 'final' if targeting older APIs

        holder.itemTitle.setText(item.title);
        holder.itemSubtitle.setText(item.subtitle);

        // --- NEW: Handle Item Click Listener ---
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item); // Pass the clicked item back to the activity
            }
        });
        // ----------------------------------------

        // --- UPDATED: IMAGE LOADING LOGIC USING GLIDE ---
        Glide.with(context)
                .load(item.imageUrl)
                .centerCrop()
                .into(holder.itemImage);
        // ------------------------------------------------
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * NEW METHOD: Updates the data list and refreshes the RecyclerView.
     */
    public void updateData(List<SearchActivity.SearchItemPlaceholder> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemSubtitle;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Using the correct IDs from item_most_searched_horizontal.xml
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.most_searched_name);
            itemSubtitle = itemView.findViewById(R.id.search_count);
        }
    }
}