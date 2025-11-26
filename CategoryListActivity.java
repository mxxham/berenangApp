package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity implements CategoryListAdapter.OnItemClickListener {

    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
    private String currentCategory;
    private RecyclerView recyclerView;
    private CategoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // Get category from intent
        currentCategory = getIntent().getStringExtra(EXTRA_CATEGORY);
        // Fallback if no extra is provided
        if (currentCategory == null) {
            currentCategory = "Attraction";
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Setup title
        TextView titleText = findViewById(R.id.title_text);

        // 🚀 MODIFIED: Set title based on whether it's "SHOW_ALL" or a specific category.
        if (currentCategory.equals(SearchActivity.CATEGORY_SHOW_ALL)) {
            titleText.setText("All Events & Attractions");
        } else {
            titleText.setText(currentCategory);
        }

        // Setup RecyclerView with GridLayout (2 columns)
        recyclerView = findViewById(R.id.category_items_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Get filtered items and setup adapter
        List<SearchActivity.SearchItemPlaceholder> items = getItemsForCategory(currentCategory);
        adapter = new CategoryListAdapter(this, items, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(SearchActivity.SearchItemPlaceholder item) {
        Toast.makeText(this, "Opening: " + item.title, Toast.LENGTH_SHORT).show();

        double itemPrice = getPriceForItem(item.title, item.category);

        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(EventActivity.EXTRA_TITLE, item.title);
        intent.putExtra(EventActivity.EXTRA_SUBTITLE, item.subtitle);
        intent.putExtra(EventActivity.EXTRA_IMAGE_URL, item.imageUrl);
        intent.putExtra(EventActivity.EXTRA_PRICE, itemPrice);
        startActivity(intent);
    }

    /**
     * 🚀 MODIFIED: Handles filtering. Returns all items if category is CATEGORY_SHOW_ALL.
     */
    private List<SearchActivity.SearchItemPlaceholder> getItemsForCategory(String category) {
        List<SearchActivity.SearchItemPlaceholder> allItems = getAllSearchItems();

        // Check for "Show All" flag
        if (category.equals(SearchActivity.CATEGORY_SHOW_ALL)) {
            return allItems; // Return the entire list
        }

        // Standard filtering by specific category
        List<SearchActivity.SearchItemPlaceholder> filtered = new ArrayList<>();
        for (SearchActivity.SearchItemPlaceholder item : allItems) {
            if (item.category.equals(category)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private List<SearchActivity.SearchItemPlaceholder> getAllSearchItems() {
        List<SearchActivity.SearchItemPlaceholder> items = new ArrayList<>();

        // Attraction
        items.add(new SearchActivity.SearchItemPlaceholder("Bird Paradise", "Location: Singapore Zoo Complex", "Attraction", "https://www.mandai.com/content/dam/mandai/bird-paradise/bird-paradise-crimson-wetlands-1200x630.png"));
        items.add(new SearchActivity.SearchItemPlaceholder("Dunia Fantasi (Dufan) Ancol", "Location: Ancol Dreamland, North Jakarta", "Attraction", "https://images.tokopedia.net/blog-tokopedia-com/uploads/2016/08/dunia-fantasi-impian-jaya-ancol-pintu-masuk.jpg"));
        items.add(new SearchActivity.SearchItemPlaceholder("Taman Safari Bogor", "Location: Cisarua, West Java", "Attraction", "https://tamansafari.com/taman-safari-bogor/wp-content/uploads/sites/8/2025/03/Taman-Safari-Bogor.jpg"));
        items.add(new SearchActivity.SearchItemPlaceholder("Prambanan Temple", "Location: Sleman Regency, Yogyakarta", "Attraction", "https://i0.wp.com/pamitrantours.com/wp-content/uploads/2018/04/Prambanan-Temple-Tour-2.jpg?resize=1000%2C750"));

        // Events
        items.add(new SearchActivity.SearchItemPlaceholder("Jakarta Music Fest", "Location: JIExpo Kemayoran, Central Jakarta", "Events", "https://sp-ao.shortpixel.ai/client/to_auto,q_lossy,ret_img,w_800,h_600/https://www.flokq.com/blog/wp-content/uploads/2020/06/people-having-a-concert-1190297-800x600.jpg"));
        items.add(new SearchActivity.SearchItemPlaceholder("Art Exhibition 2024", "Location: Museum MACAN, West Jakarta", "Events", "https://www.nowjakarta.co.id/wp-content/uploads/2022/07/e0708492e521671c4c5d0807e676f3f9.jpg"));

        // To Dos
        items.add(new SearchActivity.SearchItemPlaceholder("Hiking Bromo", "Location: Bromo Tengger Semeru National Park, East Java", "To Dos", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6VkA6J6hp0njfhj3g5NTQJXtLYcBIC20IYQ&s"));

        // Playground
        items.add(new SearchActivity.SearchItemPlaceholder("Kiddie Rides", "Location: Mall Taman Anggrek, West Jakarta", "Playground", "https://www.lakecompounce.com/content/dam/lkc/images/blogs/Rainbow-raiders-Lake-Compounce-main%20(1).jpg"));

        // Spa & Beauty
        items.add(new SearchActivity.SearchItemPlaceholder("Luxury Spa Bali", "Location: Ubud, Bali, Indonesia", "Spa & Beauty", "https://cdn.sanity.io/images/nxpteyfv/goguides/2a82f70c455e7d67f56fda2db8e3e9d15574769f-1600x1066.jpg"));

        return items;
    }

    private double getPriceForItem(String title, String category) {
        switch (title) {
            case "Bird Paradise":
                return 220000.0;
            case "Luxury Spa Bali":
                return 450000.0;
            case "Jakarta Music Fest":
                return 350000.0;
            case "Dunia Fantasi (Dufan) Ancol":
                return 295000.0;
            case "Taman Safari Bogor":
                return 250000.0;
            case "Prambanan Temple":
                return 175000.0;
            case "Art Exhibition 2024":
                return 120000.0;
            case "Hiking Bromo":
                return 500000.0;
            case "Kiddie Rides":
                return 65000.0;
            default:
                return getPriceForCategory(category);
        }
    }

    private double getPriceForCategory(String category) {
        switch (category.toLowerCase()) {
            case "events":
                return 150000.0;
            case "attraction":
                return 100000.0;
            case "spa & beauty":
                return 300000.0;
            case "to dos":
                return 100000.0;
            case "playground":
                return 75000.0;
            default:
                return 50000.0;
        }
    }
}