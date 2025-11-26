package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

public class FlightSearchParams implements Parcelable {
    private String origin;
    private String destination;
    private String departureDate;
    private String returnDate;
    private int passengers;
    private String seatClass;

    public FlightSearchParams(String origin, String destination, String departureDate,
                              String returnDate, int passengers, String seatClass) {
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.passengers = passengers;
        this.seatClass = seatClass;
    }

    protected FlightSearchParams(Parcel in) {
        origin = in.readString();
        destination = in.readString();
        departureDate = in.readString();
        returnDate = in.readString();
        passengers = in.readInt();
        seatClass = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(origin);
        dest.writeString(destination);
        dest.writeString(departureDate);
        dest.writeString(returnDate);
        dest.writeInt(passengers);
        dest.writeString(seatClass);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FlightSearchParams> CREATOR = new Creator<FlightSearchParams>() {
        @Override
        public FlightSearchParams createFromParcel(Parcel in) {
            return new FlightSearchParams(in);
        }

        @Override
        public FlightSearchParams[] newArray(int size) {
            return new FlightSearchParams[size];
        }
    };

    // Getters
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureDate() { return departureDate; }
    public String getReturnDate() { return returnDate; }
    public int getPassengers() { return passengers; }
    public String getSeatClass() { return seatClass; }
}