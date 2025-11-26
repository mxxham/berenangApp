package com.example.berenang10;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final long MIN_DISPLAY_MS = 3000L;
    private static final long EXIT_ANIMATION_DURATION_MS = 300L;
    private static final long ENTRANCE_ANIMATION_DURATION_MS = 400L; // New duration for entrance
    private static final List<Integer> LETTER_IDS = Arrays.asList(
            R.id.letter_b, R.id.letter_e1, R.id.letter_r, R.id.letter_e2,
            R.id.letter_n1, R.id.letter_a, R.id.letter_n2, R.id.letter_g
    );

    private AnimatorSet overallWaveAnimator;
    private boolean isAppReady = false;
    private long startTime;
    private View textContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startTime = System.currentTimeMillis();
        textContainer = findViewById(R.id.text_container_wave);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        applyCustomFont();

        // Start with the entrance animation. The wave animation starts after this completes.
        startEntranceAnimation();

        // SIMULATE BACKGROUND LOADING TASK
        View rootView = findViewById(android.R.id.content);
        rootView.postDelayed(() -> {
            isAppReady = true;
            goToMainActivity();
        }, MIN_DISPLAY_MS);
    }

    /**
     * Finds all TextViews and applies the custom Typeface.
     */
    private void applyCustomFont() {
        Typeface customFont = ResourcesCompat.getFont(this, R.font.rethink_sans_bold);

        // Apply the font to each TextView using the centralized list
        if (customFont != null) {
            for (int id : LETTER_IDS) {
                TextView letter = findViewById(id);
                if (letter != null) {
                    letter.setTypeface(customFont);
                }
            }
        }
    }

    /**
     * Handles the initial fade-in and scale-up animation for the text container.
     */
    private void startEntranceAnimation() {
        if (textContainer == null) {
            overallWaveAnimator = startWaveAnimation();
            return;
        }

        // Fade In (alpha: 0.0 -> 1.0)
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textContainer, "alpha", 0.0f, 1.0f);

        // Scale Up (scaleX/Y: 0.8 -> 1.0)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textContainer, "scaleX", 0.8f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textContainer, "scaleY", 0.8f, 1.0f);

        AnimatorSet entranceSet = new AnimatorSet();
        entranceSet.playTogether(fadeIn, scaleX, scaleY);
        entranceSet.setDuration(ENTRANCE_ANIMATION_DURATION_MS);

        entranceSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Once the entrance is complete, start the repeating wave
                overallWaveAnimator = startWaveAnimation();
            }
        });

        entranceSet.start();
    }

    private AnimatorSet startWaveAnimation() {
        List<TextView> letters = new ArrayList<>();
        // Robustly check for and add each letter using the centralized list
        for (int id : LETTER_IDS) {
            TextView letter = findViewById(id);
            if (letter != null) {
                letters.add(letter);
            }
        }

        if (letters.isEmpty()) {
            return null;
        }

        List<Animator> letterAnimators = createLetterWaveAnimators(letters, 100L);

        AnimatorSet overallWave = new AnimatorSet();
        overallWave.playTogether(letterAnimators);

        // Listener to repeat the animation until the minimum time has passed
        overallWave.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime < MIN_DISPLAY_MS) {
                    // Time hasn't passed, restart the animation
                    animation.start();
                } else if (isAppReady) {
                    // Animation finished its cycle and app is ready, go to main.
                    goToMainActivity();
                }
            }
        });

        overallWave.start();
        return overallWave;
    }

    /**
     * Helper to create individual, delayed, repeating wave animators for each letter.
     */
    private List<Animator> createLetterWaveAnimators(List<TextView> letters, long delayBetweenLetters) {
        List<Animator> letterAnimators = new ArrayList<>();

        for (int i = 0; i < letters.size(); i++) {
            TextView currentLetter = letters.get(i);

            // Load the wave_cycle animator
            AnimatorSet letterCycle = (AnimatorSet) AnimatorInflater.loadAnimator(
                    this,
                    R.animator.wave_cycle
            );

            letterCycle.setTarget(currentLetter);

            // Ensure individual animation components repeat infinitely
            for (Animator anim : letterCycle.getChildAnimations()) {
                if (anim instanceof ObjectAnimator) {
                    ((ObjectAnimator) anim).setRepeatCount(ObjectAnimator.INFINITE);
                }
            }

            letterCycle.setStartDelay(i * delayBetweenLetters);
            letterAnimators.add(letterCycle);
        }
        return letterAnimators;
    }


    private void goToMainActivity() {
        // Only transition if the minimum time has passed AND the background task is done
        if (System.currentTimeMillis() - startTime >= MIN_DISPLAY_MS && isAppReady) {
            if (isFinishing()) return;

            // 1. Cancel the repeating wave animator immediately
            if (overallWaveAnimator != null) {
                overallWaveAnimator.cancel();
            }

            // 2. Start the smooth fade-out exit animation
            if (textContainer != null) {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textContainer, "alpha", 1f, 0f);
                fadeOut.setDuration(EXIT_ANIMATION_DURATION_MS);

                // 3. Start the next activity after the animation finishes
                fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                fadeOut.start();
            } else {
                // Fallback for immediate transition
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (overallWaveAnimator != null) {
            overallWaveAnimator.cancel();
        }
    }
}