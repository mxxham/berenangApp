package com.example.berenang10;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

// --- BCrypt and Hashing Imports ---
import org.mindrot.jbcrypt.BCrypt;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// ---------------------------------

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "berenang.db";
    // *** CRITICAL CHANGE: INCREMENT VERSION TO 2 ***
    private static final int DATABASE_VERSION = 2;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";

    // *** NEW COLUMN CONSTANT ***
    private static final String COL_PROFILE_IMAGE_URI = "profile_image_uri";

    // Bookings table
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COL_BOOKING_ID = "id";
    private static final String COL_BOOKING_PNR = "pnr";
    private static final String COL_BOOKING_TYPE = "booking_type";
    private static final String COL_BOOKING_USER_EMAIL = "user_email";
    private static final String COL_BOOKING_TITLE = "title";
    private static final String COL_BOOKING_DETAILS = "details";
    private static final String COL_BOOKING_AMOUNT = "total_amount";
    private static final String COL_BOOKING_STATUS = "status";
    private static final String COL_BOOKING_DATE = "booking_date";
    private static final String COL_BOOKING_PAYMENT_METHOD = "payment_method";

    // --- Hashing Constants ---
    private static final int BCRYPT_WORK_FACTOR = 12;
    // -------------------------

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_USER_PASSWORD + " TEXT NOT NULL, "
                // *** ADD NEW COLUMN to the creation statement ***
                + COL_PROFILE_IMAGE_URI + " TEXT DEFAULT NULL)";
        db.execSQL(createUsersTable);

        // Create bookings table
        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " ("
                + COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BOOKING_PNR + " TEXT UNIQUE NOT NULL, "
                + COL_BOOKING_TYPE + " TEXT NOT NULL, "
                + COL_BOOKING_USER_EMAIL + " TEXT NOT NULL, "
                + COL_BOOKING_TITLE + " TEXT NOT NULL, "
                + COL_BOOKING_DETAILS + " TEXT NOT NULL, "
                + COL_BOOKING_AMOUNT + " REAL NOT NULL, "
                + COL_BOOKING_STATUS + " TEXT NOT NULL, "
                + COL_BOOKING_DATE + " INTEGER NOT NULL, "
                + COL_BOOKING_PAYMENT_METHOD + " TEXT NOT NULL)";
        db.execSQL(createBookingsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If DATABASE_VERSION is incremented, this runs.
        // Since we only need to add a column, we use ALTER TABLE.

        if (oldVersion < 2) {
            // Add the new profile image URI column to the users table
            String alterTable = "ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_PROFILE_IMAGE_URI + " TEXT DEFAULT NULL";
            db.execSQL(alterTable);
        }

        // NOTE: If you needed a full clean slate for testing, you would use:
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        // onCreate(db);
    }

    // ==================== HASHING METHODS (Double-Hash: SHA-256 + BCrypt) ====================

    /**
     * Helper for a simple SHA-256 hash. Used as the first layer in the double hash.
     */
    private String simpleSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported on this device", e);
        }
    }

    /**
     * Hashes the password using the double-hash method: BCrypt(SHA-256(password)).
     */
    public String hashPassword(String password) {
        // 1. First hash (SHA-256)
        String firstHash = simpleSha256(password);

        // 2. Second hash (BCrypt on the result of the first hash)
        String salt = BCrypt.gensalt(BCRYPT_WORK_FACTOR);
        return BCrypt.hashpw(firstHash, salt);
    }

    /**
     * Verifies a plaintext password against a stored double-hash.
     */
    public boolean verifyPassword(String plaintextPassword, String storedHash) {
        // 1. First hash the input password with SHA-256
        String firstHash = simpleSha256(plaintextPassword);

        // 2. Use BCrypt to verify the first hash against the stored BCrypt hash
        return BCrypt.checkpw(firstHash, storedHash);
    }

    // ==================== USER OPERATIONS (Updated and New) ====================

    public long registerUser(String name, String email, String password) {
        // HASH the password before storing
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) return -1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, hashedPassword); // Store the HASH
        // The COL_PROFILE_IMAGE_URI column will use its default value (NULL)

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // *** NEW METHOD: Update Profile Image URI ***
    public int updateProfileImageUri(String email, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_IMAGE_URI, imageUri);

        int rowsAffected = db.update(TABLE_USERS, values,
                COL_USER_EMAIL + " = ?",
                new String[]{email});
        db.close();
        return rowsAffected;
    }

    // *** NEW METHOD: Get Profile Image URI ***
    public String getProfileImageUri(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String uri = null;

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_PROFILE_IMAGE_URI},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        if (cursor.moveToFirst()) {
            // Note: You must handle the case where the URI is NULL in your fragment
            uri = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFILE_IMAGE_URI));
        }
        cursor.close();
        db.close();
        return uri; // Returns the URI string or null
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_EMAIL},
                COL_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean validateUser(String email, String password) {
        // 1. Retrieve the stored hash for the given email
        SQLiteDatabase db = this.getReadableDatabase();
        String storedHash = null;
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_PASSWORD}, // Select only the password hash
                COL_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor.moveToFirst()) {
            storedHash = cursor.getString(0);
        }
        cursor.close();
        db.close();

        // 2. If an email/hash was found, verify the password
        if (storedHash != null) {
            return verifyPassword(password, storedHash);
        }
        return false;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_NAME},
                COL_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return name;
    }

    // ==================== BOOKING OPERATIONS (No changes needed here) ====================
    // ... (rest of the booking operations remain the same) ...

    public long addBooking(String pnr, String bookingType, String userEmail,
                           String title, String details, double amount,
                           String status, long bookingDate, String paymentMethod) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_PNR, pnr);
        values.put(COL_BOOKING_TYPE, bookingType);
        values.put(COL_BOOKING_USER_EMAIL, userEmail);
        values.put(COL_BOOKING_TITLE, title);
        values.put(COL_BOOKING_DETAILS, details);
        values.put(COL_BOOKING_AMOUNT, amount);
        values.put(COL_BOOKING_STATUS, status);
        values.put(COL_BOOKING_DATE, bookingDate);
        values.put(COL_BOOKING_PAYMENT_METHOD, paymentMethod);

        long result = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return result;
    }

    public List<BookingData> getAllBookings(String userEmail) {
        List<BookingData> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKINGS,
                null,
                COL_BOOKING_USER_EMAIL + "=?",
                new String[]{userEmail},
                null, null,
                COL_BOOKING_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                BookingData booking = new BookingData(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_PNR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_DETAILS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BOOKING_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_STATUS)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_BOOKING_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_PAYMENT_METHOD))
                );
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookingList;
    }

    public BookingData getBookingByPNR(String pnr) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS,
                null,
                COL_BOOKING_PNR + "=?",
                new String[]{pnr},
                null, null, null);

        BookingData booking = null;
        if (cursor.moveToFirst()) {
            booking = new BookingData(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_PNR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_DETAILS)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BOOKING_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_STATUS)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_BOOKING_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_PAYMENT_METHOD))
            );
        }

        cursor.close();
        db.close();
        return booking;
    }

    public int updateBookingStatus(String pnr, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_STATUS, newStatus);

        int rowsAffected = db.update(TABLE_BOOKINGS, values,
                COL_BOOKING_PNR + "=?",
                new String[]{pnr});

        db.close();
        return rowsAffected;
    }

    public int deleteBooking(String pnr) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_BOOKINGS,
                COL_BOOKING_PNR + "=?",
                new String[]{pnr});
        db.close();
        return rowsDeleted;
    }

    public int getBookingCount(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_BOOKINGS +
                        " WHERE " + COL_BOOKING_USER_EMAIL + "=?",
                new String[]{userEmail});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}

// Simple BookingData class for database retrieval
class BookingData {
    String pnr;
    String type;
    String title;
    String details;
    double amount;
    String status;
    long date;
    String paymentMethod;

    public BookingData(String pnr, String type, String title, String details,
                       double amount, String status, long date, String paymentMethod) {
        this.pnr = pnr;
        this.type = type;
        this.title = title;
        this.details = details;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }

    public String getPnr() { return pnr; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public long getDate() { return date; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getBookingId() { return "BRG-" + pnr; }
}