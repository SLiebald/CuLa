package com.sliebald.cula.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.pojos.StatisticsActivityEntry;
import com.sliebald.cula.data.database.pojos.StatisticsLibraryWordCount;
import com.sliebald.cula.ui.updateLibrary.UpdateLibraryFragment;
import com.sliebald.cula.utilities.InjectorUtils;

import java.util.List;

/**
 * {@link ViewModel}  for the {@link UpdateLibraryFragment}.
 */
public class StatisticsViewModel extends ViewModel {

    private final LiveData<List<StatisticsLibraryWordCount>> libraryCount;
    private final LiveData<List<StatisticsActivityEntry>> activity;

    /**
     * Constructor.
     */
    public StatisticsViewModel() {
        CulaRepository mRepository = InjectorUtils.provideRepository();
        libraryCount = mRepository.getStatisticsLibraryCountByKnowledgeLevel();
        activity = mRepository.getStatisticsActivity();
    }

    LiveData<List<StatisticsLibraryWordCount>> getLibraryCount() {
        return libraryCount;
    }

    public LiveData<List<StatisticsActivityEntry>> getActivity() {
        return activity;
    }
}
