package com.sliebald.cula.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sliebald.cula.data.database.CulaDatabase;
import com.sliebald.cula.data.database.entities.LibraryEntry;

import java.util.List;

/**
 * {@link Dao} which provides an api for all data operations with the {@link CulaDatabase}
 * related to the library.
 */
@Dao
public interface LibraryDao {

    /**
     * Inserts a {@link LibraryEntry} into the library table. If there is a conflicting id the
     * libraryEntry uses the {@link OnConflictStrategy} of replacing the word pair.
     * The required uniqueness of these values is defined in the {@link LibraryEntry}.
     *
     * @param libraryEntry A list of {@link LibraryEntry}s to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(LibraryEntry... libraryEntry);

    /**
     * Gets all {@link LibraryEntry}s in the library database table matching the current language.
     *
     * @return {@link LiveData} with all matching @{@link LibraryEntry}s.
     */
    @Query("SELECT id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated " +
            "FROM library " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "ORDER BY nativeWord DESC")
    LiveData<List<LibraryEntry>> getAllEntries();

    /**
     * Updates a {@link LibraryEntry} into the library table.
     *
     * @param libraryEntries A list of {@link LibraryEntry}s to update
     */
    @Update
    void updateEntry(LibraryEntry... libraryEntries);

    /**
     * Gets the {@link LibraryEntry} in the library database table with the given id.
     *
     * @param id The id of the entry.
     * @return {@link LiveData} with the @{@link LibraryEntry}.
     */
    @Query("SELECT id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated FROM " +
            "library WHERE id=:id")
    LiveData<LibraryEntry> getEntryById(int id);

    /**
     * Get the current amount of entries in the library table.
     *
     * @return The current size.
     */
    @Query("Select count(id) from library")
    int getLibrarySize();

    /**
     * Delete the given entry from the library table.
     */
    @Delete
    void deleteEntry(LibraryEntry entry);


    @Query("SELECT id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated " +
            "FROM library " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "AND knowledgeLevel>=:minKnowledgeLevel " +
            "AND knowledgeLevel<=:maxKnowledgeLevel " +
            "ORDER by RANDOM() " +
            "LIMIT :number")
    LiveData<List<LibraryEntry>> getTrainingEntries(int number, double
            minKnowledgeLevel, double maxKnowledgeLevel);


    @Query("SELECT library.id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated " +
            "FROM library LEFT OUTER JOIN lesson_mapping ON library.id  = libraryEntryId " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "AND knowledgeLevel>=:minKnowledgeLevel " +
            "AND knowledgeLevel<=:maxKnowledgeLevel " +
            "AND :lessonId=lessonEntryId " +
            "ORDER by RANDOM() " +
            "LIMIT :number")
    LiveData<List<LibraryEntry>> getTrainingEntries(int number, double
            minKnowledgeLevel, double maxKnowledgeLevel, int lessonId);

}