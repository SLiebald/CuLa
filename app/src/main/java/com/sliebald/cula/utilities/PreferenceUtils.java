package com.sliebald.cula.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sliebald.cula.MyApplication;
import com.sliebald.cula.R;

import java.util.Objects;

public class PreferenceUtils {

    /**
     * Returns by how much the Knowledgelevel of a correctly trained word should be incremented.
     *
     * @return Double that should be added to the Knowledgelevel of the trained word.
     */
    static double getKnowledgeIncrementCorrectTraining() {
        Context context = MyApplication.Companion.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(context.getString(R.string.
                settings_reward_correct_training_key), 5) / 10D;
    }

    /**
     * Returns by how much the Knowledgelevel of a wrongly trained word should be decremented.
     *
     * @return Double that should be removed from the Knowledgelevel of the trained word.
     */
    static double getKnowledgeDecrementWrongTraining() {
        Context context = MyApplication.Companion.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(context.getString(R.string.
                settings_punish_wrong_training_key), 5) / 10D;
    }

    /**
     * Returns the currently active language.
     *
     * @return The currently active language.
     */
    public static String getActiveLanguage() {
        Context context = MyApplication.Companion.getContext();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString
                (R.string.settings_select_language_key), "");
    }

    /**
     * Sets the currently active language.
     *
     * @param language The currently active language.
     */
    public static void setActiveLanguage(String language) {
        Context context = MyApplication.Companion.getContext();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getResources().getString(R.string
                .settings_select_language_key), language);
        editor.apply();
    }

    /**
     * Returns the default knowledgelevel, as set in the settings.
     *
     * @return The default knowledgelevel as double.
     */
    public static double getDefaultKnowledgeLevel() {
        Context context = MyApplication.Companion.getContext();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        double result;
        try {
            result = Double.parseDouble(Objects.requireNonNull(preferences.getString(context.getString(R.string
                    .settings_default_knowledgeLevel_key), context.getString
                    (R.string.knowledgeLevel3_value))));
        } catch (NumberFormatException e) {
            result = 2.5;
        }
        return result;
    }
}
