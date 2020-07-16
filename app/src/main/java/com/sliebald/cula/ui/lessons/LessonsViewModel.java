package com.sliebald.cula.ui.lessons;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.utilities.InjectorUtils;
import com.sliebald.cula.utilities.SortUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link ViewModel} for the {@link LessonsFragment}.
 */
public class LessonsViewModel extends ViewModel {

    private final MediatorLiveData<List<LessonEntry>> mLessonEntries;
    private final CulaRepository mCulaRepository;

    private Comparator<LessonEntry> mComparator;

    private boolean mCurrentSortOrder;
    private SortUtils.SortType mCurrentSortType;

    /**
     * Constructor of the ViewModel.
     */
    public LessonsViewModel() {
        mCulaRepository = InjectorUtils.provideRepository();
        mCurrentSortOrder = true;
        mCurrentSortType = SortUtils.SortType.NAME;
        mLessonEntries = new MediatorLiveData<>();
        mComparator = (one, two) -> one.getLessonName().compareTo(two.getLessonName());

        mLessonEntries.addSource(mCulaRepository.getAllLessonEntries(), libraryEntries -> {
                    Collections.sort(libraryEntries, mComparator);
                    mLessonEntries.setValue(libraryEntries);
                }
        );
    }

    /**
     * Sort the lesson by the given parameters and order.
     *
     * @param sortBy    The parameter to sort by.
     * @param ascending True if sorting ascending, false otherwise.
     */
    void sortLessonsBy(SortUtils.SortType sortBy, boolean ascending) {
        mCurrentSortOrder = ascending;
        mCurrentSortType = sortBy;
        mComparator = sortBy == SortUtils.SortType.ID
                ? ((one, two) -> Integer.compare(one.getId(), two.getId()))
                : ((one, two) -> one.getLessonName().toLowerCase().compareTo(two.getLessonName().toLowerCase()));
        if (!ascending) mComparator = mComparator.reversed();

        List<LessonEntry> entries = mLessonEntries.getValue();
        if (entries != null) Collections.sort(entries, mComparator);

        mLessonEntries.setValue(entries);
    }


    /**
     * Returns all {@link LessonEntry}s.
     *
     * @return All {@link LessonEntry}s as {@link List} in {@link LiveData}.
     */
    LiveData<List<LessonEntry>> getLessonEntries() {
        return mLessonEntries;
    }


    /**
     * Remove the selected {@link LessonEntry} from the database.
     *
     * @param index Index of the selected index.
     */
    void removeLessonEntry(int index) {
        if (mLessonEntries == null || mLessonEntries.getValue() == null)
            return;
        LessonEntry latestDeletedEntry = mLessonEntries.getValue().get(index);
        Log.d(LessonsViewModel.class.getSimpleName(), latestDeletedEntry.toString());
        mCulaRepository.deleteLessonEntry(latestDeletedEntry);
    }

//    /**
//     * Restore the latest deleted entry.
//     */
    //TODO: implement working version
//    public void restoreLatestDeletedLessonEntry() {
//        //TODO: currently the lesson mappings are still gone.
//        CulaRepository.OnLessonEntryAddedListener dummyListener = ids -> {
//        };
//        mCulaRepository.insertLessonEntry(dummyListener, latestDeletedEntry);
//    }

    /**
     * Returns the active sortOrder.
     *
     * @return True for ascending, false for descending.
     */
    boolean getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    /**
     * Type the lessons are currently sorted by.
     *
     * @return SortType
     */
    SortUtils.SortType getCurrentSortType() {
        return mCurrentSortType;
    }

}
