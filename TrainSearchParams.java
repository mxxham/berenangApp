package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

public class TrainSearchParams implements Parcelable {
    private final String fromStation;
    private final String toStation;
    private final long dateMillis;
    private final int passengerCount;
    private final boolean isRoundTrip; // 🆕 New field added

    // 1. UPDATED CONSTRUCTOR: Now accepts the isRoundTrip boolean
    public TrainSearchParams(String fromStation, String toStation, long dateMillis, int passengerCount, boolean isRoundTrip) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.dateMillis = dateMillis;
        this.passengerCount = passengerCount;
        this.isRoundTrip = isRoundTrip; // 🆕 Initialize new field
    }

    // Getters
    public String getFromStation() { return fromStation; }
    public String getToStation() { return toStation; }
    public long getDateMillis() { return dateMillis; }
    public int getPassengerCount() { return passengerCount; }
    public boolean isRoundTrip() { return isRoundTrip; } // 🆕 New getter

    // --- Parcelable Implementation ---

    // 2. UPDATED PARCEL CONSTRUCTOR: Reads the new boolean
    protected TrainSearchParams(Parcel in) {
        fromStation = in.readString();
        toStation = in.readString();
        dateMillis = in.readLong();
        passengerCount = in.readInt();
        isRoundTrip = in.readByte() != 0; // 🆕 Read boolean (readByte() != 0 is standard)
    }

    public static final Creator<TrainSearchParams> CREATOR = new Creator<TrainSearchParams>() {
        @Override
        public TrainSearchParams createFromParcel(Parcel in) {
            return new TrainSearchParams(in);
        }

        @Override
        public TrainSearchParams[] newArray(int size) {
            return new TrainSearchParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // 3. UPDATED writeToParcel: Writes the new boolean
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromStation);
        dest.writeString(toStation);
        dest.writeLong(dateMillis);
        dest.writeInt(passengerCount);
        dest.writeByte((byte) (isRoundTrip ? 1 : 0)); // 🆕 Write boolean
    }
}