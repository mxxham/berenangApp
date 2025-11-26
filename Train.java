package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

public class Train implements Parcelable {
    private String trainNumber;
    private String trainName;
    private String trainClass;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private double price;
    private int availableSeats;
    private String facilities;

    public Train(String trainNumber, String trainName, String trainClass,
                 String origin, String destination, String departureTime,
                 String arrivalTime, String duration, double price,
                 int availableSeats, String facilities) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.trainClass = trainClass;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.price = price;
        this.availableSeats = availableSeats;
        this.facilities = facilities;
    }

    protected Train(Parcel in) {
        trainNumber = in.readString();
        trainName = in.readString();
        trainClass = in.readString();
        origin = in.readString();
        destination = in.readString();
        departureTime = in.readString();
        arrivalTime = in.readString();
        duration = in.readString();
        price = in.readDouble();
        availableSeats = in.readInt();
        facilities = in.readString();
    }

    public static final Creator<Train> CREATOR = new Creator<Train>() {
        @Override
        public Train createFromParcel(Parcel in) {
            return new Train(in);
        }

        @Override
        public Train[] newArray(int size) {
            return new Train[size];
        }
    };

    // Getters
    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getTrainClass() { return trainClass; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getDuration() { return duration; }
    public double getPrice() { return price; }
    public int getAvailableSeats() { return availableSeats; }
    public String getFacilities() { return facilities; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trainNumber);
        dest.writeString(trainName);
        dest.writeString(trainClass);
        dest.writeString(origin);
        dest.writeString(destination);
        dest.writeString(departureTime);
        dest.writeString(arrivalTime);
        dest.writeString(duration);
        dest.writeDouble(price);
        dest.writeInt(availableSeats);
        dest.writeString(facilities);
    }
}