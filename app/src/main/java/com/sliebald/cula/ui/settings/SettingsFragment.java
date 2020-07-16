package com.sliebald.cula.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.google.android.material.snackbar.Snackbar;
import com.sliebald.cula.R;
import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.entities.LanguageEntry;
import com.sliebald.cula.utilities.InjectorUtils;

import java.util.List;

/**
 * The settings/preferences Fragment for configuration of the app.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    /**
     * The {@link SettingsViewModel} for the settings.
     */
    private SettingsViewModel mViewModel;

    /**
     * The {@link ListPreference} for selecting the current language.
     */
    private ListPreference mLanguageListPreference;

    /**
     * The {@link SeekBarPreference} used to configure how much the KnowledgeLevel of a word
     * should be incremented on correct training.
     */
    private SeekBarPreference mCorrectTrainingSeekBarPreference;
    /**
     * The {@link SeekBarPreference} used to configure how much the KnowledgeLevel of a word
     * should be decremented on wrong training.
     */
    private SeekBarPreference mWrongTrainingSeekBarPreference;


    private Preference mDeleteLanguage;
    private CulaRepository mRepository;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // set the correct preference layout
        addPreferencesFromResource(R.xml.fragment_settings);
        mRepository = InjectorUtils.provideRepository();

        // get the ListPreference for the language settings (select, add, delete)
        mLanguageListPreference = findPreference(getResources().getString(R
                .string.settings_select_language_key));
        Preference addLanguage = findPreference(getResources().getString(R.string
                .settings_add_language_key));
        mDeleteLanguage = findPreference(getResources().getString(R.string
                .settings_delete_language_key));

        // set onClick listener for adding/deleting language buttons
        assert addLanguage != null;
        addLanguage.setOnPreferenceClickListener(preference -> {
            showAddLanguageDialog();
            return true;
        });
        mDeleteLanguage.setOnPreferenceClickListener(preference -> {
            deleteLanguageDialog();
            return true;
        });

        // get the ViewModel
        if (getActivity() != null && getContext() != null) {
            mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        }
        //Load the language entries and update the ListPreference
        mViewModel.getLanguageEntries().observe(this, this::updateLanguageList);

        // Set up the SeekBarPreferences for updating how much the KnowledgeLevel of a word should
        // change when trained.
        mCorrectTrainingSeekBarPreference = findPreference(getResources()
                .getString(R.string.settings_reward_correct_training_key));
        mWrongTrainingSeekBarPreference = findPreference(getResources()
                .getString(R.string.settings_punish_wrong_training_key));

        Preference.OnPreferenceChangeListener mTrainingSeekBarPreferenceChangeListener = (preference, newValue) -> {
            //update the summary. Using a SummaryProvider should be the better way but
            // does only  update once reloading the fragment.
            preference.setSummary(getString(R.string
                    .settings_adapt_knowledge_level_summary, ((int) newValue) * 10));
            return true;
        };
        mCorrectTrainingSeekBarPreference.setOnPreferenceChangeListener
                (mTrainingSeekBarPreferenceChangeListener);
        mWrongTrainingSeekBarPreference.setOnPreferenceChangeListener
                (mTrainingSeekBarPreferenceChangeListener);

        // When clicking on the SeekBarPreference the users should get feedback.
        Preference.OnPreferenceClickListener mTrainingSeekBarPreferenceClickListener = preference -> {
            AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
            int changeRate = ((SeekBarPreference) preference).getValue() * 10;
            if (preference.getKey().equals(getString(R.string
                    .settings_reward_correct_training_key))) {
                alertDialog.setTitle(getString(R.string.settings_reward_correct_training_title));
                alertDialog.setMessage(getString(R.string
                        .settings_reward_correct_training_explanation, changeRate));
            } else {
                alertDialog.setTitle(getString(R.string.settings_punish_wrong_training_title));
                alertDialog.setMessage(getString(R.string
                        .settings_punish_wrong_training_explanation, changeRate));
            }
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> dialog.dismiss());
            alertDialog.show();
            return true;
        };
        mCorrectTrainingSeekBarPreference.setOnPreferenceClickListener(mTrainingSeekBarPreferenceClickListener);
        mWrongTrainingSeekBarPreference.setOnPreferenceClickListener(mTrainingSeekBarPreferenceClickListener);

        initializePreferences();
    }

    /**
     * Initialize the preferences. Most Importantly: summaries.
     */
    private void initializePreferences() {
        mCorrectTrainingSeekBarPreference.callChangeListener(mCorrectTrainingSeekBarPreference
                .getValue());
        mWrongTrainingSeekBarPreference.callChangeListener(mWrongTrainingSeekBarPreference
                .getValue());
    }

    /**
     * Shows a dialog and asks the user to write the language he wants to add.
     */
    private void showAddLanguageDialog() {
        Context context = requireContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.settings_add_language_title));
        final EditText newLanguageEditText = new EditText(context);
        newLanguageEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(newLanguageEditText);
        builder.setPositiveButton(R.string.add, (dialog, which) -> addNewLanguageToDb
                (newLanguageEditText.getText().toString()));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Adds a new language to the App.
     */
    private void addNewLanguageToDb(String newLanguage) {
        if (newLanguage == null || newLanguage.length() < 2 || newLanguage.length() > 20) {
            Snackbar snackbar = Snackbar.make(requireView(), R.string
                    .settings_warning_invalid_language_length, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        mRepository.insertLanguageEntry(new LanguageEntry(newLanguage, true));
    }

    /**
     * Deletes the current language from the app, including all words. Asks for confirmation first.
     */
    private void deleteLanguageDialog() {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.settings_delete_language_title_warning,
                mLanguageListPreference.getValue()));
        builder.setMessage(getResources().getString(R.string
                .settings_delete_language_message_warning));
        builder.setPositiveButton(R.string.ok, (dialog, which) -> deleteLanguageFromDb
                (mLanguageListPreference.getValue()));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Deletes the current language from the app, including all words.
     */
    private void deleteLanguageFromDb(String language) {
        CulaRepository repository = InjectorUtils.provideRepository();
        repository.deleteLanguageEntry(new LanguageEntry(language, false));
    }

    /**
     * Updates the {@link ListPreference} when the list of entries was changed in the database.
     *
     * @param languageEntries the new List of entries that should be shown.
     */
    private void updateLanguageList(@NonNull List<LanguageEntry> languageEntries) {
        int selected = 0;
        if (languageEntries.size() == 0) {
            mLanguageListPreference.setSummary(getResources().getString(R.string
                    .settings_select_language_default));
            mLanguageListPreference.setEntries(new String[]{getResources().getString(R.string
                    .settings_select_language_default)});
            mLanguageListPreference.setEnabled(false);
            mDeleteLanguage.setEnabled(false);
        } else {
            String[] entryValues = new String[languageEntries.size()];
            for (int i = 0; i < languageEntries.size(); i++) {
                entryValues[i] = languageEntries.get(i).getLanguage();
                if (languageEntries.get(i).isActive())
                    selected = i;
                Log.d(TAG, "Language: " + languageEntries.get(i).getLanguage() + " active: " +
                        languageEntries.get(i).isActive());
            }
            mDeleteLanguage.setEnabled(true);
            mLanguageListPreference.setEnabled(true);
            mLanguageListPreference.setEntries(entryValues);
            mLanguageListPreference.setValue(entryValues[selected]);
            mLanguageListPreference.setEntryValues(entryValues);
            mLanguageListPreference.setSummary(mLanguageListPreference.getValue());
            Log.d(TAG, "Update LanguageList: " + mLanguageListPreference.getValue() + " amount " +
                    "of languages: " + languageEntries.size());
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        //check which preference changed and update the summary correctly.
        if (preference instanceof ListPreference && key.equals(getResources().getString(R.string
                .settings_select_language_key))) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(sharedPreferences.getString(key,
                    getResources().getString(R.string.settings_select_language_default)));
            if (index >= 0 && index < listPreference.getEntries().length) {
                mRepository.setActiveLanguage(listPreference.getEntries()[index].toString());
                listPreference.setSummary(listPreference.getEntries()[index]);
                listPreference.setValue(listPreference.getEntries()[index].toString());
                Log.d(TAG, "SharedPreferences was set to: " + listPreference.getEntries()[index]);
            }
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
