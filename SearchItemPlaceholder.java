package com.example.berenang10;

import java.io.Serializable;
import java.util.List;

/**
 * A data model class representing an item displayed in search results or horizontal lists.
 * This class is Serializable so it can be passed easily via Intents.
 */
public class SearchItemPlaceholder implements Serializable {

    // Core Fields
    private String title;
    private String subtitle;
    private String category;
    private String imageUrl;

    // Feature Fields (for events/bookable items)
    private double price;
    private List<EventSchedule> schedules;


    /**
     * Constructor for feature-packed items (Events, Bookables).
     */
    public SearchItemPlaceholder(String title, String subtitle, String category,
                                 String imageUrl, double price, List<EventSchedule> schedules) {
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.imageUrl = imageUrl;
        this.price = price;
        this.schedules = schedules;
    }

    /**
     * Simpler Constructor for standard items (like Destinations or general activities
     * that don't immediately require booking details).
     */
    public SearchItemPlaceholder(String title, String subtitle, String category, String imageUrl) {
        this(title, subtitle, category, imageUrl, 0.0, null);
    }

    // --- Public Getters (Standard Practice) ---

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public List<EventSchedule> getSchedules() {
        return schedules;
    }

    // --- Utility ---

    @Override
    public String toString() {
        return "SearchItemPlaceholder{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", schedulesCount=" + (schedules != null ? schedules.size() : 0) +
                '}';
    }
}