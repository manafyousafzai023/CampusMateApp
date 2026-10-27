package com.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Login with email or username + password.
 * Links to Register and Forgot Password; opens the Dashboard on success.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_PREFILL_EMAIL = "com.campusmate.extra.PREFILL_EMAIL";
    public static final String EXTRA_MESSAGE = "com.campusmate.extra.MESSAGE";

    private TextInputLayout tilIdentifier;
    private TextInputLayout tilPassword;
    private TextInputEditText etIdentifier;
    private TextInputEditText etPassword;
    private UserStore userStore;

    // Register screen returns the new email so we can pre-fill it
    private final ActivityResultLauncher<Intent> registerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    etIdentifier.setText(result.getData().getStringExtra(EXTRA_PREFILL_EMAIL));
                    etPassword.setText("");
                    etPassword.requestFocus();
                    Toast.makeText(this, R.string.msg_registered, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userStore = new UserStore(this);

        tilIdentifier = findViewById(R.id.tilIdentifier);
        tilPassword = findViewById(R.id.tilPassword);
        etIdentifier = findViewById(R.id.etIdentifier);
        etPassword = findViewById(R.id.etPassword);

        ValidationUtils.clearErrorOnType(tilIdentifier);
        ValidationUtils.clearErrorOnType(tilPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());

        findViewById(R.id.btnForgot).setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        findViewById(R.id.btnGoRegister).setOnClickListener(v ->
                registerLauncher.launch(new Intent(this, RegisterActivity.class)));

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        handleIncoming(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncoming(intent);
    }

    /** Shows a message / pre-fills the email when we arrive from Reset Password or Logout. */
    private void handleIncoming(Intent intent) {
        if (intent == null) {
            return;
        }
        String email = intent.getStringExtra(EXTRA_PREFILL_EMAIL);
        if (email != null) {
            etIdentifier.setText(email);
            etPassword.setText("");
            etPassword.requestFocus();
        }
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            intent.removeExtra(EXTRA_MESSAGE); // don't repeat it after rotation
        }
    }

    private void attemptLogin() {
        String identifier = ValidationUtils.text(etIdentifier);
        String password = ValidationUtils.raw(etPassword);
        boolean valid = true;

        if (identifier.isEmpty()) {
            tilIdentifier.setError(getString(R.string.err_identifier_required));
            valid = false;
        } else if (identifier.contains("@") && !ValidationUtils.isValidEmail(identifier)) {
            tilIdentifier.setError(getString(R.string.err_email_invalid));
            valid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.err_password_required));
            valid = false;
        }

        if (!valid) {
            return;
        }

        String email = userStore.authenticate(identifier, password);
        if (email == null) {
            tilPassword.setError(getString(R.string.err_invalid_credentials));
            return;
        }

        userStore.startSession(email);
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(DashboardActivity.EXTRA_EMAIL, email);
        startActivity(intent);
        finish(); // Back from the dashboard should not return to login
    }
}
