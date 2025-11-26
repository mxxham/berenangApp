package com.example.berenang10;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;
import android.view.WindowManager;

public class HomeFragment extends Fragment implements
        SearchItemHorizontalAdapter.OnItemClickListener,
        DestinationAdapter.OnDestinationClickListener {

    private RecyclerView destinationsRecycler;
    private DestinationAdapter destinationAdapter;
    private RecyclerView thingsToDoRecycler;
    private SearchItemHorizontalAdapter thingsToDoAdapter;
    private TextView placeholderText;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<String> searchPrompts = Arrays.asList(
            "Events in Jakarta",
            "Hotel in Bali",
            "Vacation on Yogyakarta"
    );
    private int currentPromptIndex = 0;
    private boolean isTyping = true;
    private int charIndex = 0;

    public static final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";
    public static final String EXTRA_SEARCH_PARAMS = "search_params";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getActivity() != null) {
            getActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        MaterialCardView hotelCard = view.findViewById(R.id.hotel_card);
        MaterialCardView flightCard = view.findViewById(R.id.flight_card);
        MaterialCardView tourCard = view.findViewById(R.id.tour_card);
        MaterialCardView trainCard = view.findViewById(R.id.train_card);
        MaterialCardView searchCard = view.findViewById(R.id.search_card);

        destinationsRecycler = view.findViewById(R.id.destinations_recycler);
        placeholderText = view.findViewById(R.id.placeholder_text);

        // Setup RecyclerView for popular destinations
        destinationsRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        List<Destination> destinations = getPopularDestinations();

        destinationAdapter = new DestinationAdapter(destinations, this::onDestinationClick);
        destinationsRecycler.setAdapter(destinationAdapter);

        // Setup Things to Do Section
        thingsToDoRecycler = view.findViewById(R.id.things_to_do_recycler);
        thingsToDoRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        List<SearchActivity.SearchItemPlaceholder> allItems = getAllSearchItems();

        // Filter the list to only include 'Attraction' items (matching SearchActivity default)
        List<SearchActivity.SearchItemPlaceholder> thingsToDoList = filterItemsByCategory(allItems, "Attraction");

        thingsToDoAdapter = new SearchItemHorizontalAdapter(getContext(), thingsToDoList, this);
        thingsToDoRecycler.setAdapter(thingsToDoAdapter);

        // Setup Featured Event Card
        SearchActivity.SearchItemPlaceholder featuredEvent = findFeaturedEvent("Jakarta Music Fest");
        if (featuredEvent != null) {
            setupFeaturedEventCard(view, featuredEvent);
        }

        // Setup card clicks
        hotelCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HotelSearchActivity.class);
            startActivity(intent);
        });

        flightCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FlightSearchActivity.class);
            startActivity(intent);
        });

        tourCard.setOnClickListener(v ->
                Toast.makeText(getContext(), "Tours coming soon!", Toast.LENGTH_SHORT).show());

        if (trainCard != null) {
            trainCard.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), TrainSearchActivity.class);
                startActivity(intent);
            });
        }

        if (searchCard != null) {
            searchCard.setOnClickListener(v -> {
                handler.removeCallbacks(typingRunnable);

                // Pass the full intended text from the searchPrompts list based on the current index.
                String fullSearchQuery = searchPrompts.get(currentPromptIndex);

                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                searchIntent.putExtra(EXTRA_SEARCH_QUERY, fullSearchQuery);
                startActivity(searchIntent);
            });
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(typingRunnable);
        placeholderText = null;

        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --- Interface Implementations ---
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(SearchActivity.SearchItemPlaceholder item) {
        if (getContext() == null) return;

        // Create schedules based on the item type
        ArrayList<EventSchedule> schedules = createSchedulesForItem(item);

        Intent intent = new Intent(getContext(), EventActivity.class);
        intent.putExtra(EventActivity.EXTRA_TITLE, item.title);
        intent.putExtra(EventActivity.EXTRA_SUBTITLE, item.subtitle);
        intent.putExtra(EventActivity.EXTRA_IMAGE_URL, item.imageUrl);

        // Create appropriate description based on category
        String description = getDescriptionForCategory(item.category, item.title);
        intent.putExtra(EventActivity.EXTRA_DESCRIPTION, description);

        // Pass price based on category
        double price = getPriceForCategory(item.category);
        intent.putExtra(EventActivity.EXTRA_PRICE, price);

        // Pass the schedules
        intent.putExtra(EventActivity.EXTRA_SCHEDULES, schedules);

        startActivity(intent);
    }

    @Override
    public void onDestinationClick(Destination destination) {
        if (getContext() == null) return;

        // A. Generate default check-in/out dates
        Calendar cal = Calendar.getInstance();
        // NOTE: Using "dd MMM yyyy" format consistent with HotelSearchActivity
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        String checkInDate = dateFormat.format(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, 3); // Check out 3 days later
        String checkOutDate = dateFormat.format(cal.getTime());

        // B. Create HotelSearchParams
        HotelSearchParams searchParams = new HotelSearchParams(
                destination.getName(),
                checkInDate,
                checkOutDate,
                2, // Default 2 guests
                1  // Default 1 room
        );

        // C. Start the HotelResultsActivity
        Intent intent = new Intent(getContext(), HotelResultsActivity.class);
        intent.putExtra(EXTRA_SEARCH_PARAMS, searchParams);
        startActivity(intent);
    }
    // ---------------------------------------------------------------------------------------------
    // --- END Interface Implementations ---
    // ---------------------------------------------------------------------------------------------


    // ---------------------------------------------------------------------------------------------
    // --- Data Fetching and Filtering Methods from SearchActivity ---
    // ---------------------------------------------------------------------------------------------

    /**
     * Builds the complete, static list of items (copied from SearchActivity).
     */
    private List<SearchActivity.SearchItemPlaceholder> getAllSearchItems() {
        List<SearchActivity.SearchItemPlaceholder> items = new ArrayList<>();

        // Attraction
        items.add(new SearchActivity.SearchItemPlaceholder("Bird Paradise", "Location: Singapore Zoo Complex", "Attraction", "https://www.mandai.com/content/dam/mandai/bird-paradise/bird-paradise-crimson-wetlands-1200x630.png"));
        items.add(new SearchActivity.SearchItemPlaceholder("Dunia Fantasi (Dufan) Ancol", "Location: Ancol Dreamland, North Jakarta", "Attraction", "https://images.tokopedia.net/blog-tokopedia-com/uploads/2016/08/dunia-fantasi-impian-jaya-ancol-pintu-masuk.jpg"));
        items.add(new SearchActivity.SearchItemPlaceholder("Taman Safari Bogor", "Location: Cisarua, West Java", "Attraction", "https://tamansafari.com/taman-safari-bogor/wp-content/uploads/sites/8/2025/03/Taman-Safari-Bogor.jpg"));
        items.add(new SearchActivity.SearchItemPlaceholder("Prambanan Temple", "Location: Sleman Regency, Yogyakarta", "Attraction", "https://i0.wp.com/pamitrantours.com/wp-content/uploads/2018/04/Prambanan-Temple-Tour-2.jpg?resize=1000%2C750"));

        // Events
        items.add(new SearchActivity.SearchItemPlaceholder("Jakarta Music Fest", "Location: JIExpo Kemayoran, Central Jakarta", "Events", "https://assets.telkomsel.com/public/2025-02/Rayakan-20-Tahun-Java-Jazz-Kembali-Hadir-Lagi-di-Jakarta.jpg?VersionId=7ITMsvqzrJVGjXJhxPJnKm_tPGBCCVxs"));
        items.add(new SearchActivity.SearchItemPlaceholder("Art Exhibition 2024", "Location: Museum MACAN, West Jakarta", "Events", "https://www.nowjakarta.co.id/wp-content/uploads/2022/07/e0708492e521671c4c5d0807e676f3f9.jpg"));

        // To Dos
        items.add(new SearchActivity.SearchItemPlaceholder("Hiking Bromo", "Location: Bromo Tengger Semeru National Park, East Java", "To Dos", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6VkA6J6hp0njfhj3g5NTQJXtLYcBIC20IYQ&s"));

        // Playground
        items.add(new SearchActivity.SearchItemPlaceholder("Kiddie Rides", "Location: Mall Taman Anggrek, West Jakarta", "Playground", "https://www.lakecompounce.com/content/dam/lkc/images/blogs/Rainbow-raiders-Lake-Compounce-main%20(1).jpg"));

        // Spa & Beauty
        items.add(new SearchActivity.SearchItemPlaceholder("Luxury Spa Bali", "Location: Ubud, Bali, Indonesia", "Spa & Beauty", "https://cdn.sanity.io/images/nxpteyfv/goguides/2a82f70c455e7d67f56fda2db8e3e9d15574769f-1600x1066.jpg"));

        return items;
    }

    /**
     * Utility method to filter the main list by category (copied from SearchActivity).
     */
    private List<SearchActivity.SearchItemPlaceholder> filterItemsByCategory(
            List<SearchActivity.SearchItemPlaceholder> allItems, String category) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return allItems.stream()
                    .filter(item -> item.category.equals(category))
                    .collect(Collectors.toList());
        } else {
            List<SearchActivity.SearchItemPlaceholder> filteredList = new ArrayList<>();
            for (SearchActivity.SearchItemPlaceholder item : allItems) {
                if (item.category.equals(category)) {
                    filteredList.add(item);
                }
            }
            return filteredList;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --- END: Data Fetching and Filtering Methods ---
    // ---------------------------------------------------------------------------------------------

    private ArrayList<EventSchedule> createSchedulesForItem(SearchActivity.SearchItemPlaceholder item) {
        ArrayList<EventSchedule> schedules = new ArrayList<>();

        // Create different schedules based on the category
        switch (item.category.toLowerCase()) {
            case "attraction":
            case "to dos":
            case "playground":
            case "spa & beauty":
                schedules.add(new EventSchedule("Daily: 9:00 AM - 5:00 PM", "Main Entrance"));
                schedules.add(new EventSchedule("Weekend: 9:00 AM - 7:00 PM", "Main Entrance"));
                break;

            case "tours":
                schedules.add(new EventSchedule("Daily at 9:00 AM", "Meet at Hotel Lobby"));
                schedules.add(new EventSchedule("Daily at 2:00 PM", "Meet at Hotel Lobby"));
                schedules.add(new EventSchedule("Daily at 6:00 PM", "Meet at Hotel Lobby"));
                break;

            case "activities":
                schedules.add(new EventSchedule("Monday & Wednesday, 10:00 AM", "Central Kitchen Studio"));
                schedules.add(new EventSchedule("Friday, 3:00 PM", "Central Kitchen Studio"));
                schedules.add(new EventSchedule("Saturday, 11:00 AM", "Beachside Cooking School"));
                break;

            case "experience":
                schedules.add(new EventSchedule("Daily at Sunrise (5:30 AM)", "Ubud Launch Site"));
                schedules.add(new EventSchedule("Daily at 6:00 AM", "Nusa Dua Marina"));
                break;

            case "events":
                schedules.add(new EventSchedule("Friday, Nov 15, 2025 - 7:00 PM", "Jakarta International Expo"));
                schedules.add(new EventSchedule("Saturday, Nov 16, 2025 - 7:00 PM", "Jakarta International Expo"));
                schedules.add(new EventSchedule("Sunday, Nov 17, 2025 - 6:00 PM", "Jakarta International Expo"));
                break;

            default:
                // Generic schedule
                schedules.add(new EventSchedule("Available Daily", "Check with provider"));
                break;
        }

        return schedules;
    }

    private String getDescriptionForCategory(String category, String title) {
        switch (category.toLowerCase()) {
            case "attraction":
                return "Explore the wonders of " + title + "! This world-class attraction offers " +
                        "unique experiences for all ages. Purchase your tickets in advance for exclusive discounts " +
                        "and skip the line access.";

            case "tours":
                return "Discover the best of " + title + " with our expert local guides. " +
                        "This comprehensive tour includes transportation, entrance fees, and a delicious lunch. " +
                        "Perfect for families and solo travelers alike!";

            case "activities":
                return "Learn the art of cooking authentic dishes in this hands-on " + title + " experience. " +
                        "All ingredients and equipment provided. Small group sizes ensure personalized attention. " +
                        "Take home recipes and new skills!";

            case "experience":
                return "An unforgettable " + title + " adventure awaits! " +
                        "Professional guides ensure your safety while you create memories that last a lifetime. " +
                        "Photography service available. Hotel pickup included.";

            case "events":
                return "Don't miss " + title + "! Featuring world-class performances, food vendors, " +
                        "and an incredible atmosphere. VIP packages available with exclusive lounge access, " +
                        "premium seating, and meet-and-greet opportunities.";

            default:
                return "This highly rated " + category + " is available for instant booking! " +
                        "Check availability and prices now. Perfect for all ages and experience levels.";
        }
    }

    private double getPriceForCategory(String category) {
        // NOTE: This is a placeholder price logic and does not fully replicate SearchActivity's complex pricing.
        switch (category.toLowerCase()) {
            case "attraction":
                return 100000.0; // Default for attractions
            case "tours":
                return 75000.0;  // Rp 75,000
            case "activities":
                return 50000.0;  // Rp 50,000
            case "experience":
                return 150000.0; // Rp 150,000
            case "events":
                return 250000.0; // Rp 250,000
            default:
                return 50000.0;
        }
    }

    private void setupFeaturedEventCard(View parentView, final SearchActivity.SearchItemPlaceholder item) {
        TextView eventTitle = parentView.findViewById(R.id.featured_event_title);
        TextView eventSubtitle = parentView.findViewById(R.id.featured_event_subtitle);
        ImageView eventImage = parentView.findViewById(R.id.featured_event_image);
        View eventCard = parentView.findViewById(R.id.featured_event_card);

        if (eventTitle != null) eventTitle.setText(item.title);
        if (eventSubtitle != null) eventSubtitle.setText(item.subtitle);

        if (eventImage != null && getContext() != null && item.imageUrl != null) {
            Glide.with(getContext()).load(item.imageUrl).centerCrop().into(eventImage);
        }

        if (eventCard != null) {
            eventCard.setOnClickListener(v -> {
                if (getContext() == null) return;

                // Create schedules for featured event
                ArrayList<EventSchedule> schedules = new ArrayList<>();
                schedules.add(new EventSchedule("Friday, Nov 15, 2025 - 7:00 PM", "Jakarta International Expo - Hall A"));
                schedules.add(new EventSchedule("Saturday, Nov 16, 2025 - 7:00 PM", "Jakarta International Expo - Hall A"));
                schedules.add(new EventSchedule("Sunday, Nov 17, 2025 - 6:00 PM", "Jakarta International Expo - Main Stage"));

                Intent intent = new Intent(getContext(), EventActivity.class);
                intent.putExtra(EventActivity.EXTRA_TITLE, item.title);
                intent.putExtra(EventActivity.EXTRA_SUBTITLE, item.subtitle);
                intent.putExtra(EventActivity.EXTRA_IMAGE_URL, item.imageUrl);
                intent.putExtra(EventActivity.EXTRA_DESCRIPTION,
                        "The biggest music festival of the year is back! Featuring headliners from across Asia, " +
                                "multiple stages, food vendors, and an unforgettable experience. " +
                                "VIP packages include express entry, lounge access, and exclusive merchandise.");
                intent.putExtra(EventActivity.EXTRA_PRICE, 350000.0); // Premium event price
                intent.putExtra(EventActivity.EXTRA_SCHEDULES, schedules);

                startActivity(intent);
            });
        }
    }

    private SearchActivity.SearchItemPlaceholder findFeaturedEvent(String title) {
        if ("Jakarta Music Fest".equals(title)) {
            // Updated URL to match the one in getAllSearchItems for consistency
            return new SearchActivity.SearchItemPlaceholder("Jakarta Music Fest", "10K interested", "Events", "https://assets.telkomsel.com/public/2025-02/Rayakan-20-Tahun-Java-Jazz-Kembali-Hadir-Lagi-di-Jakarta.jpg?VersionId=7ITMsvqzrJVGjXJhxPJnKm_tPGBCCVxs");
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        charIndex = 0;
        isTyping = true;
        if (placeholderText != null) {
            placeholderText.setText("");
        }
        startTypingAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(typingRunnable);
    }

    private final Runnable typingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || placeholderText == null) {
                return;
            }

            String currentText = searchPrompts.get(currentPromptIndex);

            if (isTyping) {
                if (charIndex < currentText.length()) {
                    charIndex++;
                    placeholderText.setText(currentText.substring(0, charIndex));
                    handler.postDelayed(this, 50);
                } else {
                    isTyping = false;
                    handler.postDelayed(this, 1500);
                }
            } else {
                if (charIndex > 0) {
                    charIndex--;
                    placeholderText.setText(currentText.substring(0, charIndex));
                    handler.postDelayed(this, 10);
                } else {
                    currentPromptIndex = (currentPromptIndex + 1) % searchPrompts.size();
                    isTyping = true;
                    handler.postDelayed(this, 500);
                }
            }
        }
    };

    private void startTypingAnimation() {
        handler.post(typingRunnable);
    }

    private List<Destination> getPopularDestinations() {
        List<Destination> destinations = new ArrayList<>();
        destinations.add(new Destination("Bali", "Indonesia", "Start from Rp 4.500.000",
                "https://bankraya.co.id/uploads/insights/jO3TRUmMuBAuyilKHgu9Ovjfs3nFoubWiSSjB3Pn.jpg"));
        destinations.add(new Destination("Tokyo", "Japan", "Start from Rp 8.900.000",
                "https://media.digitalnomads.world/wp-content/uploads/2021/02/20120635/tokyo-for-digital-nomads.jpg"));
        destinations.add(new Destination("Paris", "France", "Start from Rp 12.000.000",
                "https://www.ilxtravel.com/wp-content/uploads/2024/09/Eiffel-Tower-France.webp"));
        destinations.add(new Destination("Dubai", "UAE", "Start from Rp 7.500.000",
                "https://cdn.prod.website-files.com/64e5211ac1a7bf3e9e5ae267/67f3d62f3981e44484214529_is-dubai-a-country.webp"));
        return destinations;
    }
}