package com.campusmate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Shows the signed-in user's details, a dark-theme switch and a Logout button.
 */
public class ProfileActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "com.campusmate.extra.PROFILE_EMAIL";

    private UserStore userStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userStore = new UserStore(this);
        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        if (email == null) {
            email = userStore.getSessionEmail();
        }
        if (email == null) {
            goToLogin(null);
            return;
        }

        setContentView(R.layout.activity_profile);

        String name = userStore.getName(email);
        String username = UserStore.usernameOf(email);
        String accountType = getString(userStore.isDemoAccount(email)
                ? R.string.profile_type_demo
                : R.string.profile_type_registered);

        ((TextView) findViewById(R.id.tvAvatar)).setText(UserStore.initials(name));
        ((TextView) findViewById(R.id.tvName)).setText(name);
        ((TextView) findViewById(R.id.tvEmail)).setText(email);
        ((TextView) findViewById(R.id.tvInfoName)).setText(name);
        ((TextView) findViewById(R.id.tvInfoUsername)).setText(username);
        ((TextView) findViewById(R.id.tvInfoEmail)).setText(email);
        ((TextView) findViewById(R.id.tvInfoType)).setText(accountType);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());

        // Dark theme switch: lets you see the values-night colors without changing system settings
        MaterialSwitch switchDark = findViewById(R.id.switchDarkTheme);
        switchDark.setChecked(isNightModeActive());
        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            userStore.setNightMode(mode);
            AppCompatDelegate.setDefaultNightMode(mode);
        });
    }

    private boolean isNightModeActive() {
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return uiMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.action_logout, (dialog, which) -> {
                    userStore.endSession();
                    goToLogin(getString(R.string.msg_logged_out));
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /** Clears the whole back stack so Back cannot return to the dashboard after logout. */
    private void goToLogin(String message) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (message != null) {
            intent.putExtra(LoginActivity.EXTRA_MESSAGE, message);
        }
        startActivity(intent);
        finish();
    }
}
