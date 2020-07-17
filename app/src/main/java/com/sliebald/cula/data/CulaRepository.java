/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sliebald.cula.data;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.sliebald.cula.BuildConfig;
import com.sliebald.cula.data.database.CulaDatabase;
import com.sliebald.cula.data.database.dao.LanguageDao;
import com.sliebald.cula.data.database.dao.LessonDao;
import com.sliebald.cula.data.database.dao.LibraryDao;
import com.sliebald.cula.data.database.dao.StatisticsDao;
import com.sliebald.cula.data.database.entities.LanguageEntry;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.data.database.entities.LessonMappingEntry;
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.data.database.entities.StatisticEntry;
import com.sliebald.cula.data.database.pojos.LessonKnowledgeLevel;
import com.sliebald.cula.data.database.pojos.MappingPOJO;
import com.sliebald.cula.data.database.pojos.StatisticsActivityEntry;
import com.sliebald.cula.data.database.pojos.StatisticsLastTrainingDate;
import com.sliebald.cula.data.database.pojos.StatisticsLibraryWordCount;
import com.sliebald.cula.utilities.AppExecutors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Handles data operations in Cula.
 */
public class CulaRepository {

    /**
     * Tag for logging.
     */
    private static final String TAG = CulaRepository.class.getSimpleName();
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    @SuppressLint("StaticFieldLeak")
    private static CulaRepository sInstance;
    private final LibraryDao mLibraryDao;
    private final LanguageDao mLanguageDao;
    private final LessonDao mLessonDao;
    private final StatisticsDao mStatisticsDao;
    private final AppExecutors mExecutors;

    private CulaRepository(CulaDatabase database, AppExecutors appExecutors) {
        mLibraryDao = database.libraryDao();
        mStatisticsDao = database.statisticsDao();
        mExecutors = appExecutors;
        mLanguageDao = database.languageDao();
        mLessonDao = database.lessonDao();

        //Adding test data to database for testing
        if (BuildConfig.DEBUG) {
            setDebugState();
        }
    }

