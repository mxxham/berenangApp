package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data model representing a passenger, used for passing booking information
 * between activities. This class implements Parcelable to be passed in Intents.
 */
public class Passenger implements Parcelable {
    private final String fullName;
    private final String email;
    private final String phone;
    private final String idNumber;

    public Passenger(String fullName, String email, String phone, String idNumber) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.idNumber = idNumber;
    }

    // --- Getters ---
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getIdNumber() {
        return idNumber;
    }

    // --- Parcelable Implementation ---

    protected Passenger(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        phone = in.readString();
        idNumber = in.readString();
    }

    public static final Creator<Passenger> CREATOR = new Creator<Passenger>() {
        @Override
        public Passenger createFromParcel(Parcel in) {
            return new Passenger(in);
        }

        @Override
        public Passenger[] newArray(int size) {
            return new Passenger[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(idNumber);
    }
}