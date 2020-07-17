package com.sliebald.cula.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sliebald.cula.data.database.dao.LanguageDao
import com.sliebald.cula.data.database.dao.LessonDao
import com.sliebald.cula.data.database.dao.LibraryDao
import com.sliebald.cula.data.database.dao.StatisticsDao
import com.sliebald.cula.data.database.entities.*

/**
 * The Room Database for the App.
 */
@Database(
        version = 1,
        entities = [
            LibraryEntry::class,
            LanguageEntry::class,
            LessonEntry::class,
            LessonMappingEntry::class,
            StatisticEntry::class
        ],
        exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class CulaDatabase : RoomDatabase() {
    /**
     * Gain access to the [LibraryDao] for interacting with the library related database
     * parts.
     *
     * @return The [LibraryDao].
     */
    abstract fun libraryDao(): LibraryDao

    /**
     * Gain access to the [LanguageDao] for interacting with the language related database
     * parts.
     *
     * @return The [LanguageDao].
     */
    abstract fun languageDao(): LanguageDao

    /**
     * Gain access to the [LessonDao] for interacting with the lesson related database parts.
     *
     * @return The [LessonDao].
     */
    abstract fun lessonDao(): LessonDao

    /**
     * Gain access to the [StatisticsDao] for interacting with the statistics related
     * database parts.
     *
     * @return The [StatisticsDao].
     */
    abstract fun statisticsDao(): StatisticsDao

    companion object {
        private const val DATABASE_NAME = "CulaDatabase"
        private val LOCK = Any()

        @Volatile
        private lateinit var sInstance: CulaDatabase

        private var init = false;
        fun getInstance(context: Context): CulaDatabase {
            if (!init) {
                synchronized(LOCK) {
                    if (!init) {
                        sInstance = Room.databaseBuilder(context.applicationContext,
                                CulaDatabase::class.java,
                                DATABASE_NAME).build()
                    }
                }
            }
            return sInstance
        }
    }
}