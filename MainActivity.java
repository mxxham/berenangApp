package com.example.berenang10;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.SharedPreferences; // Required for accessing user state

// FIX: Implement the UserStateProvider interface
public class MainActivity extends AppCompatActivity implements UserStateProvider {

    // Define the name of your SharedPreferences file and key for consistency
    private static final String PREFS_NAME = "BerenangPrefs";
    private static final String USER_EMAIL_KEY = "current_user_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    } else if (itemId == R.id.nav_bookings) {
                        selectedFragment = new BookingsFragment();
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };

    // FIX: Implement the required method from UserStateProvider
    @Override
    public String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // This retrieves the stored email used throughout the app for identifying the user.
        return prefs.getString(USER_EMAIL_KEY, null);
    }
}