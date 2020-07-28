package com.sliebald.cula.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.sliebald.cula.BuildConfig
import com.sliebald.cula.data.database.CulaDatabase
import com.sliebald.cula.data.database.entities.*
import com.sliebald.cula.data.database.pojos.*
import com.sliebald.cula.utilities.AppExecutors
import java.util.*

/**
 * Handles data operations in Cula.
 */
class CulaRepository private constructor(database: CulaDatabase, appExecutors: AppExecutors) {

    /**
     * A callback when one or more [LessonEntry]s were added to the database.
     * Contains the Ids of the new entries
     */
    interface OnLessonEntryAddedListener {
        fun onLessonEntryAdded(ids: LongArray)
    }

    private val mLibraryDao = database.libraryDao()
    private val mLanguageDao = database.languageDao()
    private val mLessonDao = database.lessonDao()
    private val mStatisticsDao = database.statisticsDao()
    private val mExecutors = appExecutors

    //TODO: Check how to use singletons in kotlin using the object keyword
    companion object {
        /**
         * Tag for logging.
         */
        private val TAG = CulaRepository::class.simpleName

        // For Singleton instantiation
        private val LOCK = Any()
        private var init = false
        private lateinit var sInstance: CulaRepository

        /**
         * Singleton to make sure only one [CulaRepository] is used at a time.
         *
         * @param database     The [CulaDatabase] to access all
         * [androidx.room.Dao]s.
         * @param appExecutors The [AppExecutors] used to execute all kind of queries of
         * the main thread.
         * @return A new [CulaRepository] if none exists. If already an instance exists this is
         * returned instead of creating a new one.
         */
        @JvmStatic
        @Synchronized
        fun getInstance(database: CulaDatabase, appExecutors: AppExecutors): CulaRepository {
            if (!init) {
                synchronized(LOCK) {
                    if (!init) {
                        init = true
                        sInstance = CulaRepository(database, appExecutors)
                        Log.d(TAG, "Made new repository")
                    }
                }
            }
            return sInstance
        }
    }

    init {
        //Adding test data to database for testing
        if (BuildConfig.DEBUG) {
            setDebugState()
        }
    }

