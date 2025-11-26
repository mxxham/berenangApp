package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

public class Flight implements Parcelable {
    private String flightId;
    private String airline;
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String seatClass;
    private double price;
    private int availableSeats;
    private boolean isDirect;
    private String layoverInfo;

    public Flight(String flightId, String airline, String flightNumber, String origin,
                  String destination, String departureTime, String arrivalTime, String duration,
                  String seatClass, double price, int availableSeats, boolean isDirect, String layoverInfo) {
        this.flightId = flightId;
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.seatClass = seatClass;
        this.price = price;
        this.availableSeats = availableSeats;
        this.isDirect = isDirect;
        this.layoverInfo = layoverInfo;
    }

    protected Flight(Parcel in) {
        flightId = in.readString();
        airline = in.readString();
        flightNumber = in.readString();
        origin = in.readString();
        destination = in.readString();
        departureTime = in.readString();
        arrivalTime = in.readString();
        duration = in.readString();
        seatClass = in.readString();
        price = in.readDouble();
        availableSeats = in.readInt();
        isDirect = in.readByte() != 0;
        layoverInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(flightId);
        dest.writeString(airline);
        dest.writeString(flightNumber);
        dest.writeString(origin);
        dest.writeString(destination);
        dest.writeString(departureTime);
        dest.writeString(arrivalTime);
        dest.writeString(duration);
        dest.writeString(seatClass);
        dest.writeDouble(price);
        dest.writeInt(availableSeats);
        dest.writeByte((byte) (isDirect ? 1 : 0));
        dest.writeString(layoverInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Flight> CREATOR = new Creator<Flight>() {
        @Override
        public Flight createFromParcel(Parcel in) {
            return new Flight(in);
        }

        @Override
        public Flight[] newArray(int size) {
            return new Flight[size];
        }
    };

    // Getters
    public String getFlightId() { return flightId; }
    public String getAirline() { return airline; }
    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getDuration() { return duration; }
    public String getSeatClass() { return seatClass; }
    public double getPrice() { return price; }
    public int getAvailableSeats() { return availableSeats; }
    public boolean isDirect() { return isDirect; }
    public String getLayoverInfo() { return layoverInfo; }
}