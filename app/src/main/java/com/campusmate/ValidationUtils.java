package com.campusmate;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Shared input-validation rules so every screen validates the same way.
 */
public final class ValidationUtils {

    public static final int MIN_PASSWORD_LENGTH = 8;

    private ValidationUtils() {
    }

    /** Trimmed text of a field (use for names, emails). */
    public static String text(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    /** Raw text of a field (use for passwords, where spaces matter). */
    public static String raw(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }

    public static boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        return name.length() >= 3 && name.matches("[\\p{L} .'-]+");
    }

    /** @return an error message, or null when the password is acceptable. */
    @Nullable
    public static String passwordError(Context context, String password) {
        if (password.isEmpty()) {
            return context.getString(R.string.err_password_required);
        }
        if (password.contains(" ")) {
            return context.getString(R.string.err_password_spaces);
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return context.getString(R.string.err_password_short, MIN_PASSWORD_LENGTH);
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            return context.getString(R.string.err_password_weak);
        }
        return null;
    }

    /** Removes a field's error as soon as the user starts typing again. */
    public static void clearErrorOnType(final TextInputLayout layout) {
        EditText editText = layout.getEditText();
        if (editText == null) {
            return;
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
