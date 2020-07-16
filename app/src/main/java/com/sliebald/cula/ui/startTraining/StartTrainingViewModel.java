package com.sliebald.cula.ui.startTraining;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.data.database.pojos.TrainingData;
import com.sliebald.cula.ui.updateLibrary.UpdateLibraryFragment;
import com.sliebald.cula.utilities.InjectorUtils;

import java.util.List;

/**
 * {@link ViewModel} for the {@link UpdateLibraryFragment}.
 */
public class StartTrainingViewModel extends ViewModel {

    private final LiveData<List<LessonEntry>> mLessonEntries;

    private final CulaRepository mRepository;

    /**
     * Constructor.
     */
    public StartTrainingViewModel() {
        mRepository = InjectorUtils.provideRepository();
        mLessonEntries = mRepository.getAllLessonEntries();
    }

    /**
     * Returns all {@link LessonEntry}s for the current Language.
     *
     * @return The {@link LiveData} wrapped {@link List} of {@link LessonEntry}s.
     */
    LiveData<List<LessonEntry>> getLessonEntries() {
        return mLessonEntries;
    }

    LiveData<TrainingData> getTrainingData(int mAmount, double
            mMinKnowledgeLevel, double mMaxKnowledgeLevel, int mLessonId, boolean reverseTraining) {

        LiveData<List<LibraryEntry>> libraryEntries = mLessonId < 0
                ? mRepository.getTrainingEntries(mAmount, mMinKnowledgeLevel, mMaxKnowledgeLevel)
                : mRepository.getTrainingEntries(mAmount, mMinKnowledgeLevel, mMaxKnowledgeLevel, mLessonId);
        return Transformations.map(libraryEntries, entries -> new TrainingData(mLessonId, reverseTraining, libraryEntries.getValue()));
    }
}