    /**
     * For debugging purposes pre-fill database with specified data.
     */
    private fun setDebugState() {
        //  mExecutors.diskIO().execute(mLibraryDao::deleteAll);
        insertLanguageEntry(LanguageEntry("German", true))
        insertLanguageEntry(LanguageEntry("Greek", false))
        insertLibraryEntry(LibraryEntry("Bread", "Brot", "German", 1.1, Date(), 1))
        insertLibraryEntry(LibraryEntry("Apple", "Apfel", "German", 2.2, Date(), 2))
        insertLibraryEntry(LibraryEntry("Banana", "Banane", "German", 3.3, Date(), 3))
        insertLibraryEntry(LibraryEntry("Pear", "Birne", "German", 0.5, Date(), 4))
        insertLibraryEntry(LibraryEntry("Blackberry", "Brombeere", "German", 4.8, Date(), 5))
        insertLibraryEntry(LibraryEntry("native6", "foreign6", "Greek", 2.0, Date(), 6))
        insertLibraryEntry(LibraryEntry("native7", "foreign7", "Greek", 4.0, Date(), 7))
        insertLibraryEntry(LibraryEntry("native8", "foreign8", "Greek", 4.0, Date(), 8))
        insertLibraryEntry(LibraryEntry("Strawberry", "Erdbeere", "German", 1.0, Date(), 9))
        insertLibraryEntry(LibraryEntry("Peanut", "Erdnuss", "German", 4.0, Date(), 10))
        insertLibraryEntry(LibraryEntry("Raspberry", "Himbeeere", "German", 4.7, Date(), 11))
        insertLibraryEntry(LibraryEntry("Cherry", "Kirsche", "German", 2.1, Date(), 12))
        insertLibraryEntry(LibraryEntry("Bedroom", "Schlafzimmer", "German", 3.3, Date(), 13))
        insertLibraryEntry(LibraryEntry("Living room", "Wohnzimmer", "German", 3.8, Date(), 14))
        insertLibraryEntry(LibraryEntry("Bath", "Bad", "German", 1.6, Date(), 15))
        insertLibraryEntry(LibraryEntry("bath2", "bad2", "German", 1.6, Date(), 16))
        val dummyListener = object : OnLessonEntryAddedListener {
            override fun onLessonEntryAdded(ids: LongArray) {}
        }
        insertLessonEntry(dummyListener, LessonEntry("Fruits", "This lesson contains all fruits", "German", 1))
        insertLessonEntry(dummyListener, LessonEntry("Rooms", "This lesson contains rooms", "German", 2))
        insertLessonEntry(dummyListener, LessonEntry("Test Lesson 3", "This lesson is for testing purposes", "Greek", 3))
        insertLessonEntry(dummyListener, LessonEntry("Everything", "This lesson contains all German words", "German", 4))
        insertLessonEntry(dummyListener, LessonEntry("everything", "This lesson contains all German words", "German", 5))
        insertLessonMappingEntry(LessonMappingEntry(1, 1, 1))
        insertLessonMappingEntry(LessonMappingEntry(1, 2, 2))
        insertLessonMappingEntry(LessonMappingEntry(1, 3, 3))
        insertLessonMappingEntry(LessonMappingEntry(1, 4, 4))
        insertLessonMappingEntry(LessonMappingEntry(1, 5, 5))
        insertLessonMappingEntry(LessonMappingEntry(1, 9, 6))
        insertLessonMappingEntry(LessonMappingEntry(1, 10, 7))
        insertLessonMappingEntry(LessonMappingEntry(1, 11, 8))
        insertLessonMappingEntry(LessonMappingEntry(1, 12, 9))
        insertLessonMappingEntry(LessonMappingEntry(2, 13, 10))
        insertLessonMappingEntry(LessonMappingEntry(2, 14, 11))
        insertLessonMappingEntry(LessonMappingEntry(2, 15, 12))
        insertLessonMappingEntry(LessonMappingEntry(4, 1, 13))
        insertLessonMappingEntry(LessonMappingEntry(4, 2, 14))
        insertLessonMappingEntry(LessonMappingEntry(4, 3, 15))
        insertLessonMappingEntry(LessonMappingEntry(4, 4, 16))
        insertLessonMappingEntry(LessonMappingEntry(4, 5, 17))
        insertLessonMappingEntry(LessonMappingEntry(4, 9, 18))
        insertLessonMappingEntry(LessonMappingEntry(4, 10, 19))
        insertLessonMappingEntry(LessonMappingEntry(4, 11, 20))
        insertLessonMappingEntry(LessonMappingEntry(4, 12, 21))
        insertLessonMappingEntry(LessonMappingEntry(4, 13, 22))
        insertLessonMappingEntry(LessonMappingEntry(4, 14, 23))
        insertLessonMappingEntry(LessonMappingEntry(4, 15, 24))
        insertLessonMappingEntry(LessonMappingEntry(3, 6, 25))
        insertLessonMappingEntry(LessonMappingEntry(3, 7, 26))
        insertLessonMappingEntry(LessonMappingEntry(3, 8, 27))
        insertStatisticsEntry(StatisticEntry(1, 1, 1, 0.0, Date()))
        insertStatisticsEntry(StatisticEntry(2, 1, 1, 0.0, Date()))
        insertStatisticsEntry(StatisticEntry(3, 1, 1, 0.0, Date()))
        insertStatisticsEntry(StatisticEntry(4, 1, 1, 0.0, Date()))
        insertStatisticsEntry(StatisticEntry(5, 1, 1, 0.0, Date(Date().time - 86400000)))
        insertStatisticsEntry(StatisticEntry(6, 1, 1, 0.0, Date(Date().time - 86400000)))
        insertStatisticsEntry(StatisticEntry(7, 1, 1, 0.0, Date(Date().time - 86400000 * 2)))
        insertStatisticsEntry(StatisticEntry(8, 1, 1, 0.0, Date(Date().time - 86400000 * 2)))
        insertStatisticsEntry(StatisticEntry(9, 1, 1, 0.0, Date(Date().time - 86400000 * 2)))
        insertStatisticsEntry(StatisticEntry(10, 1, 1, 0.0, Date(Date().time - 86400000 * 4)))
        insertStatisticsEntry(StatisticEntry(11, 1, 1, 0.0, Date(Date().time - 86400000 * 4)))
        insertStatisticsEntry(StatisticEntry(12, 1, 1, 0.0, Date(Date().time - 86400000 * 4)))
        insertStatisticsEntry(StatisticEntry(13, 1, 1, 0.0, Date(Date().time - 86400000 * 4)))
        insertStatisticsEntry(StatisticEntry(14, 1, 1, 0.0, Date(Date().time - 86400000 * 5)))
        insertStatisticsEntry(StatisticEntry(15, 1, 1, 0.0, Date(Date().time - 86400000 * 7)))
        insertStatisticsEntry(StatisticEntry(16, 1, 1, 0.0, Date(Date().time - 86400000 * 7)))
        insertStatisticsEntry(StatisticEntry(17, 1, 1, 0.0, Date(Date().time - 86400000 * 8)))
        insertStatisticsEntry(StatisticEntry(18, 1, 1, 0.0, Date(Date().time - 86400000 * 9)))
        insertStatisticsEntry(StatisticEntry(19, 1, 1, 0.0, Date(Date().time - 86400000 * 9)))
        insertStatisticsEntry(StatisticEntry(20, 1, 1, 0.0, Date(Date().time - 86400000 * 10)))
        insertStatisticsEntry(StatisticEntry(21, 1, 1, 0.0, Date(Date().time - 86400000 * 10)))
        insertStatisticsEntry(StatisticEntry(18, 1, 1, 0.0, Date(Date().time - 86400000 * 10)))
        insertStatisticsEntry(StatisticEntry(22, 1, 1, 0.0, Date(Date().time - 86400000 * 10)))
        //print the current entries in the db to the log console.
        mExecutors.diskIO().execute { Log.d(TAG, "Database has now " + mLibraryDao.getLibrarySize() + " library entries") }
        mExecutors.diskIO().execute {
            Log.d(TAG, "Database has now " + mLanguageDao.getAmountOfLanguages() + " language entries")
        }
        mExecutors.diskIO().execute { Log.d(TAG, "Database has now " + mLessonDao.getAmountOfLessons() + " lesson entries") }
        mExecutors.diskIO().execute { Log.d(TAG, "Database has now " + mLessonDao.getAmountOfLessonsMappings() + " lesson mapping entries") }
    }

