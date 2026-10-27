package com.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Date;

/**
 * Home screen: greeting plus the main modules shown as cards in a GridLayout.
 */
public class DashboardActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "com.campusmate.extra.EMAIL";

    private UserStore userStore;
    private String email;
    private TextView tvGreeting;
    private TextView tvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userStore = new UserStore(this);
        email = getIntent().getStringExtra(EXTRA_EMAIL);
        if (email == null) {
            email = userStore.getSessionEmail();
        }
        if (email == null) {
            // Not logged in: go back to Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_dashboard);

        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(new Date()));

        tvGreeting = findViewById(R.id.tvGreeting);
        tvAvatar = findViewById(R.id.tvAvatar);
        tvAvatar.setOnClickListener(v -> openProfile());

        findViewById(R.id.cardProfile).setOnClickListener(v -> openProfile());
        setModuleClick(R.id.cardCourses, R.string.module_courses);
        setModuleClick(R.id.cardTimetable, R.string.module_timetable);
        setModuleClick(R.id.cardAttendance, R.string.module_attendance);
        setModuleClick(R.id.cardResults, R.string.module_results);
        setModuleClick(R.id.cardNotices, R.string.module_notices);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvGreeting == null) {
            return;
        }
        String name = userStore.getName(email);
        String firstName = name.isEmpty() ? UserStore.usernameOf(email) : name.split("\\s+")[0];
        tvGreeting.setText(getString(R.string.dashboard_greeting, firstName));
        tvAvatar.setText(UserStore.initials(name));
    }

    private void setModuleClick(int cardId, @StringRes final int titleRes) {
        findViewById(cardId).setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.msg_module_opened, getString(titleRes)),
                        Toast.LENGTH_SHORT).show());
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_EMAIL, email);
        startActivity(intent);
    }
}
