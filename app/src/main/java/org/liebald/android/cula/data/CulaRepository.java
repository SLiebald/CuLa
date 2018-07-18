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

package org.liebald.android.cula.data;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import org.liebald.android.cula.data.database.CulaDatabase;
import org.liebald.android.cula.data.database.Dao.LanguageDao;
import org.liebald.android.cula.data.database.Dao.LessonDao;
import org.liebald.android.cula.data.database.Dao.LibraryDao;
import org.liebald.android.cula.data.database.Dao.QuoteDao;
import org.liebald.android.cula.data.database.Dao.StatisticsDao;
import org.liebald.android.cula.data.database.Entities.LanguageEntry;
import org.liebald.android.cula.data.database.Entities.LessonEntry;
import org.liebald.android.cula.data.database.Entities.LessonMappingEntry;
import org.liebald.android.cula.data.database.Entities.LibraryEntry;
import org.liebald.android.cula.data.database.Entities.QuoteEntry;
import org.liebald.android.cula.data.database.Entities.StatisticEntry;
import org.liebald.android.cula.data.database.Pojos.MappingPOJO;
import org.liebald.android.cula.data.database.Pojos.StatisticsActivityEntry;
import org.liebald.android.cula.data.database.Pojos.StatisticsLastTrainingDate;
import org.liebald.android.cula.data.database.Pojos.StatisticsLibraryWordCount;
import org.liebald.android.cula.utilities.AppExecutors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Handles data operations in Cula.
 */
public class CulaRepository {

    private CulaRepository(CulaDatabase database, AppExecutors appExecutors) {
        mLibraryDao = database.libraryDao();
        mStatisticsDao = database.statisticsDao();
        mExecutors = appExecutors;
        mLanguageDao = database.languageDao();
        mQuoteDao = database.quoteDao();
        mLessonDao = database.lessonDao();
        //TODO: remove following testcode before publishing
        setDebugState();
    }


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
    private final QuoteDao mQuoteDao;
    private final LessonDao mLessonDao;
    private final StatisticsDao mStatisticsDao;
    private final AppExecutors mExecutors;

    /**
     * Singleton to make sure only one {@link CulaRepository} is used at a time.
     *
     * @param database          The {@link CulaDatabase} to access all
     * {@link android.arch.persistence.room.Dao}s.
     * @param appExecutors      The {@link AppExecutors} used to execute all kind of queries of
     *                          the main thread.
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
     * For debugging purposes prefill database with specified data.
     */
    private void setDebugState() {
        mExecutors.diskIO().execute(mLibraryDao::deleteAll);
        insertLanguageEntry(new LanguageEntry("German", true));
        insertLanguageEntry(new LanguageEntry("Greek", false));
        insertLibraryEntry(new LibraryEntry(1, "native1", "foreign", "German", 1.1, new Date()));
        insertLibraryEntry(new LibraryEntry(2, "native2", "foreign2", "German", 2.2, new Date()));
        insertLibraryEntry(new LibraryEntry(3, "native3", "foreign3", "German", 3.3, new Date()));
        insertLibraryEntry(new LibraryEntry(4, "native4", "foreign4", "German", 4.4, new Date()));
        insertLibraryEntry(new LibraryEntry(5, "native5", "foreign5", "German", 4.8, new Date()));
        insertLibraryEntry(new LibraryEntry(6, "native6", "foreign6", "Greek", 2, new Date()));
        insertLibraryEntry(new LibraryEntry(7, "native7", "foreign7", "Greek", 4, new Date()));
        insertLibraryEntry(new LibraryEntry(8, "native8", "foreign8", "Greek", 4, new Date()));

        insertQuoteEntry(new QuoteEntry(1, "TestQuote of the Day with a rather medium long text",
                "Stefan"));

        OnLessonEntryAddedListener dummyListener = ids -> {
        };
        insertLessonEntry(dummyListener, new LessonEntry(1, "Test Lesson 1", "This lesson is for " +
                "testing purposes", "German"));
        insertLessonEntry(dummyListener, new LessonEntry(2, "Test Lesson 2", "This lesson is for " +
                "testing purposes", "Greek"));
        insertLessonEntry(dummyListener, new LessonEntry(3, "Test Lesson 3", "This lesson is for " +
                "testing purposes", "Greek"));
        insertLessonMappingEntry(new LessonMappingEntry(1, 1, 1));
        insertLessonMappingEntry(new LessonMappingEntry(2, 1, 2));
        insertLessonMappingEntry(new LessonMappingEntry(3, 2, 6));
        insertLessonMappingEntry(new LessonMappingEntry(4, 2, 7));
        insertLessonMappingEntry(new LessonMappingEntry(5, 2, 8));
        insertLessonMappingEntry(new LessonMappingEntry(6, 3, 7));
        insertLessonMappingEntry(new LessonMappingEntry(7, 3, 8));

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


        //print the current entries in the db to the log console.
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLibraryDao
                        .getLibrarySize() + " library entries")
        );
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLanguageDao
                        .getAmountOfLanguages() + " language entries")
        );
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLessonDao
                        .getAmountOfLessons() + " lesson entries")
        );
        mExecutors.diskIO().execute(() ->
                Log.d(CulaRepository.class.getSimpleName(), "Database has now " + mLessonDao
                        .getAmountOfLessonsMappings() + " lesson mapping entries")
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
    public void insertLibraryEntry(LibraryEntry... libraryEntry) {
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
     * Sets the active language in the database.
     *
     * @param language The active language
     */
    public void setActiveLanguage(String language) {
        mExecutors.diskIO().execute(() -> mLanguageDao.setActiveLanguage(language));
    }

    /**
     * Adds the given {@link QuoteEntry}s to the Database.
     *
     * @param quoteEntries One or more {@link QuoteEntry}s to add to the Database
     */
    public void insertQuoteEntry(QuoteEntry... quoteEntries) {
        mExecutors.diskIO().execute(() -> mQuoteDao.insertEntry(quoteEntries));
    }

    /**
     * Load a new {@link QuoteEntry} and return it wrapped in {@link LiveData}.
     *
     * @return The {@link LiveData} wrapped {@link QuoteEntry}
     */
    public LiveData<QuoteEntry> getQuote() {
        return mQuoteDao.getLatestEntry();
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
     * Updates the given {@link LessonEntry}s to the Database.
     *
     * @param lessonEntries One or more {@link LessonEntry}s to update in the Database
     */
    public void updateLessonEntry(LessonEntry... lessonEntries) {
        mExecutors.diskIO().execute(() -> mLessonDao.updateEntry(lessonEntries));
    }

    /**
     * A callback when one or more {@link LessonEntry}s were added to the database.
     * Contains the Ids of the new entries
     */
    public interface OnLessonEntryAddedListener {
        void onLessonEntryAdded(long[] ids);
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
     * @return Date of the last training.
     */
    public LiveData<StatisticsLastTrainingDate> getLastTrainingDate() {
        return mStatisticsDao.getLastTrainingDate();
    }


}