    /**
     * Adds the given [LessonEntry]s to the Database.
     * If the [LessonEntry] already exists, nothing is done.
     *
     * @param callback      called with the ids of the added entries.
     * @param lessonEntries One or more [LessonEntry]s to add to the Database
     */
    fun insertLessonEntry(callback: OnLessonEntryAddedListener, vararg lessonEntries: LessonEntry) {
        mExecutors.diskIO().execute { callback.onLessonEntryAdded(mLessonDao.insertEntry(*lessonEntries)) }
    }

    /**
     * Get all [LibraryEntry]s.
     *
     * @return All [LibraryEntry]s.
     */
    fun getAllLibraryEntries(): LiveData<List<LibraryEntry>> = mLibraryDao.getAllEntries()

    /**
     * Get all [LibraryEntry]s.
     *
     * @return The [LibraryEntry] with the given ID.
     */
    fun getLibraryEntry(id: Int): LiveData<LibraryEntry> = mLibraryDao.getEntryById(id)

    /**
     * Adds the given [LibraryEntry]s to the Database.
     *
     * @param libraryEntry One or more [LibraryEntry]s to add to the Database
     */
    fun insertLibraryEntry(vararg libraryEntry: LibraryEntry) {
        mExecutors.diskIO().execute { mLibraryDao.insertEntry(*libraryEntry) }
        for (lib in libraryEntry) {
            Log.d(TAG, "Added Entry to db:$lib")
        }
    }

    /**
     * Removes the given [LibraryEntry]s from the Database.
     *
     * @param libraryEntry The @[LibraryEntry]s to remove from the Database
     */
    fun deleteLibraryEntry(libraryEntry: LibraryEntry) {
        mExecutors.diskIO().execute { mLibraryDao.deleteEntry(libraryEntry) }
    }

    /**
     * Updates the given [LibraryEntry]s to the Database.
     *
     * @param libraryEntries One or more [LibraryEntry]s to update in the Database
     */
    fun updateLibraryEntry(vararg libraryEntries: LibraryEntry) {
        mExecutors.diskIO().execute { mLibraryDao.updateEntry(*libraryEntries) }
    }

    /**
     * Get all [LanguageEntry]s.
     *
     * @return All [LanguageEntry]s.
     */
    fun getAllLanguageEntries(): LiveData<List<LanguageEntry>> = mLanguageDao.getAllEntries()

    /**
     * Removes the given [LanguageEntry]s from the Database.
     *
     * @param languageEntry The @[LanguageEntry]s to remove from the Database
     */
    fun deleteLanguageEntry(languageEntry: LanguageEntry) {
        mExecutors.diskIO().execute { mLanguageDao.deleteEntry(languageEntry) }
    }

    /**
     * Adds the given [LanguageEntry]s to the Database. If the language already exists,
     * nothing is done.
     *
     * @param languageEntries One or more [LanguageEntry]s to add to the Database
     */
    fun insertLanguageEntry(vararg languageEntries: LanguageEntry) {
        mExecutors.diskIO().execute { mLanguageDao.insertEntry(*languageEntries) }
    }

    /**
     * Get all [LessonEntry]s.
     *
     * @return All [LessonEntry]s.
     */
    fun getAllLessonEntries(): LiveData<List<LessonEntry>> = mLessonDao.getAllEntries()

    /**
     * Gets the active Language [LanguageEntry]s in the language database table.
     *
     * @return [LiveData] with the active @[LanguageEntry]s. Null if none is active.
     */
    fun getActiveLanguage(): LiveData<LanguageEntry?> = mLanguageDao.getActiveLanguage()

