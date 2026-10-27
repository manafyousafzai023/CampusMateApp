package com.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Shows the logo and app name for two seconds, then opens the Login screen.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2000L;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable openLogin = new Runnable() {
        @Override
        public void run() {
            // Explicit intent: we name the exact Activity class to open
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish(); // remove splash from the back stack
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(openLogin, SPLASH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(openLogin);
        super.onDestroy();
    }
}