    /**
     * Singleton to make sure only one {@link CulaRepository} is used at a time.
     *
     * @param database     The {@link CulaDatabase} to access all
     *                     {@link androidx.room.Dao}s.
     * @param appExecutors The {@link AppExecutors} used to execute all kind of queries of
     *                     the main thread.
     * @return A new {@link CulaRepository} if none exists. If already an instance exists this is
     * returned instead of creating a new one.
     */
    public synchronized static CulaRepository getInstance(
            CulaDatabase database, AppExecutors appExecutors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new CulaRepository(database, appExecutors);
                Log.d(TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * For debugging purposes pre-fill database with specified data.
     */
    private void setDebugState() {
//        mExecutors.diskIO().execute(mLibraryDao::deleteAll);
        insertLanguageEntry(new LanguageEntry("German", true));
        insertLanguageEntry(new LanguageEntry("Greek", false));
        insertLibraryEntry(new LibraryEntry("Bread", "Brot", "German", 1.1, new Date(), 1));
        insertLibraryEntry(new LibraryEntry("Apple", "Apfel", "German", 2.2, new Date(), 2));
        insertLibraryEntry(new LibraryEntry("Banana", "Banane", "German", 3.3, new Date(), 3));
        insertLibraryEntry(new LibraryEntry("Pear", "Birne", "German", 0.5, new Date(), 4));
        insertLibraryEntry(new LibraryEntry("Blackberry", "Brombeere", "German", 4.8, new Date(), 5
        ));
        insertLibraryEntry(new LibraryEntry("native6", "foreign6", "Greek", 2, new Date(), 6));
        insertLibraryEntry(new LibraryEntry("native7", "foreign7", "Greek", 4, new Date(), 7));
        insertLibraryEntry(new LibraryEntry("native8", "foreign8", "Greek", 4, new Date(), 8));
        insertLibraryEntry(new LibraryEntry("Strawberry", "Erdbeere", "German", 1, new Date(), 9));
        insertLibraryEntry(new LibraryEntry("Peanut", "Erdnuss", "German", 4, new Date(), 10));
        insertLibraryEntry(new LibraryEntry("Raspberry", "Himbeeere", "German", 4.7, new Date(), 11
        ));
        insertLibraryEntry(new LibraryEntry("Cherry", "Kirsche", "German", 2.1, new Date(), 12));
        insertLibraryEntry(new LibraryEntry("Bedroom", "Schlafzimmer", "German", 3.3, new Date(), 13
        ));
        insertLibraryEntry(new LibraryEntry("Living room", "Wohnzimmer", "German", 3.8, new Date(), 14
        ));
        insertLibraryEntry(new LibraryEntry("Bath", "Bad", "German", 1.6, new Date(), 15));
        insertLibraryEntry(new LibraryEntry("bath2", "bad2", "German", 1.6, new Date(), 16));


        OnLessonEntryAddedListener dummyListener = ids -> {
        };
        insertLessonEntry(dummyListener, new LessonEntry("Fruits", "This lesson contains all " +
                "fruits", "German", 1));
        insertLessonEntry(dummyListener, new LessonEntry("Rooms", "This lesson " +
                "contains rooms", "German", 2));
        insertLessonEntry(dummyListener, new LessonEntry("Test Lesson 3", "This lesson is for " +
                "testing purposes", "Greek", 3));
        insertLessonEntry(dummyListener, new LessonEntry("Everything", "This lesson " +
                "contains all German words", "German", 4));
        insertLessonEntry(dummyListener, new LessonEntry("everything", "This lesson " +
                "contains all German words", "German", 5));

        insertLessonMappingEntry(new LessonMappingEntry(1, 1, 1));
        insertLessonMappingEntry(new LessonMappingEntry(1, 2, 2));
        insertLessonMappingEntry(new LessonMappingEntry(1, 3, 3));
        insertLessonMappingEntry(new LessonMappingEntry(1, 4, 4));
        insertLessonMappingEntry(new LessonMappingEntry(1, 5, 5));
        insertLessonMappingEntry(new LessonMappingEntry(1, 9, 6));
        insertLessonMappingEntry(new LessonMappingEntry(1, 10, 7));
        insertLessonMappingEntry(new LessonMappingEntry(1, 11, 8));
        insertLessonMappingEntry(new LessonMappingEntry(1, 12, 9));
        insertLessonMappingEntry(new LessonMappingEntry(2, 13, 10));
        insertLessonMappingEntry(new LessonMappingEntry(2, 14, 11));
        insertLessonMappingEntry(new LessonMappingEntry(2, 15, 12));
        insertLessonMappingEntry(new LessonMappingEntry(4, 1, 13));
        insertLessonMappingEntry(new LessonMappingEntry(4, 2, 14));
        insertLessonMappingEntry(new LessonMappingEntry(4, 3, 15));
        insertLessonMappingEntry(new LessonMappingEntry(4, 4, 16));
        insertLessonMappingEntry(new LessonMappingEntry(4, 5, 17));
        insertLessonMappingEntry(new LessonMappingEntry(4, 9, 18));
        insertLessonMappingEntry(new LessonMappingEntry(4, 10, 19));
        insertLessonMappingEntry(new LessonMappingEntry(4, 11, 20));
        insertLessonMappingEntry(new LessonMappingEntry(4, 12, 21));
        insertLessonMappingEntry(new LessonMappingEntry(4, 13, 22));
        insertLessonMappingEntry(new LessonMappingEntry(4, 14, 23));
        insertLessonMappingEntry(new LessonMappingEntry(4, 15, 24));
        insertLessonMappingEntry(new LessonMappingEntry(3, 6, 25));
        insertLessonMappingEntry(new LessonMappingEntry(3, 7, 26));
        insertLessonMappingEntry(new LessonMappingEntry(3, 8, 27));


        insertStatisticsEntry(new StatisticEntry(1, 1, 1, 0, new Date()));
        insertStatisticsEntry(new StatisticEntry(2, 1, 1, 0, new Date()));
        insertStatisticsEntry(new StatisticEntry(3, 1, 1, 0, new Date()));
        insertStatisticsEntry(new StatisticEntry(4, 1, 1, 0, new Date()));
        insertStatisticsEntry(new StatisticEntry(5, 1, 1, 0, new Date(new Date().getTime()
                - 86400000)));
        insertStatisticsEntry(new StatisticEntry(6, 1, 1, 0, new Date(new Date().getTime()
                - 86400000)));
        insertStatisticsEntry(new StatisticEntry(7, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 2)));
        insertStatisticsEntry(new StatisticEntry(8, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 2)));
        insertStatisticsEntry(new StatisticEntry(9, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 2)));
        insertStatisticsEntry(new StatisticEntry(10, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 4)));
        insertStatisticsEntry(new StatisticEntry(11, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 4)));
        insertStatisticsEntry(new StatisticEntry(12, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 4)));
        insertStatisticsEntry(new StatisticEntry(13, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 4)));
        insertStatisticsEntry(new StatisticEntry(14, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 5)));
        insertStatisticsEntry(new StatisticEntry(15, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 7)));
        insertStatisticsEntry(new StatisticEntry(16, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 7)));
        insertStatisticsEntry(new StatisticEntry(17, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 8)));
        insertStatisticsEntry(new StatisticEntry(18, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 9)));
        insertStatisticsEntry(new StatisticEntry(19, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 9)));
        insertStatisticsEntry(new StatisticEntry(20, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 10)));
        insertStatisticsEntry(new StatisticEntry(21, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 10)));
        insertStatisticsEntry(new StatisticEntry(18, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 10)));
        insertStatisticsEntry(new StatisticEntry(22, 1, 1, 0, new Date(new Date().getTime()
                - 86400000 * 10)));
        //print the current entries in the db to the log console.
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLibraryDao.getLibrarySize() + " library entries")
        );
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLanguageDao
                        .getAmountOfLanguages() + " language entries")
        );
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLessonDao.getAmountOfLessons() + " lesson entries")
        );
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLessonDao.getAmountOfLessonsMappings() + " lesson mapping entries")
        );

    }


    /**
     * Adds the given {@link LessonEntry}s to the Database.
     * If the {@link LessonEntry} already exists, nothing is done.
     *
     * @param callback      called with the ids of the added entries.
     * @param lessonEntries One or more {@link LessonEntry}s to add to the Database
     */
    public void insertLessonEntry(OnLessonEntryAddedListener callback, LessonEntry...
            lessonEntries) {
        mExecutors.diskIO().execute(() -> callback.onLessonEntryAdded(mLessonDao.insertEntry
                (lessonEntries)));
    }


    /**
     * Get all {@link LibraryEntry}s.
     *
     * @return All {@link LibraryEntry}s.
     */
    public LiveData<List<LibraryEntry>> getAllLibraryEntries() {
        return mLibraryDao.getAllEntries();
    }

    /**
     * Get all {@link LibraryEntry}s.
     *
     * @return The {@link LibraryEntry} with the given ID.
     */
    public LiveData<LibraryEntry> getLibraryEntry(int id) {
        return mLibraryDao.getEntryById(id);
    }

    /**
     * Adds the given {@link LibraryEntry}s to the Database.
     *
     * @param libraryEntry One or more {@link LibraryEntry}s to add to the Database
     */
    public void insertLibraryEntry(@NonNull LibraryEntry... libraryEntry) {
        mExecutors.diskIO().execute(() -> mLibraryDao.insertEntry(libraryEntry));
        for (LibraryEntry lib :
                libraryEntry) {
            Log.d(TAG, "Added Entry to db:" + lib.toString());
        }
    }

    /**
     * Removes the given {@link LibraryEntry}s from the Database.
     *
     * @param libraryEntry The @{@link LibraryEntry}s to remove from the Database
     */
    public void deleteLibraryEntry(LibraryEntry libraryEntry) {
        mExecutors.diskIO().execute(() -> mLibraryDao.deleteEntry(libraryEntry));
    }

    /**
     * Updates the given {@link LibraryEntry}s to the Database.
     *
     * @param libraryEntries One or more {@link LibraryEntry}s to update in the Database
     */
    public void updateLibraryEntry(LibraryEntry... libraryEntries) {
        mExecutors.diskIO().execute(() -> mLibraryDao.updateEntry(libraryEntries));
    }

    /**
     * Get all {@link LanguageEntry}s.
     *
     * @return All {@link LanguageEntry}s.
     */
    public LiveData<List<LanguageEntry>> getAllLanguageEntries() {
        return mLanguageDao.getAllEntries();
    }

    /**
     * Removes the given {@link LanguageEntry}s from the Database.
     *
     * @param languageEntry The @{@link LanguageEntry}s to remove from the Database
     */
    public void deleteLanguageEntry(LanguageEntry languageEntry) {
        mExecutors.diskIO().execute(() -> mLanguageDao.deleteEntry(languageEntry));
    }

    /**
     * Adds the given {@link LanguageEntry}s to the Database. If the language already exists,
     * nothing is done.
     *
     * @param languageEntries One or more {@link LanguageEntry}s to add to the Database
     */
    public void insertLanguageEntry(LanguageEntry... languageEntries) {
        mExecutors.diskIO().execute(() -> mLanguageDao.insertEntry(languageEntries));
    }

    /**
     * Get all {@link LessonEntry}s.
     *
     * @return All {@link LessonEntry}s.
     */
    public LiveData<List<LessonEntry>> getAllLessonEntries() {
        return mLessonDao.getAllEntries();
    }

    /**
     * Gets the active Language {@link LanguageEntry}s in the language database table.
     *
     * @return {@link LiveData} with the active @{@link LanguageEntry}s. Null if none is active.
     */
    public LiveData<LanguageEntry> getActiveLanguage() {
        return mLanguageDao.getActiveLanguage();
    }

    /**
     * Sets the active language in the database.
     *
     * @param language The active language
     */
    public void setActiveLanguage(String language) {
        mExecutors.diskIO().execute(() -> mLanguageDao.setActiveLanguage(language));
    }

    /**
     * Updates the given {@link LessonEntry}s to the Database.
     *
     * @param lessonEntries One or more {@link LessonEntry}s to update in the Database
     */
    public void updateLessonEntry(LessonEntry... lessonEntries) {
        mExecutors.diskIO().execute(() -> mLessonDao.updateEntry(lessonEntries));
    }

    /**
     * Removes the given {@link LessonEntry}s from the Database.
     *
     * @param lessonEntry The @{@link LessonEntry}s to remove from the Database
     */
    public void deleteLessonEntry(LessonEntry lessonEntry) {
        mExecutors.diskIO().execute(() -> mLessonDao.deleteEntry(lessonEntry));
    }

    /**
     * Get the {@link LessonEntry} with the given ID.
     *
     * @return The {@link LessonEntry} with the given ID.
     */
    public LiveData<LessonEntry> getLessonEntry(int id) {
        return mLessonDao.getEntryById(id);
    }

    /**
     * Adds the given {@link LessonMappingEntry}s to the Database. If the
     * {@link LessonMappingEntry} already exists, nothing is done.
     *
     * @param lessonMappingEntries One or more {@link LanguageEntry}s to add to the Database
     */
    public void insertLessonMappingEntry(LessonMappingEntry... lessonMappingEntries) {
        mExecutors.diskIO().execute(() -> mLessonDao.insertMappingEntry(lessonMappingEntries));
    }

    /**
     * Deletes a mapping between a {@link LessonEntry} and a {@link LibraryEntry}.
     *
     * @param lessonMappingEntry The {@link LessonMappingEntry} describing the mapping.
     */
    public void deleteLessonMappingEntry(LessonMappingEntry lessonMappingEntry) {
        mExecutors.diskIO().execute(() -> mLessonDao.deleteMappingEntry(
                lessonMappingEntry.getLessonEntryId(), lessonMappingEntry.getLibraryEntryId()));

    }

    /**
     * Gets the List of {@link MappingPOJO}s in the lesson database table with the given id
     * based on the current language.
     *
     * @param id The lesson id for which the {@link List} of {@link MappingPOJO}s
     *           should be retrieved.
     * @return {@link LiveData} with the {@link List} of @{@link MappingPOJO}s.
     */
    public LiveData<List<MappingPOJO>> getMappingEntries(int id) {
        return mLessonDao.getLessonMappingById(id);
    }

    public LiveData<List<LibraryEntry>> getTrainingEntries(int number, double
            minKnowledgeLevel, double maxKnowledgeLevel, int lessonId) {
        return mLibraryDao.getTrainingEntries(number, minKnowledgeLevel,
                maxKnowledgeLevel, lessonId);
    }

    public LiveData<List<LibraryEntry>> getTrainingEntries(int number, double
            minKnowledgeLevel, double maxKnowledgeLevel) {
        return mLibraryDao.getTrainingEntries(number, minKnowledgeLevel,
                maxKnowledgeLevel);
    }

    /**
     * Adds the given {@link StatisticEntry}s to the Database.
     *
     * @param statisticEntries One or more {@link StatisticEntry}s to add to the Database
     */
    public void insertStatisticsEntry(StatisticEntry... statisticEntries) {
        mExecutors.diskIO().execute(() -> mStatisticsDao.insertEntry(statisticEntries));
    }

    /**
     * Gets a {@link List} of {@link StatisticsLibraryWordCount} entries, where each entry
     * contains the amount of words for each knowledgeLevel range (0-0.9999, 1-1.9999,...).
     *
     * @return The List of {@link StatisticsLibraryWordCount}s
     */
    public LiveData<List<StatisticsLibraryWordCount>> getStatisticsLibraryCountByKnowledgeLevel() {
        return mStatisticsDao.getStatisticsLibraryCountByKnowledgeLevel();
    }

    /**
     * Returns the activity during the last 14 days. For each active day a
     * {@link StatisticsActivityEntry} is added to the list. Days without activity have no entry.
     *
     * @return The List of {@link StatisticsActivityEntry} for all active days.
     */
    public LiveData<List<StatisticsActivityEntry>> getStatisticsActivity() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        return mStatisticsDao.getStatisticsActivity(calendar.getTime());
    }

    /**
     * Get the date of the last training.
     *
     * @return Date of the last training wrapped in LiveData.
     */
    public LiveData<StatisticsLastTrainingDate> getLastTrainingDate() {
        return mStatisticsDao.getLastTrainingDate();
    }

    /**
     * Returns the lesson with the lowest KnowledgeLevel
     *
     * @return LessonKnowledgeLevel holding the result wrapped in LiveData.
     */
    public LiveData<LessonKnowledgeLevel> getWorstLesson() {
        return mStatisticsDao.getWorstLesson();
    }

    /**
     * A callback when one or more {@link LessonEntry}s were added to the database.
     * Contains the Ids of the new entries
     */
    public interface OnLessonEntryAddedListener {
        void onLessonEntryAdded(long[] ids);
    }


}