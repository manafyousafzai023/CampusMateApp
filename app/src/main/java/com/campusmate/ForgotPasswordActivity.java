package com.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Accepts an email and simulates sending a recovery link
 * (short delay + dialog), then opens Reset Password.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private static final long SIMULATED_DELAY_MS = 1500L;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnSend;
    private View sendingRow;
    private UserStore userStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userStore = new UserStore(this);

        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        btnSend = findViewById(R.id.btnSend);
        sendingRow = findViewById(R.id.sendingRow);

        ValidationUtils.clearErrorOnType(tilEmail);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnBackToLogin).setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> attemptSend());

        etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptSend();
                return true;
            }
            return false;
        });
    }

    private void attemptSend() {
        final String email = ValidationUtils.text(etEmail).toLowerCase();

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.err_email_required));
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.err_email_invalid));
            return;
        }
        if (!userStore.emailExists(email)) {
            tilEmail.setError(getString(R.string.err_email_not_found));
            return;
        }

        setSending(true);
        handler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            setSending(false);
            showLinkSentDialog(email);
        }, SIMULATED_DELAY_MS);
    }

    private void setSending(boolean sending) {
        btnSend.setEnabled(!sending);
        etEmail.setEnabled(!sending);
        sendingRow.setVisibility(sending ? View.VISIBLE : View.GONE);
    }

    private void showLinkSentDialog(final String email) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.forgot_dialog_title)
                .setMessage(getString(R.string.forgot_dialog_message, email))
                .setCancelable(false)
                .setPositiveButton(R.string.action_reset_now, (dialog, which) -> {
                    Intent intent = new Intent(this, ResetPasswordActivity.class);
                    intent.putExtra(ResetPasswordActivity.EXTRA_EMAIL, email);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
