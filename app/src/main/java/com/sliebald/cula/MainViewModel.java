package com.sliebald.cula;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.database.Entities.LanguageEntry;
import com.sliebald.cula.utilities.InjectorUtils;


public class MainViewModel extends ViewModel {

    private final LiveData<LanguageEntry> mActiveLanguage;

    /**
     * Constructor of the ViewModel.
     */
    public MainViewModel() {
        mActiveLanguage = InjectorUtils.provideRepository().getActiveLanguage();
    }

    LiveData<LanguageEntry> getActiveLanguage() {
        return mActiveLanguage;
    }

}
