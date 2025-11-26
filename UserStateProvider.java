package com.example.berenang10;

public interface UserStateProvider {
    /**
     * Retrieves the email or unique ID of the currently logged-in user.
     * This method is implemented by the Host Activity (e.g., MainActivity)
     * to provide state information to its Fragments/Views.
     * @return The user's unique identifier (String), typically their email address.
     */
    String getCurrentUserEmail();
}