package com.sliebald.cula.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sliebald.cula.data.database.CulaDatabase;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.data.database.entities.LessonMappingEntry;
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.data.database.pojos.MappingPOJO;

import java.util.List;

/**
 * {@link Dao} which provides an api for all data operations with the {@link CulaDatabase}
 * related to the lessons.
 */
@Dao
public interface LessonDao {

    /**
     * Inserts a {@link LessonEntry} into the lessons table. If there is a conflicting id the
     * {@link LessonEntry} uses the {@link OnConflictStrategy} to abort if an according entry
     * already exists. The required uniqueness of these values is defined in the
     * {@link LessonEntry}.
     *
     * @param lessonEntries A list of {@link LessonEntry}s to insert
     * @return The ids of the new {@link LessonEntry}s.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertEntry(LessonEntry... lessonEntries);

    /**
     * Updates a {@link LessonEntry} into the lessons table.
     *
     * @param lessonEntries A list of {@link LessonEntry}s to update
     */
    @Update
    void updateEntry(LessonEntry... lessonEntries);

    /**
     * Deletes the given {@link LessonEntry}s from the database.
     *
     * @param lessonEntries The {@link LessonEntry}s to delete.
     */
    @Delete
    void deleteEntry(LessonEntry... lessonEntries);

    /**
     * Inserts a {@link LessonMappingEntry} into the lesson_mapping table. If there is a
     * conflicting id the {@link LessonMappingEntry} uses the {@link OnConflictStrategy} to abort
     * if an according entry already exists. The required uniqueness of these values is defined
     * in the {@link LessonMappingEntry}.
     *
     * @param lessonMappingEntries A list of {@link LessonMappingEntry}s to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMappingEntry(LessonMappingEntry... lessonMappingEntries);

    /**
     * Gets all {@link LessonEntry}s in the lesson database table.
     *
     * @return {@link LiveData} with all @{@link LessonEntry}s.
     */
    @Query("SELECT id, lessonName, lessonDescription, language " +
            "FROM lesson  " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "ORDER BY lessonName DESC")
    LiveData<List<LessonEntry>> getAllEntries();


    /**
     * Get the current amount of entries in the lesson table.
     *
     * @return The current size.
     */
    @Query("SELECT count(id) FROM lesson")
    int getAmountOfLessons();

    /**
     * Get the current amount of entries in the lesson_mapping table.
     *
     * @return The current size.
     */
    @Query("SELECT count(id) FROM lesson_mapping")
    int getAmountOfLessonsMappings();

    /**
     * Gets the {@link LessonEntry} in the lesson database table with the given id.
     *
     * @param id The id of the entry.
     * @return {@link LiveData} with the @{@link LessonEntry}.
     */
    @Query("SELECT id, lessonName, lessonDescription, language FROM lesson WHERE id=:id")
    LiveData<LessonEntry> getEntryById(int id);


    /**
     * Gets the List of {@link MappingPOJO}s in the lesson database table with the given id.
     *
     * @param id The lesson id for which the {@link List} of {@link MappingPOJO}s should be
     *           retrieved.
     * @return {@link LiveData} with the {@link List} of @{@link MappingPOJO}s.
     */
    //TODO: probably not the most efficient query.
    @Query("SELECT id, foreignWord, nativeWord, knowledgeLevel, " +
            "CASE WHEN EXISTS(SELECT Id FROM lesson_mapping WHERE libraryEntryId=library.id AND " +
            "lessonEntryId=:id) THEN 1 ELSE 0 END AS partOfLesson " +
            "FROM library " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1)")
    LiveData<List<MappingPOJO>> getLessonMappingById(int id);

    /**
     * Deletes a {@link LessonMappingEntry} between a {@link LessonEntry} and a
     * {@link LibraryEntry}.
     *
     * @param lessonId  The lessonId of the {@link LessonMappingEntry}
     * @param libraryId The libraryId of the {@link LessonMappingEntry}
     */
    @Query("DELETE FROM lesson_mapping WHERE lessonEntryId=:lessonId AND libraryEntryId=:libraryId")
    void deleteMappingEntry(int lessonId, int libraryId);
}
