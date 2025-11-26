package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar; // Import Calendar
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HotelSearchParams implements Parcelable {
    private String destination;
    private String checkInDate;
    private String checkOutDate;
    private int guests;
    private int rooms;

    public HotelSearchParams(String destination, String checkInDate, String checkOutDate,
                             int guests, int rooms) {
        this.destination = destination;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.guests = guests;
        this.rooms = rooms;
    }

    protected HotelSearchParams(Parcel in) {
        destination = in.readString();
        checkInDate = in.readString();
        checkOutDate = in.readString();
        guests = in.readInt();
        rooms = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(destination);
        dest.writeString(checkInDate);
        dest.writeString(checkOutDate);
        dest.writeInt(guests);
        dest.writeInt(rooms);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HotelSearchParams> CREATOR = new Creator<HotelSearchParams>() {
        @Override
        public HotelSearchParams createFromParcel(Parcel in) {
            return new HotelSearchParams(in);
        }

        @Override
        public HotelSearchParams[] newArray(int size) {
            return new HotelSearchParams[size];
        }
    };

    public String getDestination() { return destination; }
    public String getCheckInDate() { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public int getGuests() { return guests; }
    public int getRooms() { return rooms; }

    /**
     * Calculates the number of nights by finding the difference between check-in and check-out dates.
     * Uses Calendar to normalize dates to midnight to prevent issues with Daylight Saving Time.
     * Assumes dates are stored in the format "dd MMM yyyy".
     * @return The number of nights, or 1 if calculation fails or difference is zero/negative.
     */
    public long getNights() {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            Date dateIn = format.parse(checkInDate);
            Date dateOut = format.parse(checkOutDate);

            if (dateIn != null && dateOut != null) {
                // Normalize dates to the start of the day (midnight) to ignore time component and DST changes
                Calendar calIn = Calendar.getInstance();
                calIn.setTime(dateIn);
                calIn.set(Calendar.HOUR_OF_DAY, 0);
                calIn.set(Calendar.MINUTE, 0);
                calIn.set(Calendar.SECOND, 0);
                calIn.set(Calendar.MILLISECOND, 0);

                Calendar calOut = Calendar.getInstance();
                calOut.setTime(dateOut);
                calOut.set(Calendar.HOUR_OF_DAY, 0);
                calOut.set(Calendar.MINUTE, 0);
                calOut.set(Calendar.SECOND, 0);
                calOut.set(Calendar.MILLISECOND, 0);

                long diff = calOut.getTimeInMillis() - calIn.getTimeInMillis();
                long nights = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                // Ensure a positive result, return 1 if the stay is zero or negative (e.g., same day check-in/out)
                return nights > 0 ? nights : 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        return 1;
    }
}