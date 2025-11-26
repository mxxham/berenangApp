package com.example.berenang10;

/**
 * Model class representing a single booking item displayed in the RecyclerView.
 * Added getPnr() method to ensure compatibility with BookingAdapter.java and
 * BookingDatabaseDetailActivity.java, which rely on the PNR identifier.
 */
public class Booking {
    private String bookingId;
    private String title;
    private String type;
    private String date; // Stored as a formatted string for display
    private String status;

    public Booking(String bookingId, String title, String type, String date, String status) {
        this.bookingId = bookingId;
        this.title = title;
        this.type = type;
        this.date = date;
        this.status = status;
    }

    // The BookingAdapter requires a getPnr() method to display the PNR identifier.
    // We assume the user's bookingId is the intended PNR identifier for the list view.
    public String getPnr() {
        return bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}