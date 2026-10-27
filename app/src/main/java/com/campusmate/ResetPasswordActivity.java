package com.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * New password + confirm password with validation.
 * Saves the new password locally and returns to Login.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "com.campusmate.extra.RESET_EMAIL";

    private TextInputLayout tilNewPassword, tilConfirmPassword;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private UserStore userStore;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        email = getIntent().getStringExtra(EXTRA_EMAIL);
        if (email == null) {
            finish(); // this screen only makes sense after Forgot Password
            return;
        }

        setContentView(R.layout.activity_reset_password);
        userStore = new UserStore(this);

        TextView subtitle = findViewById(R.id.tvResetSubtitle);
        subtitle.setText(getString(R.string.reset_subtitle, email));

        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        ValidationUtils.clearErrorOnType(tilNewPassword);
        ValidationUtils.clearErrorOnType(tilConfirmPassword);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnReset).setOnClickListener(v -> attemptReset());

        etConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptReset();
                return true;
            }
            return false;
        });
    }

    private void attemptReset() {
        String newPassword = ValidationUtils.raw(etNewPassword);
        String confirm = ValidationUtils.raw(etConfirmPassword);
        boolean valid = true;

        String error = ValidationUtils.passwordError(this, newPassword);
        if (error == null && userStore.isCurrentPassword(email, newPassword)) {
            error = getString(R.string.err_password_same);
        }
        if (error != null) {
            tilNewPassword.setError(error);
            valid = false;
        }

        if (confirm.isEmpty()) {
            tilConfirmPassword.setError(getString(R.string.err_confirm_required));
            valid = false;
        } else if (!confirm.equals(newPassword)) {
            tilConfirmPassword.setError(getString(R.string.err_password_mismatch));
            valid = false;
        }

        if (!valid) {
            return;
        }

        userStore.updatePassword(email, newPassword);

        // Return to the existing Login screen and clear Forgot/Reset from the back stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(LoginActivity.EXTRA_PREFILL_EMAIL, email);
        intent.putExtra(LoginActivity.EXTRA_MESSAGE, getString(R.string.msg_password_reset));
        startActivity(intent);
        finish();
    }
}
