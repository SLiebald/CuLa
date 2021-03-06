package com.sliebald.cula.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

public class KeyboardUtils {

    /**
     * Hide keyboard.
     *
     * @param context Context of calling fragment/activity.
     * @param view    relevant view that provides the windowtoken.
     */
    public static void hideKeyboard(@NonNull Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus();
    }
}
