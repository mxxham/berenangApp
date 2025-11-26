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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private Context context;
    private List<SearchActivity.SearchItemPlaceholder> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchActivity.SearchItemPlaceholder item);
    }

    public CategoryListAdapter(Context context, List<SearchActivity.SearchItemPlaceholder> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchActivity.SearchItemPlaceholder item = items.get(position);

        holder.titleText.setText(item.title);
        holder.locationText.setText(item.subtitle);

        // Load image with Glide
        Glide.with(context)
                .load(item.imageUrl)
                .apply(new RequestOptions()
                        .transform(new RoundedCorners(24))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView locationText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            titleText = itemView.findViewById(R.id.item_title);
            locationText = itemView.findViewById(R.id.item_location);
        }
    }
}