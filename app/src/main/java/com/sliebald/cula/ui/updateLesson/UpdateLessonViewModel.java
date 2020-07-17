package com.sliebald.cula.ui.updateLesson;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.data.database.entities.LessonMappingEntry;
import com.sliebald.cula.data.database.pojos.MappingPOJO;
import com.sliebald.cula.utilities.InjectorUtils;
import com.sliebald.cula.utilities.PreferenceUtils;
import com.sliebald.cula.utilities.SortUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link ViewModel}  for the {@link UpdateLessonFragment}.
 */
public class UpdateLessonViewModel extends ViewModel implements CulaRepository
        .OnLessonEntryAddedListener {

    /**
     * Tag of this activity.
     */
    private static final String TAG = UpdateLessonFragment.class.getSimpleName();
    private final CulaRepository mCulaRepository;
    private LiveData<LessonEntry> entry;
    private MediatorLiveData<List<MappingPOJO>> mapping;
    private Comparator<MappingPOJO> mComparator;

    private boolean mCurrentSortOrder;
    private SortUtils.SortType mCurrentSortType;

    /**
     * The entryId of an entry that is updated. -1 means a new entry is being edited.
     */
    private MutableLiveData<Integer> lessonId;

    /**
     * Constructor.
     *
     * @param entryId Id of the {@link LessonEntry} which should be loaded.
     */
    @SuppressWarnings("WeakerAccess")
    public UpdateLessonViewModel(int entryId) {
        mCulaRepository = InjectorUtils.provideRepository();
        lessonId = new MutableLiveData<>();
        lessonId.setValue(entryId);
        mapping = new MediatorLiveData<>();
        mCurrentSortOrder = true;
        mCurrentSortType = SortUtils.SortType.NATIVE_WORD;
        mComparator = (one, two) -> one.getNativeWord().compareTo(two.getNativeWord());
        entry = Transformations.switchMap(lessonId, mCulaRepository::getLessonEntry);
        //mCulaRepository.getLessonEntry(entryId);
        mapping.addSource(Transformations.switchMap(lessonId, mCulaRepository::getMappingEntries), libraryEntries -> {
                    Collections.sort(libraryEntries, mComparator);
                    mapping.setValue(libraryEntries);
                }
        );
    }

    /**
     * Returns the current {@link LessonEntry}.
     *
     * @return The {@link LiveData} wrapped {@link LessonEntry}
     */
    LiveData<LessonEntry> getEntry() {
        return entry;
    }

    /**
     * Sort the lesson/library mapping list by the given parameter and order.
     *
     * @param sortBy    The parameter to sort by.
     * @param ascending True if sorting ascending, false otherwise.
     */
    void sortMappingBy(@NonNull SortUtils.SortType sortBy, boolean ascending) {
        Log.d("test", "sortBy: " + sortBy + " " + ascending);
        mCurrentSortOrder = ascending;
        mCurrentSortType = sortBy;
        switch (sortBy) {
            case ID:
                mComparator = (one, two) -> Integer.compare(one.getLibraryId(), two.getLibraryId());
                break;
            case PART_OF_LESSON:
                mComparator =
                        (one, two) -> Boolean.compare(one.isPartOfLesson(), two.isPartOfLesson());
                break;
            case FOREIGN_WORD:
                mComparator =
                        (one, two) -> one.getForeignWord().toLowerCase().compareTo(two.getForeignWord().toLowerCase());
                break;
            case KNOWLEDGE_LEVEL:
                mComparator =
                        (one, two) -> Double.compare(one.getKnowledgeLevel(),
                                two.getKnowledgeLevel());
                break;
            default:
                mComparator =
                        (one, two) -> one.getNativeWord().toLowerCase().compareTo(two.getNativeWord().toLowerCase());
        }
        if (!ascending) {
            mComparator = mComparator.reversed();
        }
        List<MappingPOJO> entries = mapping.getValue();
        if (entries != null) Collections.sort(entries, mComparator);
        mapping.setValue(entries);
    }


    /**
     * Returns the {@link List} of {@link MappingPOJO}s for the current {@link LessonEntry}.
     *
     * @return The {@link LiveData} wrapped the {@link List} of {@link MappingPOJO}s.
     */
    LiveData<List<MappingPOJO>> getMapping() {
        return mapping;
    }

    /**
     * Updates the currently edited lesson. If it is a new lesson (lessonId<0), creates a new
     * lesson.
     *
     * @param lessonName        Name of the lesson.
     * @param lessonDescription Description of the lesson.
     * @return True if a new lesson was created, false if a lesson was updated.
     */
    boolean addOrUpdateLesson(String lessonName, String lessonDescription) {
        String language = PreferenceUtils.getActiveLanguage();
        Integer lessonIdValue = lessonId.getValue();
        if (lessonIdValue == null) return false;
        if (lessonIdValue < 0) {
            Log.d(TAG, "Updating existing lesson: " + lessonId);
            mCulaRepository.insertLessonEntry(this, new LessonEntry(lessonName,
                    lessonDescription, language));
        } else {
            Log.d(TAG, "Inserting new lesson");
            mCulaRepository.updateLessonEntry(new LessonEntry(lessonName, lessonDescription, language, lessonIdValue
            ));
//            updateViewModel(lessonId);
        }
        return lessonIdValue < 0;
    }

    @Override
    public void onLessonEntryAdded(long[] ids) {
        if (ids.length > 0) {
            lessonId.postValue((int) ids[0]);
            Log.d(TAG, "Added ID " + ids[0]);
        }
    }

    /**
     * Insert or deletes the selected {@link LessonMappingEntry}.
     *
     * @param insert         If true insert the entry, otherwise delete it.
     * @param libraryEntryId Id of the {@link com.sliebald.cula.data.database.entities.LibraryEntry} to add or delete from the current lesson.
     */
    void insertOrDeleteLessonMappingEntry(boolean insert, int libraryEntryId) {
        if (entry.getValue() == null)
            return;
        if (insert) {
            mCulaRepository.insertLessonMappingEntry(new LessonMappingEntry(entry.getValue().getId(), libraryEntryId));
        } else {
            mCulaRepository.deleteLessonMappingEntry(new LessonMappingEntry(entry.getValue().getId(), libraryEntryId));
        }
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
     * Type the entries are currently sorted by.
     *
     * @return SortType
     */
    SortUtils.SortType getCurrentSortType() {
        return mCurrentSortType;
    }
}
