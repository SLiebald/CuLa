package com.sliebald.cula.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.database.entities.LanguageEntry;
import com.sliebald.cula.utilities.InjectorUtils;

import java.util.List;

public class SettingsViewModel extends ViewModel {

    private final LiveData<List<LanguageEntry>> mLanguageEntries;

    /**
     * Constructor of the ViewModel.
     */
    public SettingsViewModel() {
        mLanguageEntries = InjectorUtils.provideRepository().getAllLanguageEntries();
    }

    LiveData<List<LanguageEntry>> getLanguageEntries() {
        return mLanguageEntries;
    }

}
