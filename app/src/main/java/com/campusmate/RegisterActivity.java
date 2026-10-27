package com.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Registration with name, email, password and confirm password.
 * On success the new email is returned to the Login screen.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etName, etEmail, etPassword, etConfirm;
    private UserStore userStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userStore = new UserStore(this);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirm);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        ValidationUtils.clearErrorOnType(tilName);
        ValidationUtils.clearErrorOnType(tilEmail);
        ValidationUtils.clearErrorOnType(tilPassword);
        ValidationUtils.clearErrorOnType(tilConfirm);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnGoLogin).setOnClickListener(v -> finish());
        findViewById(R.id.btnRegister).setOnClickListener(v -> attemptRegister());

        etConfirm.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister();
                return true;
            }
            return false;
        });
    }

    private void attemptRegister() {
        String name = ValidationUtils.text(etName);
        String email = ValidationUtils.text(etEmail);
        String password = ValidationUtils.raw(etPassword);
        String confirm = ValidationUtils.raw(etConfirm);

        EditText firstInvalid = null;

        // Name
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.err_name_required));
            firstInvalid = etName;
        } else if (!ValidationUtils.isValidName(name)) {
            tilName.setError(getString(R.string.err_name_invalid));
            firstInvalid = etName;
        }

        // Email
        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.err_email_required));
            if (firstInvalid == null) firstInvalid = etEmail;
        } else if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.err_email_invalid));
            if (firstInvalid == null) firstInvalid = etEmail;
        } else if (userStore.emailExists(email)) {
            tilEmail.setError(getString(R.string.err_email_taken));
            if (firstInvalid == null) firstInvalid = etEmail;
        }

        // Password
        String passwordError = ValidationUtils.passwordError(this, password);
        if (passwordError != null) {
            tilPassword.setError(passwordError);
            if (firstInvalid == null) firstInvalid = etPassword;
        }

        // Confirm password
        if (confirm.isEmpty()) {
            tilConfirm.setError(getString(R.string.err_confirm_required));
            if (firstInvalid == null) firstInvalid = etConfirm;
        } else if (!confirm.equals(password)) {
            tilConfirm.setError(getString(R.string.err_password_mismatch));
            if (firstInvalid == null) firstInvalid = etConfirm;
        }

        if (firstInvalid != null) {
            firstInvalid.requestFocus();
            return;
        }

        userStore.register(name, email, password);

        Intent result = new Intent();
        result.putExtra(LoginActivity.EXTRA_PREFILL_EMAIL, email.toLowerCase());
        setResult(RESULT_OK, result);
        finish();
    }
}
