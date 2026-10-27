package com.campusmate;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Applies the saved theme choice (system / light / dark) before any screen is shown.
 */
public class CampusMateApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(new UserStore(this).getNightMode());
    }
}
