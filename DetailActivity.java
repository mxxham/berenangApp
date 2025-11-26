package com.example.berenang10;

import android.content.Intent; // Needed for the share button functionality
import android.os.Bundle;
import android.view.View; // <--- ADD THIS IMPORT
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // --- Setup Toolbar ---
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Get Data from Intent ---
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            // Handle case where no data is passed
            finish();
            return;
        }

        String title = extras.getString("ITEM_TITLE", "Title Not Found");
        String subtitle = extras.getString("ITEM_SUBTITLE", "Subtitle Not Found");
        String imageUrl = extras.getString("ITEM_URL");

        // --- Find Views ---
        TextView detailTitle = findViewById(R.id.detail_title);
        TextView detailSubtitle = findViewById(R.id.detail_subtitle);
        ImageView detailImage = findViewById(R.id.detail_image);

        // --- Set Data ---
        detailTitle.setText(title);
        detailSubtitle.setText(subtitle);

        // --- Load Image using Glide ---
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(detailImage);
        } else {
            // If URL is null or empty, hide the image or set a default
            detailImage.setVisibility(View.GONE); // Fix applied here by importing View
        }

        // Example feature: Handle a share button click
        findViewById(R.id.share_button).setOnClickListener(v -> {
            shareItemDetails(title, subtitle);
        });
    }

    // Example Share Feature Implementation
    private void shareItemDetails(String title, String subtitle) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBody = "Check out this place: " + title + " (" + subtitle + ").";
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}