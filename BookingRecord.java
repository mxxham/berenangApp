package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

public class BookingRecord implements Parcelable {

    // --- Common Fields ---
    private String bookingId; // Used for PNR (Flight) or H-ID (Hotel)
    private String paymentMethod;
    private double totalAmount;
    private long bookingTimestamp;
    private String status;
    private String bookingType; // NEW: "Flight" or "Hotel"

    // --- Flight Fields ---
    private String pnr; // Kept for backward compatibility with Flight logic
    private Flight flight;
    private FlightSearchParams searchParams;
    private PassengerData passengerData;

    // --- Hotel Fields (NEW) ---
    private Hotel hotel;
    private HotelSearchParams hotelSearchParams;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String guestIdNumber;

    // -------------------------------------------------------------------
    // --- 1. Constructor for FLIGHT Booking (Existing, just updated) ---
    // -------------------------------------------------------------------
    public BookingRecord(String pnr, Flight flight, FlightSearchParams searchParams,
                         PassengerData passengerData, String paymentMethod, double totalAmount,
                         long bookingTimestamp, String status) {
        this.pnr = pnr;
        this.flight = flight;
        this.searchParams = searchParams;
        this.passengerData = passengerData;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.bookingTimestamp = bookingTimestamp;
        this.status = status;
        this.bookingType = "Flight"; // Set type
        this.bookingId = "BRG-" + pnr; // Set unified ID

        // Ensure Hotel fields are null for a Flight booking
        this.hotel = null;
        this.hotelSearchParams = null;
        this.guestName = null;
        this.guestEmail = null;
        this.guestPhone = null;
        this.guestIdNumber = null;
    }

    // -------------------------------------------------------------------
    // --- 2. Constructor for HOTEL Booking (NEW) ---
    // -------------------------------------------------------------------
    public BookingRecord(String bookingId, Hotel hotel, HotelSearchParams hotelSearchParams,
                         String guestName, String guestEmail, String guestPhone, String guestIdNumber,
                         String paymentMethod, double totalAmount, long bookingTimestamp, String status) {
        this.bookingId = bookingId;
        this.hotel = hotel;
        this.hotelSearchParams = hotelSearchParams;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
        this.guestIdNumber = guestIdNumber;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.bookingTimestamp = bookingTimestamp;
        this.status = status;
        this.bookingType = "Hotel"; // Set type

        // Ensure Flight fields are null for a Hotel booking
        this.pnr = null;
        this.flight = null;
        this.searchParams = null;
        this.passengerData = null;
    }

    // --- Getters ---
    // Note: getPnr() now only works for Flight bookings. Use getBookingId() for both.
    public String getPnr() { return pnr; }
    public String getBookingId() { return bookingId; }
    public String getBookingType() { return bookingType; }

    // Flight Getters
    public Flight getFlight() { return flight; }
    public FlightSearchParams getSearchParams() { return searchParams; }
    public PassengerData getPassengerData() { return passengerData; }

    // Hotel Getters (NEW)
    public Hotel getHotel() { return hotel; }
    public HotelSearchParams getHotelSearchParams() { return hotelSearchParams; }
    public String getGuestName() { return guestName; }
    public String getGuestEmail() { return guestEmail; }
    public String getGuestPhone() { return guestPhone; }
    public String getGuestIdNumber() { return guestIdNumber; }

    // Common Getters/Setters
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount() { return totalAmount; }
    public long getBookingTimestamp() { return bookingTimestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // --- Parcelable Implementation (REQUIRED for passing between activities) ---
    protected BookingRecord(Parcel in) {
        bookingId = in.readString();
        bookingType = in.readString();
        paymentMethod = in.readString();
        totalAmount = in.readDouble();
        bookingTimestamp = in.readLong();
        status = in.readString();

        // Flight Fields
        pnr = in.readString();
        flight = in.readParcelable(Flight.class.getClassLoader());
        searchParams = in.readParcelable(FlightSearchParams.class.getClassLoader());
        passengerData = in.readParcelable(PassengerData.class.getClassLoader());

        // Hotel Fields
        hotel = in.readParcelable(Hotel.class.getClassLoader());
        hotelSearchParams = in.readParcelable(HotelSearchParams.class.getClassLoader());
        guestName = in.readString();
        guestEmail = in.readString();
        guestPhone = in.readString();
        guestIdNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookingId);
        dest.writeString(bookingType);
        dest.writeString(paymentMethod);
        dest.writeDouble(totalAmount);
        dest.writeLong(bookingTimestamp);
        dest.writeString(status);

        // Flight Fields
        dest.writeString(pnr);
        dest.writeParcelable(flight, flags);
        dest.writeParcelable(searchParams, flags);
        dest.writeParcelable(passengerData, flags);

        // Hotel Fields
        dest.writeParcelable(hotel, flags);
        dest.writeParcelable(hotelSearchParams, flags);
        dest.writeString(guestName);
        dest.writeString(guestEmail);
        dest.writeString(guestPhone);
        dest.writeString(guestIdNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingRecord> CREATOR = new Creator<BookingRecord>() {
        @Override
        public BookingRecord createFromParcel(Parcel in) {
            return new BookingRecord(in);
        }

        @Override
        public BookingRecord[] newArray(int size) {
            return new BookingRecord[size];
        }
    };
}