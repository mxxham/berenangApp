package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity implements SearchItemHorizontalAdapter.OnItemClickListener {

    public static final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";
    // 💡 NEW CONSTANT: Flag to signal CategoryListActivity to display ALL items.
    public static final String CATEGORY_SHOW_ALL = "ALL_CATEGORIES";

    private List<SearchItemPlaceholder> allSearchItems;
    private SearchItemHorizontalAdapter itemAdapter;
    private RecyclerView mostSearchedItemsRecycler;
    private SearchTagAdapter tagAdapter;

    // Define the categories that should redirect to EventActivity
    private static final List<String> EVENT_REDIRECT_CATEGORIES = Arrays.asList(
            "Events",
            "Attraction",
            "To Dos",
            "Playground",
            "Spa & Beauty"
    );

    // A field to track the currently selected category for the "See All" button
    private String currentSelectedCategory = "Attraction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ... (onCreate content remains the same, error fix applied) ...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Handle incoming search query (The typo fix is included here)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_SEARCH_QUERY)) {
            String initialQuery = intent.getStringExtra(EXTRA_SEARCH_QUERY);
            TextView searchInputText = findViewById(R.id.search_input_text);

            if (searchInputText != null && initialQuery != null && !initialQuery.isEmpty()) {
                searchInputText.setText(initialQuery);
                searchInputText.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
            }
        }

        // --- Setup Data and Adapters ---

        // 1. Get ALL data items with categories
        allSearchItems = getAllSearchItems();

        // 2. Setup Most Searched Items RecyclerView
        mostSearchedItemsRecycler = findViewById(R.id.most_searched_items_recycler);
        mostSearchedItemsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initially show only 'Attraction' items (default selection)
        List<SearchItemPlaceholder> initialList = filterItemsByCategory("Attraction");

        itemAdapter = new SearchItemHorizontalAdapter(this, initialList, this);
        mostSearchedItemsRecycler.setAdapter(itemAdapter);

        // 3. Setup Search Tags RecyclerView
        RecyclerView searchTagsRecycler = findViewById(R.id.search_tags_recycler);
        searchTagsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> tags = new ArrayList<>();
        tags.add("Attraction");
        tags.add("Events");
        tags.add("To Dos");
        tags.add("Playground");
        tags.add("Spa & Beauty");

        // Set up the adapter with a click listener
        tagAdapter = new SearchTagAdapter(tags, this::onTagSelected);
        searchTagsRecycler.setAdapter(tagAdapter);

        // Handle See All Button Click
        // 🚀 MODIFIED LOGIC: Pass the CATEGORY_SHOW_ALL constant to display all items.
        findViewById(R.id.see_all_button).setOnClickListener(v -> {
            // Launch CategoryListActivity and tell it to show everything
            Intent categoryIntent = new Intent(SearchActivity.this, CategoryListActivity.class);
            categoryIntent.putExtra(CategoryListActivity.EXTRA_CATEGORY, CATEGORY_SHOW_ALL);
            startActivity(categoryIntent);
        });
    }

    /**
     * Handles the filtering and display logic when a search tag is clicked.
     */
    private void onTagSelected(String category) {
        // Update the tracked category (This is still useful for the horizontal list display)
        this.currentSelectedCategory = category;

        // Filter the full list based on the selected category
        List<SearchItemPlaceholder> filteredList = filterItemsByCategory(category);

        // Update the adapter's data and notify the RecyclerView
        itemAdapter.updateData(filteredList);
    }

    @Override
    public void onItemClick(SearchItemPlaceholder item) {

        // All categories are redirected to EventActivity based on EVENT_REDIRECT_CATEGORIES.
        if (EVENT_REDIRECT_CATEGORIES.contains(item.category)) {
            // Launch EventActivity
            Toast.makeText(this, "Opening activity details for: " + item.title + " (Type: " + item.category + ")", Toast.LENGTH_SHORT).show();

            // 1. Calculate the specific price for the item
            double itemPrice = getPriceForItem(item.title, item.category);

            Intent intent = new Intent(this, EventActivity.class);

            // Pass data using EventActivity's constants
            intent.putExtra(EventActivity.EXTRA_TITLE, item.title);
            intent.putExtra(EventActivity.EXTRA_SUBTITLE, item.subtitle); // Subtitle/Location is passed here
            intent.putExtra(EventActivity.EXTRA_IMAGE_URL, item.imageUrl);

            // 2. CRITICAL: Pass the calculated price
            intent.putExtra(EventActivity.EXTRA_PRICE, itemPrice);

            startActivity(intent);

        } else {
            // Fallback (currently no data uses this)
            Toast.makeText(this, "Opening general details for: " + item.title + " (Type: " + item.category + ")", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("ITEM_TITLE", item.title);
            intent.putExtra("ITEM_SUBTITLE", item.subtitle);
            intent.putExtra("ITEM_URL", item.imageUrl);
            startActivity(intent);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 💰 PRICE LOGIC METHODS (Unchanged)
    // ---------------------------------------------------------------------------------------------

    /**
     * Maps item title or category to a specific price.
     */
    private double getPriceForItem(String title, String category) {
        // 1. Price based on specific item title (Highest priority)
        switch (title) {
            case "Bird Paradise":
                return 220000.0; // Specific entry price (IDR)
            case "Luxury Spa Bali":
                return 450000.0; // High-end experience price (IDR)
            case "Jakarta Music Fest":
                return 350000.0; // Premium event price (IDR)
            case "Dunia Fantasi (Dufan) Ancol":
                return 295000.0; // Day pass price (IDR)
            case "Taman Safari Bogor":
                return 250000.0; // Entry price (IDR)
            case "Prambanan Temple":
                return 175000.0; // Entry ticket price (IDR)
            case "Art Exhibition 2024":
                return 120000.0; // Exhibition ticket price (IDR)
            case "Hiking Bromo":
                return 500000.0; // Tour package price (IDR)
            case "Kiddie Rides":
                return 65000.0; // Specific ride ticket (IDR)
            default:
                // 2. Fallback Price based on category (Lower priority)
                return getPriceForCategory(category);
        }
    }

    /**
     * Provides default pricing based on the category type.
     */
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
                return 50000.0; // Generic fallback
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Utility method to filter the main list.
     */
    private List<SearchItemPlaceholder> filterItemsByCategory(String category) {
        // Requires Java 8+ features (API 24+) for streams.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return allSearchItems.stream()
                    .filter(item -> item.category.equals(category))
                    .collect(Collectors.toList());
        } else {
            // Fallback for older Android versions
            List<SearchItemPlaceholder> filteredList = new ArrayList<>();
            for (SearchItemPlaceholder item : allSearchItems) {
                if (item.category.equals(category)) {
                    filteredList.add(item);
                }
            }
            return filteredList;
        }
    }

    /**
     * Builds the complete, static list of items with clear, consistent locations in the subtitle.
     */
    private List<SearchItemPlaceholder> getAllSearchItems() {
        List<SearchItemPlaceholder> items = new ArrayList<>();

        // Attraction
        // 🛑 UPDATED URL: Bird Paradise
        items.add(new SearchItemPlaceholder("Bird Paradise", "Location: Singapore Zoo Complex", "Attraction", "https://www.mandai.com/content/dam/mandai/bird-paradise/bird-paradise-crimson-wetlands-1200x630.png"));

        // UPDATED URL: Dunia Fantasi (Dufan) Ancol
        items.add(new SearchItemPlaceholder("Dunia Fantasi (Dufan) Ancol", "Location: Ancol Dreamland, North Jakarta", "Attraction", "https://images.tokopedia.net/blog-tokopedia-com/uploads/2016/08/dunia-fantasi-impian-jaya-ancol-pintu-masuk.jpg"));

        // UPDATED URL: Taman Safari Bogor
        items.add(new SearchItemPlaceholder("Taman Safari Bogor", "Location: Cisarua, West Java", "Attraction", "https://tamansafari.com/taman-safari-bogor/wp-content/uploads/sites/8/2025/03/Taman-Safari-Bogor.jpg"));

        // UPDATED URL: Prambanan Temple
        items.add(new SearchItemPlaceholder("Prambanan Temple", "Location: Sleman Regency, Yogyakarta", "Attraction", "https://i0.wp.com/pamitrantours.com/wp-content/uploads/2018/04/Prambanan-Temple-Tour-2.jpg?resize=1000%2C750"));

        // Events
        items.add(new SearchItemPlaceholder("Jakarta Music Fest", "Location: JIExpo Kemayoran, Central Jakarta", "Events", "https://assets.telkomsel.com/public/2025-02/Rayakan-20-Tahun-Java-Jazz-Kembali-Hadir-Lagi-di-Jakarta.jpg?VersionId=7ITMsvqzrJVGjXJhxPJnKm_tPGBCCVxs"));
        items.add(new SearchItemPlaceholder("Art Exhibition 2024", "Location: Museum MACAN, West Jakarta", "Events", "https://www.nowjakarta.co.id/wp-content/uploads/2022/07/e0708492e521671c4c5d0807e676f3f9.jpg"));

        // To Dos
        items.add(new SearchItemPlaceholder("Hiking Bromo", "Location: Bromo Tengger Semeru National Park, East Java", "To Dos", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6VkA6J6hp0njfhj3g5NTQJXtLYcBIC20IYQ&s"));

        // Playground
        items.add(new SearchItemPlaceholder("Kiddie Rides", "Location: Mall Taman Anggrek, West Jakarta", "Playground", "https://www.lakecompounce.com/content/dam/lkc/images/blogs/Rainbow-raiders-Lake-Compounce-main%20(1).jpg"));

        // Spa & Beauty
        items.add(new SearchItemPlaceholder("Luxury Spa Bali", "Location: Ubud, Bali, Indonesia", "Spa & Beauty", "https://cdn.sanity.io/images/nxpteyfv/goguides/2a82f70c455e7d67f56fda2db8e3e9d15574769f-1600x1066.jpg"));

        return items;
    }

    public static class SearchItemPlaceholder {
        String title;
        String subtitle;
        String category;
        String imageUrl;

        public SearchItemPlaceholder(String title, String subtitle, String category, String imageUrl) {
            this.title = title;
            this.subtitle = subtitle;
            this.category = category;
            this.imageUrl = imageUrl;
        }
    }
}