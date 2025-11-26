package com.example.berenang10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// The adapter now uses <Object> to handle both AirportInfo objects and Country String names.
public class AirportInfoAdapter extends ArrayAdapter<Object> {

    private final List<AirportInfo> fullAirportList;
    private final List<String> fullCountryList; // NEW: List of all country names
    private List<Object> suggestionList; // Stores AirportInfo or String (Country Name)
    private final Filter airportFilter = new AirportFilter();

    // Constructor now accepts the list of unique country names
    public AirportInfoAdapter(@NonNull Context context, @NonNull List<AirportInfo> airportList, @NonNull List<String> countryList) {
        // Use a generic built-in layout for the base constructor, the custom layout is applied in getView
        super(context, 0, new ArrayList<>());
        this.fullAirportList = new ArrayList<>(airportList);
        this.fullCountryList = new ArrayList<>(countryList);
        this.suggestionList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return airportFilter;
    }

    // Override getCount and getItem to work with the generic Object list
    @Override
    public int getCount() {
        return suggestionList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return suggestionList.get(position);
    }

    // --- REVISED: getView() to use custom layout and handle both AirportInfo and Country Name ---
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // Using the custom, feature-packed layout
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.airport_autocomplete_dropdown, parent, false);
        }

        TextView title = convertView.findViewById(R.id.airport_title);
        TextView subtitle = convertView.findViewById(R.id.airport_subtitle);
        ImageView icon = convertView.findViewById(R.id.airport_icon);
        TextView iataDisplay = convertView.findViewById(R.id.iata_code_display);

        Object item = getItem(position);

        // This is a common way to fetch colors/drawables, assuming your project has them
        int defaultIconColor = getContext().getResources().getColor(android.R.color.black);
        // Assuming R.color.colorPrimary exists after previous fix
        int primaryColor = getContext().getResources().getColor(R.color.colorPrimary);

        if (item instanceof AirportInfo) {
            AirportInfo airport = (AirportInfo) item;

            // Airport View
            title.setText(String.format("%s (%s)", airport.getCity(), airport.getIataCode()));
            // FIX: Changed getAirportName() to getName()
            subtitle.setText(airport.getName() + " | " + airport.getCountry());
            iataDisplay.setText(airport.getIataCode());
            icon.setImageResource(R.drawable.ic_flight_takeoff);
            icon.setColorFilter(defaultIconColor);

        } else if (item instanceof String) {
            // Country Name Result View
            String country = (String) item;

            // Country View
            title.setText(country);
            subtitle.setText("Search All Airports in " + country);
            iataDisplay.setText("");
            icon.setImageResource(R.drawable.ic_map);
            icon.setColorFilter(primaryColor);
        }

        return convertView;
    }

    private class AirportFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Object> suggestions = new ArrayList<>();

            if (constraint != null) {
                String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();

                // 1. Check for Country Match (Prioritized Suggestion)
                for (String country : fullCountryList) {
                    if (country.toLowerCase(Locale.getDefault()).startsWith(filterPattern)) {
                        suggestions.add(country); // Add the country name as a suggested String object
                    }
                }

                // 2. Check for Airport/City/Code Match
                for (AirportInfo airport : fullAirportList) {
                    // Search against City, Name, IATA Code, OR Country
                    if (airport.getCity().toLowerCase(Locale.getDefault()).contains(filterPattern) ||
                            // Using getName() for filtering
                            airport.getName().toLowerCase(Locale.getDefault()).contains(filterPattern) ||
                            airport.getIataCode().toLowerCase(Locale.getDefault()).startsWith(filterPattern) ||
                            // Check against Country Name
                            airport.getCountry().toLowerCase(Locale.getDefault()).contains(filterPattern))
                    {
                        // Ensure the airport is not already suggested as part of a country match
                        if (!suggestions.contains(airport)) {
                            suggestions.add(airport);
                        }
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            suggestionList.clear();
            if (results != null && results.count > 0) {
                // The results list contains both AirportInfo and String objects
                suggestionList.addAll((List<Object>) results.values);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        // --- REVISED: convertResultToString() to handle both AirportInfo and Country Name ---
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if (resultValue instanceof AirportInfo) {
                // Format for the input box when an airport is selected
                AirportInfo airport = (AirportInfo) resultValue;
                // FIX: Changed getAirportName() to getName()
                return String.format("%s (%s) - %s", airport.getCity(), airport.getIataCode(), airport.getName());
            } else if (resultValue instanceof String) {
                // Put the raw country name in the input box when a country is selected
                return (String) resultValue;
            }
            return "";
        }
    }
}