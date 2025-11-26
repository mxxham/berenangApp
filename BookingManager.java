package com.example.berenang10;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// IMPORTANT: You would need a library like Gson for the following JSON operations.
// import com.google.gson.Gson;

public class BookingManager {
    private static BookingManager instance;
    private final DatabaseHelper dbHelper; // Use the DatabaseHelper for persistence
    private final Random random = new Random();
    // private final Gson gson = new Gson(); // Gson object for JSON operations

    // --- 1. Update Constructor and getInstance to require Context ---
    private BookingManager(Context context) {
        // Initialize the DatabaseHelper
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static synchronized BookingManager getInstance(Context context) {
        if (instance == null) {
            instance = new BookingManager(context.getApplicationContext());
        }
        return instance;
    }
    // Note: You must update all calls to BookingManager.getInstance() in your code
    // (e.g., in BookingsFragment and activities) to pass a Context.

    // Helper method to generate a unique ID
    private String generateUniqueId(String prefix, int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }

    // -------------------------------------------------------------------
    // --- 2. Saving a Booking (The core persistence logic) ---
    // -------------------------------------------------------------------

    // Existing: Add any BookingRecord (used by Flight) - NOW PERSISTED
    public void addBooking(BookingRecord booking, String userEmail) {
        // Step 1: Serialize the complex Flight/Hotel object into a JSON string
        String detailsJson;
        if ("Flight".equals(booking.getBookingType())) {
            // detailsJson = gson.toJson(booking.getFlight()); // Placeholder
            detailsJson = "Flight details for: " + booking.getFlight().getFlightNumber(); // Mock detail
        } else {
            // detailsJson = gson.toJson(booking.getHotel()); // Placeholder
            detailsJson = "Hotel details for: " + booking.getHotel().getName(); // Mock detail
        }

        // Step 2: Use DatabaseHelper to insert the record
        long result = dbHelper.addBooking(
                booking.getPnr() != null ? booking.getPnr() : booking.getBookingId(), // Use PNR or Booking ID as unique key
                booking.getBookingType(),
                userEmail, // We MUST pass the user's email here for filtering
                booking.getBookingType().equals("Flight") ?
                        booking.getFlight().getFlightNumber() + " (" + booking.getFlight().getAirline() + ")" :
                        booking.getHotel().getName(),
                detailsJson,
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getBookingTimestamp(),
                booking.getPaymentMethod()
        );

        if (result == -1) {
            System.err.println("Error saving booking to database!");
        }
    }

    // NEW: Add Hotel Booking - Modified to use persistence
    public String addHotelBooking(String userEmail, Hotel hotel, HotelSearchParams searchParams,
                                  String guestName, String guestEmail, String guestPhone,
                                  String guestIdNumber, String paymentMethod, double totalAmount,
                                  long timestamp, String status) {

        // 1. Generate the unique Hotel ID
        String bookingId = generateUniqueId("H", 7);

        // 2. Create the new BookingRecord object
        BookingRecord newRecord = new BookingRecord(
                bookingId, hotel, searchParams, guestName, guestEmail,
                guestPhone, guestIdNumber, paymentMethod, totalAmount,
                timestamp, status
        );

        // 3. Save the new unified record to the database
        addBooking(newRecord, userEmail);

        // 4. Return the ID
        return bookingId;
    }


    // -------------------------------------------------------------------
    // --- 3. Retrieving Bookings by User Email (The login key) ---
    // -------------------------------------------------------------------

    public List<BookingRecord> getAllBookings(String userEmail) {
        List<BookingRecord> bookingRecords = new ArrayList<>();

        // Fetch raw data from SQLite
        List<BookingData> rawBookings = dbHelper.getAllBookings(userEmail);

        // Convert raw BookingData objects into complex BookingRecord objects
        for (BookingData rawBooking : rawBookings) {

            // *** SIMPLIFIED MOCK RECONSTRUCTION ***

            String title = rawBooking.getTitle(); // Use the title field for simplicity

            if ("Flight".equals(rawBooking.getType())) {
                // Mock Flight and related data
                // Flight constructor (13 arguments)
                Flight mockFlight = new Flight(
                        title,           // Arg 1: Airline (using title mock)
                        "AR007",         // Arg 2: Flight Number
                        "Jakarta",       // Arg 3: Origin
                        "Bali",          // Arg 4: Destination
                        "10:00",         // Arg 5: Departure Time
                        "12:00",         // Arg 6: Arrival Time
                        "CGK",           // Arg 7: Origin Airport Code (Mock)
                        "DPS",           // Arg 8: Destination Airport Code (Mock)
                        "1hr 30m",       // Arg 9: Duration (Mock)
                        rawBooking.getAmount(),// Arg 10: Price (double, using raw amount)
                        250,             // Arg 11: Seats Available (int, Mock)
                        false,           // Arg 12: Is Direct (boolean, Mock)
                        "Economy"        // Arg 13: Class Type (String, Mock)
                );

                // FlightSearchParams constructor (6 arguments)
                FlightSearchParams mockSearch = new FlightSearchParams(
                        "15 Nov",       // Arg 1: Departure Date (String)
                        "1 Adult",      // Arg 2: Passenger Summary (String)
                        "Economy",      // Arg 3: Class Type (String)
                        "Jakarta",      // Arg 4: Origin City (Mock String)
                        1,              // Arg 5: Number of Adults (Mock int)
                        "Bali"          // Arg 6: Destination City (Mock String)
                );

                // PassengerData constructor (4 arguments)
                PassengerData mockPassenger = new PassengerData(
                        "Dummy Passenger", // Arg 1: Full Name
                        userEmail,         // Arg 2: Email
                        "12345",           // Arg 3: Phone
                        "IDN-98765"        // Arg 4: ID/Passport Number (NEW MOCK ARGUMENT)
                );

                BookingRecord flightRecord = new BookingRecord(
                        rawBooking.getPnr(), mockFlight, mockSearch, mockPassenger,
                        rawBooking.getPaymentMethod(), rawBooking.getAmount(),
                        rawBooking.getDate(), rawBooking.getStatus()
                );
                bookingRecords.add(flightRecord);

            } else if ("Hotel".equals(rawBooking.getType())) {

                // 🌟 FIX APPLIED: Added the 15th int argument (Image Resource ID) 🌟
                // Constructor signature used: Hotel(String,String,String,String,double,int,String,double,String,boolean,boolean,boolean,boolean,String, int)
                Hotel mockHotel = new Hotel(
                        title,                          // Arg 1: Hotel Name (String)
                        "Kuta, Bali",                   // Arg 2: Location (String)
                        "50m from beach",               // Arg 3: Description/Address (String)
                        "Deluxe Room",                  // Arg 4: Room Type (String)
                        rawBooking.getAmount(),         // Arg 5: Base Price (double)
                        4,                              // Arg 6: Star Rating (int)
                        "Free Breakfast",               // Arg 7: Amenities 1 (String)
                        25.0,                           // Arg 8: Guest Rating (double, Mock)
                        "Swimming Pool",                // Arg 9: Amenities 2 (String)
                        true,                           // Arg 10: Has WiFi (boolean)
                        true,                           // Arg 11: Has Pool (boolean)
                        false,                          // Arg 12: Is Pet Friendly (boolean)
                        true,                           // Arg 13: Free Cancellation (boolean)
                        "Best hotel in Bali",           // Arg 14: Tagline (String)
                        R.drawable.placeholder_hotel_1  // Arg 15: Image Resource ID (int - ADDED)
                );
                // ----------------------------------------------------------------------------------------

                // HotelSearchParams constructor (5 arguments)
                HotelSearchParams mockSearch = new HotelSearchParams("Bali", "15 Nov 2025", "17 Nov 2025", 1, 2);

                BookingRecord hotelRecord = new BookingRecord(
                        rawBooking.getPnr(), mockHotel, mockSearch, rawBooking.getTitle(), // Using title as guest name mock
                        userEmail, "12345", "IDN-123",
                        rawBooking.getPaymentMethod(), rawBooking.getAmount(),
                        rawBooking.getDate(), rawBooking.getStatus()
                );
                bookingRecords.add(hotelRecord);
            }
        }

        return bookingRecords;
    }

    // -------------------------------------------------------------------
    // --- 4. Database Interaction Methods ---
    // -------------------------------------------------------------------

    // Updated to use the unique PNR/Booking ID from the database
    public BookingRecord getBookingById(String id) {
        // Fetch raw BookingData from the DB
        BookingData rawBooking = dbHelper.getBookingByPNR(id);

        if (rawBooking != null) {
            // Reconstruct the complex object (similar logic as in getAllBookings)
            return null;
        }
        return null;
    }


    public void updateBookingStatus(String id, String newStatus) {
        // Update status in the database
        dbHelper.updateBookingStatus(id, newStatus);
    }

    public void cancelBooking(String id) {
        updateBookingStatus(id, "Cancelled");
    }
}