    /**
     * Sets the active language in the database.
     *
     * @param language The active language
     */
    fun setActiveLanguage(language: String) {
        mExecutors.diskIO().execute { mLanguageDao.setActiveLanguage(language) }
    }

    /**
     * Updates the given [LessonEntry]s to the Database.
     *
     * @param lessonEntries One or more [LessonEntry]s to update in the Database
     */
    fun updateLessonEntry(vararg lessonEntries: LessonEntry) {
        mExecutors.diskIO().execute { mLessonDao.updateEntry(*lessonEntries) }
    }

    /**
     * Removes the given [LessonEntry]s from the Database.
     *
     * @param lessonEntry The @[LessonEntry]s to remove from the Database
     */
    fun deleteLessonEntry(lessonEntry: LessonEntry) {
        mExecutors.diskIO().execute { mLessonDao.deleteEntry(lessonEntry) }
    }

    /**
     * Get the [LessonEntry] with the given ID.
     *
     * @return The [LessonEntry] with the given ID.
     */
    fun getLessonEntry(id: Int): LiveData<LessonEntry> = mLessonDao.getEntryById(id)

    /**
     * Adds the given [LessonMappingEntry]s to the Database. If the
     * [LessonMappingEntry] already exists, nothing is done.
     *
     * @param lessonMappingEntries One or more [LanguageEntry]s to add to the Database
     */
    fun insertLessonMappingEntry(vararg lessonMappingEntries: LessonMappingEntry) {
        mExecutors.diskIO().execute { mLessonDao.insertMappingEntry(*lessonMappingEntries) }
    }

    /**
     * Deletes a mapping between a [LessonEntry] and a [LibraryEntry].
     *
     * @param lessonMappingEntry The [LessonMappingEntry] describing the mapping.
     */
    fun deleteLessonMappingEntry(lessonMappingEntry: LessonMappingEntry) {
        mExecutors.diskIO().execute {
            mLessonDao.deleteMappingEntry(lessonMappingEntry.lessonEntryId, lessonMappingEntry.libraryEntryId)
        }
    }

    /**
     * Gets the List of [MappingPOJO]s in the lesson database table with the given id
     * based on the current language.
     *
     * @param id The lesson id for which the [List] of [MappingPOJO]s
     * should be retrieved.
     * @return [LiveData] with the [List] of @[MappingPOJO]s.
     */
    fun getMappingEntries(id: Int): LiveData<List<MappingPOJO>> = mLessonDao.getLessonMappingById(id)

    fun getTrainingEntries(number: Int, minKnowledgeLevel: Double, maxKnowledgeLevel: Double, lessonId: Int): LiveData<List<LibraryEntry>> =
            mLibraryDao.getTrainingEntries(number, minKnowledgeLevel, maxKnowledgeLevel, lessonId)

    fun getTrainingEntries(number: Int, minKnowledgeLevel: Double, maxKnowledgeLevel: Double): LiveData<List<LibraryEntry>> =
            mLibraryDao.getTrainingEntries(number, minKnowledgeLevel, maxKnowledgeLevel)

    /**
     * Adds the given [StatisticEntry]s to the Database.
     *
     * @param statisticEntries One or more [StatisticEntry]s to add to the Database
     */
    fun insertStatisticsEntry(vararg statisticEntries: StatisticEntry) {
        mExecutors.diskIO().execute { mStatisticsDao.insertEntry(*statisticEntries) }
    }

    /**
     * Gets a [List] of [StatisticsLibraryWordCount] entries, where each entry
     * contains the amount of words for each knowledgeLevel range (0-0.9999, 1-1.9999,...).
     *
     * @return The List of [StatisticsLibraryWordCount]s
     */
    fun getStatisticsLibraryCountByKnowledgeLevel(): LiveData<List<StatisticsLibraryWordCount>> =
            mStatisticsDao.getStatisticsLibraryCountByKnowledgeLevel()

    /**
     * Returns the activity during the last 14 days. For each active day a
     * [StatisticsActivityEntry] is added to the list. Days without activity have no entry.
     *
     * @return The List of [StatisticsActivityEntry] for all active days.
     */
    fun getStatisticsActivity(): LiveData<List<StatisticsActivityEntry>> {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -14)
        return mStatisticsDao.getStatisticsActivity(calendar.time)
    }

    /**
     * Get the date of the last training.
     *
     * @return Date of the last training wrapped in LiveData.
     */
    fun getLastTrainingDate(): LiveData<StatisticsLastTrainingDate> = mStatisticsDao.getLastTrainingDate()

    /**
     * Returns the lesson with the lowest KnowledgeLevel
     *
     * @return LessonKnowledgeLevel holding the result wrapped in LiveData.
     */
    fun getWorstLesson(): LiveData<LessonKnowledgeLevel> = mStatisticsDao.getWorstLesson()
}