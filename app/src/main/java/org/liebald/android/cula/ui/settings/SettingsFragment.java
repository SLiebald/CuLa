package org.liebald.android.cula.ui.settings;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.liebald.android.cula.R;
import org.liebald.android.cula.data.database.LanguageEntry;
import org.liebald.android.cula.utilities.InjectorUtils;

import java.util.List;

/**
 * The settings/preferences Fragment for configuration of the app.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    public static final String LANGUAGE_CHANGED_KEY = "language_changed";

    /**
     * The {@link SharedPreferences} used to store the preferences.
     */
    SharedPreferences sharedPreferences;

    /**
     * The {@link SettingsFragmentViewModel} for the settings.
     */
    private SettingsFragmentViewModel mViewModel;

    /**
     * The {@link ListPreference} for selecting the current language.
     */
    private ListPreference mLanguageListPreference;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_settings);
        if (getActivity() != null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLanguageListPreference = (ListPreference) findPreference(getResources().getString(R.string.settings_languages_key));

        if (getContext() != null) {
            SettingsViewModelFactory factory = InjectorUtils.provideLanguageViewModelFactory(getContext());
            mViewModel = ViewModelProviders.of(getActivity(), factory).get(SettingsFragmentViewModel.class);
        }
        mViewModel.getLanguageEntries().observe(this, this::updateLanguageList);

        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_languages_key));

    }

    private void updateLanguageList(List<LanguageEntry> languageEntries) {
        String[] entryValues = new String[languageEntries.size()];
        for (int i = 0; i < languageEntries.size(); i++) {
            entryValues[i] = languageEntries.get(i).getLanguage();
        }
        if (languageEntries.size() == 0)
            return;
        mLanguageListPreference.setEntries(entryValues);
//        mLanguageListPreference.setDefaultValue(entryValues[0]);
        mLanguageListPreference.setEntryValues(entryValues);
        mLanguageListPreference.setSummary(mLanguageListPreference.getValue());
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        //check
        if (preference instanceof ListPreference && key.equals(getResources().getString(R.string.settings_languages_key))) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(sharedPreferences.getString(key, getResources().getString(R.string.settings_languages_default)));
            if (index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);
                Log.d(TAG, "SharedPreferences was set to: " + listPreference.getEntries()[index]);
            }
            sharedPreferences.edit().putBoolean(LANGUAGE_CHANGED_KEY, true).apply();

        } else {
            //todo: implement logic for other preferences
            preference.setSummary(sharedPreferences.getString(key, key));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
