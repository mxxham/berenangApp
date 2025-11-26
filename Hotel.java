package com.example.berenang10;

import android.os.Parcel;
import android.os.Parcelable;

public class Hotel implements Parcelable {
    // Fields from the first (Parcelable) Hotel class
    private String hotelId;
    private String name;
    private String location; // Used for getArea()
    private String address;
    private double rating;
    private int reviewCount;
    private String roomType;
    private double pricePerNight;
    private String amenities;
    private boolean hasBreakfast;
    private boolean hasWifi;
    private boolean hasPool;
    private boolean hasParking;
    private String imageUrl;

    // Field added from the second (simpler) Hotel class
    private int imageResId;

    // Constructor combining all fields
    public Hotel(String hotelId, String name, String location, String address, double rating,
                 int reviewCount, String roomType, double pricePerNight, String amenities,
                 boolean hasBreakfast, boolean hasWifi, boolean hasPool, boolean hasParking,
                 String imageUrl, int imageResId) {
        this.hotelId = hotelId;
        this.name = name;
        this.location = location;
        this.address = address;
        // Note: The second class used 'float' for rating; using 'double' here as it's the broader type.
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.amenities = amenities;
        this.hasBreakfast = hasBreakfast;
        this.hasWifi = hasWifi;
        this.hasPool = hasPool;
        this.hasParking = hasParking;
        this.imageUrl = imageUrl;
        this.imageResId = imageResId; // New field
    }

    // --- Parcelable Implementation ---
    protected Hotel(Parcel in) {
        hotelId = in.readString();
        name = in.readString();
        location = in.readString();
        address = in.readString();
        rating = in.readDouble();
        reviewCount = in.readInt();
        roomType = in.readString();
        pricePerNight = in.readDouble();
        amenities = in.readString();
        hasBreakfast = in.readByte() != 0;
        hasWifi = in.readByte() != 0;
        hasPool = in.readByte() != 0;
        hasParking = in.readByte() != 0;
        imageUrl = in.readString();
        imageResId = in.readInt(); // New field added to Parcel read
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hotelId);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(address);
        dest.writeDouble(rating);
        dest.writeInt(reviewCount);
        dest.writeString(roomType);
        dest.writeDouble(pricePerNight);
        dest.writeString(amenities);
        dest.writeByte((byte) (hasBreakfast ? 1 : 0));
        dest.writeByte((byte) (hasWifi ? 1 : 0));
        dest.writeByte((byte) (hasPool ? 1 : 0));
        dest.writeByte((byte) (hasParking ? 1 : 0));
        dest.writeString(imageUrl);
        dest.writeInt(imageResId); // New field added to Parcel write
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Hotel> CREATOR = new Creator<Hotel>() {
        @Override
        public Hotel createFromParcel(Parcel in) {
            return new Hotel(in);
        }

        @Override
        public Hotel[] newArray(int size) {
            return new Hotel[size];
        }
    };
    // --- End Parcelable Implementation ---


    // --- Getters and Setters ---
    public String getHotelId() { return hotelId; }
    public String getName() { return name; }
    public String getArea() { return location; } // Uses location field
    public String getLocation() { return location; } // Getter from second class
    public String getAddress() { return address; }
    public double getRating() { return rating; } // Note: returns double, not float
    public int getReviewCount() { return reviewCount; }
    public String getRoomType() { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public String getAmenities() { return amenities; }
    public boolean hasBreakfast() { return hasBreakfast; }
    public boolean hasWifi() { return hasWifi; }
    public boolean hasPool() { return hasPool; }
    public boolean hasParking() { return hasParking; }
    public String getImageUrl() { return imageUrl; }
    public int getImageResId() { return imageResId; } // New getter

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setArea(String area) {
        this.location = area;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    // Note: The second class had a String price field, which is often redundant
    // when pricePerNight (double) is present. I've omitted the String price field
    // to avoid confusion and kept the double pricePerNight field.
    // If you need the String price field, it would also need to be added.
}