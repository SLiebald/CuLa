package com.sliebald.cula.ui.library;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.utilities.InjectorUtils;
import com.sliebald.cula.utilities.SortUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link ViewModel} for the {@link LibraryFragment}.
 */
public class LibraryViewModel extends ViewModel {

    private final MediatorLiveData<List<LibraryEntry>> mLibraryEntries;
    private final CulaRepository mCulaRepository;
    private LibraryEntry mLatestDeletedEntry = null;

    private Comparator<LibraryEntry> mComparator;

    private boolean mCurrentSortOrder;
    private SortUtils.SortType mCurrentSortType;

    /**
     * Constructor of the ViewModel.
     */
    public LibraryViewModel() {
        mCulaRepository = InjectorUtils.provideRepository();
        mLibraryEntries = new MediatorLiveData<>();
        mCurrentSortOrder = true;
        mCurrentSortType = SortUtils.SortType.KNOWLEDGE_LEVEL;
        mComparator = (one, two) -> Double.compare(one.getKnowledgeLevel(), two.getKnowledgeLevel());
        mLibraryEntries.addSource(mCulaRepository.getAllLibraryEntries(), libraryEntries -> {
                    Collections.sort(libraryEntries, mComparator);
                    mLibraryEntries.setValue(libraryEntries);
                }
        );
    }


    /**
     * Sort the library by the given parameter and order.
     *
     * @param sortBy    The parameter to sort by.
     * @param ascending True if sorting ascending, false otherwise.
     */
    void sortLibraryBy(@NonNull SortUtils.SortType sortBy, boolean ascending) {
        mCurrentSortOrder = ascending;
        mCurrentSortType = sortBy;
        switch (sortBy) {
            case ID:
                mComparator = (one, two) -> Integer.compare(one.getId(), two.getId());
                break;
            case NATIVE_WORD:
                mComparator =
                        (one, two) -> one.getNativeWord().toLowerCase().compareTo(two.getNativeWord().toLowerCase());
                break;
            case FOREIGN_WORD:
                mComparator =
                        (one, two) -> one.getForeignWord().toLowerCase().compareTo(two.getForeignWord().toLowerCase());
                break;
            default:
                mComparator =
                        (one, two) -> Double.compare(one.getKnowledgeLevel(),
                                two.getKnowledgeLevel());
        }
        if (!ascending) mComparator = mComparator.reversed();
        List<LibraryEntry> entries = mLibraryEntries.getValue();
        if (entries != null) Collections.sort(entries, mComparator);
        mLibraryEntries.setValue(entries);
    }


    /**
     * Returns all {@link LibraryEntry}s.
     *
     * @return All {@link LibraryEntry}s as {@link List} in {@link LiveData}.
     */
    LiveData<List<LibraryEntry>> getLibraryEntries() {
        return mLibraryEntries;
    }

    /**
     * Remove the selected {@link LibraryEntry} from the database.
     *
     * @param index Index of the selected index.
     */
    void removeLibraryEntry(int index) {
        if (mLibraryEntries == null || mLibraryEntries.getValue() == null) return;
        if (index < mLibraryEntries.getValue().size()) {
            mLatestDeletedEntry = mLibraryEntries.getValue().get(index);
            Log.d(LibraryViewModel.class.getSimpleName(), mLatestDeletedEntry.toString());
            mCulaRepository.deleteLibraryEntry(mLatestDeletedEntry);
        }
    }

    /**
     * Restore the latest deleted entry.
     */
    void restoreLatestDeletedLibraryEntry() {
        mCulaRepository.insertLibraryEntry(mLatestDeletedEntry);
    }

    /**
     * Returns the active sortOrder.
     *
     * @return True for ascending, false for descending.
     */
    boolean getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    /**
     * Type the library is currently sorted by.
     *
     * @return SortType
     */
    SortUtils.SortType getCurrentSortType() {
        return mCurrentSortType;
    }
}
