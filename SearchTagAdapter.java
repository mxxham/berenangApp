package com.example.berenang10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Import ContextCompat
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class SearchTagAdapter extends RecyclerView.Adapter<SearchTagAdapter.TagViewHolder> {

    private final List<String> tags;
    private final OnTagClickListener listener;
    private int selectedPosition = 0; // Default to the first tag ("Attraction")

    // Interface for click handling
    public interface OnTagClickListener {
        void onTagClick(String category);
    }

    public SearchTagAdapter(List<String> tags, OnTagClickListener listener) {
        this.tags = tags;
        this.listener = listener;
        // Trigger the initial click for the default selection
        if (!tags.isEmpty()) {
            listener.onTagClick(tags.get(0));
        }
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.tagText.setText(tag);

        // Handle selection state visually
        boolean isSelected = (position == selectedPosition);

        // Get Context once
        final View itemView = holder.itemView;

        // --- UPDATED: Use ContextCompat.getColor() for modern API compatibility ---
        int selectedBgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary);
        int unselectedBgColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white); // Use android.R.color.white for clarity
        int selectedTextColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
        int unselectedTextColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_text);

        // Set colors
        holder.cardView.setCardBackgroundColor(isSelected ? selectedBgColor : unselectedBgColor);
        holder.tagText.setTextColor(isSelected ? selectedTextColor : unselectedTextColor);
        // -------------------------------------------------------------------------

        holder.itemView.setOnClickListener(v -> {
            // Check if the same item is clicked to avoid unnecessary updates
            if (position == selectedPosition) {
                return;
            }

            // Store the previous selected position before updating
            int previousSelectedPosition = selectedPosition;
            selectedPosition = position;

            // Notify the previous and current item to redraw
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);

            // Notify the Activity/Fragment
            listener.onTagClick(tag);
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tagText;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tagText = itemView.findViewById(R.id.tag_text);
        }
    }
}