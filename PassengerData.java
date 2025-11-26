package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

public class PassengerData implements Parcelable {
    private String fullName;
    private String email;
    private String phone;
    private String passportNumber;

    public PassengerData(String fullName, String email, String phone, String passportNumber) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passportNumber = passportNumber;
    }

    protected PassengerData(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        phone = in.readString();
        passportNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(passportNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PassengerData> CREATOR = new Creator<PassengerData>() {
        @Override
        public PassengerData createFromParcel(Parcel in) {
            return new PassengerData(in);
        }

        @Override
        public PassengerData[] newArray(int size) {
            return new PassengerData[size];
        }
    };

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassportNumber() { return passportNumber; }
}