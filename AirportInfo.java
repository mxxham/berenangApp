package com.example.berenang10;

import java.io.Serializable;
import java.util.Objects;
import java.util.Locale;

/**
 * Represents a specific airport, storing its IATA code, name, city, and country.
 * Implements Serializable for easy passing via Intents.
 */
public class AirportInfo implements Serializable {

    // Unique identifier for serialization
    private static final long serialVersionUID = 1L;

    // Essential Airport Properties (made final for immutability)
    private final String iataCode; // e.g., "CGK"
    private final String name;     // e.g., "Soekarno-Hatta International Airport"
    private final String city;     // e.g., "Jakarta"
    private final String country;  // e.g., "Indonesia"

    /**
     * Full constructor for all properties.
     */
    public AirportInfo(String iataCode, String name, String city, String country) {
        // Enforce non-null and trim whitespace
        this.iataCode = iataCode != null ? iataCode.trim().toUpperCase(Locale.getDefault()) : "";
        this.name = name != null ? name.trim() : "";
        this.city = city != null ? city.trim() : "";
        this.country = country != null ? country.trim() : "";
    }

    // --- Accessor (Getter) Methods ---

    public String getIataCode() {
        return iataCode;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    /**
     * Returns a user-friendly, formatted display string.
     * Example: "Jakarta (CGK) - Soekarno-Hatta International Airport"
     */
    public String getDisplayFormat() {
        return city + " (" + iataCode + ") - " + name;
    }

    /**
     * Standardized string representation, useful for debugging.
     */
    @Override
    public String toString() {
        return "AirportInfo{" +
                "iataCode='" + iataCode + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    // --- Feature Methods: Equality and Hashing ---

    /**
     * Defines equality based on the unique IATA code.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AirportInfo that = (AirportInfo) o;
        return Objects.equals(iataCode, that.iataCode);
    }

    /**
     * Generates a hash code based on the unique IATA code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(iataCode);
    }
}
