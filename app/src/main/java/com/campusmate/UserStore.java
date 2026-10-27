package com.campusmate;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

/**
 * Simulated local "backend". Accounts are kept in SharedPreferences so that
 * registration and password resets work without a server.
 * A hardcoded demo account is created on first launch.
 *
 * NOTE: passwords are stored in plain text because this is a classroom simulation only.
 */
public class UserStore {

    public static final String DEMO_NAME = "Demo Student";
    public static final String DEMO_EMAIL = "student@campusmate.edu";
    public static final String DEMO_PASSWORD = "Student@123";

    private static final String PREFS = "campusmate_store";
    private static final String KEY_NAME = "name_";
    private static final String KEY_PASS = "pass_";
    private static final String KEY_SESSION = "session_email";
    private static final String KEY_NIGHT_MODE = "night_mode";

    private final SharedPreferences prefs;

    public UserStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_PASS + DEMO_EMAIL)) {
            prefs.edit()
                    .putString(KEY_NAME + DEMO_EMAIL, DEMO_NAME)
                    .putString(KEY_PASS + DEMO_EMAIL, DEMO_PASSWORD)
                    .apply();
        }
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    public boolean emailExists(String email) {
        return prefs.contains(KEY_PASS + normalize(email));
    }

    /** @return false if the email is already registered. */
    public boolean register(String name, String email, String password) {
        String key = normalize(email);
        if (emailExists(key)) {
            return false;
        }
        prefs.edit()
                .putString(KEY_NAME + key, name.trim())
                .putString(KEY_PASS + key, password)
                .apply();
        return true;
    }

    /** Resolves an email address or a username (the part before '@') to a registered email. */
    @Nullable
    public String findEmail(String identifier) {
        String id = normalize(identifier);
        if (id.isEmpty()) {
            return null;
        }
        if (id.contains("@")) {
            return emailExists(id) ? id : null;
        }
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(KEY_PASS)) {
                String email = key.substring(KEY_PASS.length());
                if (usernameOf(email).equals(id)) {
                    return email;
                }
            }
        }
        return null;
    }

    /** @return the account's email if the credentials match, otherwise null. */
    @Nullable
    public String authenticate(String identifier, String password) {
        String email = findEmail(identifier);
        if (email == null) {
            return null;
        }
        return isCurrentPassword(email, password) ? email : null;
    }

    public boolean isCurrentPassword(String email, String password) {
        String saved = prefs.getString(KEY_PASS + normalize(email), null);
        return saved != null && saved.equals(password);
    }

    public void updatePassword(String email, String newPassword) {
        prefs.edit().putString(KEY_PASS + normalize(email), newPassword).apply();
    }

    public String getName(String email) {
        return prefs.getString(KEY_NAME + normalize(email), "");
    }

    public boolean isDemoAccount(String email) {
        return DEMO_EMAIL.equals(normalize(email));
    }

    // ----- Session -----

    public void startSession(String email) {
        prefs.edit().putString(KEY_SESSION, normalize(email)).apply();
    }

    @Nullable
    public String getSessionEmail() {
        return prefs.getString(KEY_SESSION, null);
    }

    public void endSession() {
        prefs.edit().remove(KEY_SESSION).apply();
    }

    // ----- Theme -----

    public int getNightMode() {
        return prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setNightMode(int mode) {
        prefs.edit().putInt(KEY_NIGHT_MODE, mode).apply();
    }

    // ----- Helpers -----

    public static String usernameOf(String email) {
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    public static String initials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(parts[0].charAt(0)));
        if (parts.length > 1) {
            sb.append(Character.toUpperCase(parts[parts.length - 1].charAt(0)));
        }
        return sb.toString();
    }
}
