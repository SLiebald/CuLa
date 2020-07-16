package com.sliebald.cula.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sliebald.cula.data.database.dao.LanguageDao;
import com.sliebald.cula.data.database.dao.LessonDao;
import com.sliebald.cula.data.database.dao.LibraryDao;
import com.sliebald.cula.data.database.dao.StatisticsDao;
import com.sliebald.cula.data.database.entities.LanguageEntry;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.data.database.entities.LessonMappingEntry;
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.data.database.entities.StatisticEntry;

/**
 * The Room Database for the App.
 */
@Database(version = 1,
        entities = {
                LibraryEntry.class,
                LanguageEntry.class,
                LessonEntry.class,
                LessonMappingEntry.class,
                StatisticEntry.class},
        exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class CulaDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "CulaDatabase";
    private static final Object LOCK = new Object();
    private static volatile CulaDatabase sInstance;

    public static CulaDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            CulaDatabase.class,
                            CulaDatabase.DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }

    /**
     * Gain access to the {@link LibraryDao} for interacting with the library related database
     * parts.
     *
     * @return The {@link LibraryDao}.
     */
    public abstract LibraryDao libraryDao();

    /**
     * Gain access to the {@link LanguageDao} for interacting with the language related database
     * parts.
     *
     * @return The {@link LanguageDao}.
     */
    public abstract LanguageDao languageDao();


    /**
     * Gain access to the {@link LessonDao} for interacting with the lesson related database parts.
     *
     * @return The {@link LessonDao}.
     */
    public abstract LessonDao lessonDao();

    /**
     * Gain access to the {@link StatisticsDao} for interacting with the statistics related
     * database parts.
     *
     * @return The {@link StatisticsDao}.
     */
    public abstract StatisticsDao statisticsDao();

}