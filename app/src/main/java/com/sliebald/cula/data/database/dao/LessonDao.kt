package com.sliebald.cula.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sliebald.cula.data.database.entities.LessonEntry
import com.sliebald.cula.data.database.entities.LessonMappingEntry
import com.sliebald.cula.data.database.pojos.MappingPOJO

/**
 * [Dao] which provides an api for all data operations with the [CulaDatabase]
 * related to the lessons.
 */
@Dao
interface LessonDao {
    /**
     * Inserts a [LessonEntry] into the lessons table. If there is a conflicting id the
     * [LessonEntry] uses the [OnConflictStrategy] to abort if an according entry
     * already exists. The required uniqueness of these values is defined in the
     * [LessonEntry].
     *
     * @param lessonEntries A list of [LessonEntry]s to insert
     * @return The ids of the new [LessonEntry]s.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEntry(vararg lessonEntries: LessonEntry): LongArray

    /**
     * Updates a [LessonEntry] into the lessons table.
     *
     * @param lessonEntries A list of [LessonEntry]s to update
     */
    @Update
    fun updateEntry(vararg lessonEntries: LessonEntry)

    /**
     * Deletes the given [LessonEntry]s from the database.
     *
     * @param lessonEntries The [LessonEntry]s to delete.
     */
    @Delete
    fun deleteEntry(vararg lessonEntries: LessonEntry)

    /**
     * Inserts a [LessonMappingEntry] into the lesson_mapping table. If there is a
     * conflicting id the [LessonMappingEntry] uses the [OnConflictStrategy] to abort
     * if an according entry already exists. The required uniqueness of these values is defined
     * in the [LessonMappingEntry].
     *
     * @param lessonMappingEntries A list of [LessonMappingEntry]s to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMappingEntry(vararg lessonMappingEntries: LessonMappingEntry)

    /**
     * Gets all [LessonEntry]s in the lesson database table.
     *
     * @return [LiveData] with all @[LessonEntry]s.
     */
    @Query("SELECT id, lessonName, lessonDescription, language " +
            "FROM lesson  " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "ORDER BY lessonName DESC")
    fun getAllEntries(): LiveData<List<LessonEntry>>

    /**
     * Get the current amount of entries in the lesson table.
     *
     * @return The current size.
     */
    @Query("SELECT count(id) FROM lesson")
    fun getAmountOfLessons(): Int

    /**
     * Get the current amount of entries in the lesson_mapping table.
     *
     * @return The current size.
     */
    @Query("SELECT count(id) FROM lesson_mapping")
    fun getAmountOfLessonsMappings(): Int

    /**
     * Gets the [LessonEntry] in the lesson database table with the given id.
     *
     * @param id The id of the entry.
     * @return [LiveData] with the @[LessonEntry].
     */
    @Query("SELECT id, lessonName, lessonDescription, language FROM lesson WHERE id=:id")
    fun getEntryById(id: Int): LiveData<LessonEntry>

    /**
     * Gets the List of [MappingPOJO]s in the lesson database table with the given id.
     *
     * @param id The lesson id for which the [List] of [MappingPOJO]s should be
     * retrieved.
     * @return [LiveData] with the [List] of @[MappingPOJO]s.
     */
    //TODO: probably not the most efficient query.
    @Query("SELECT id, foreignWord, nativeWord, knowledgeLevel, " +
            "CASE WHEN EXISTS(SELECT Id FROM lesson_mapping WHERE libraryEntryId=library.id AND " +
            "lessonEntryId=:id) THEN 1 ELSE 0 END AS partOfLesson " +
            "FROM library " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1)")
    fun getLessonMappingById(id: Int): LiveData<List<MappingPOJO>>

    /**
     * Deletes a [LessonMappingEntry] between a [LessonEntry] and a
     * [LibraryEntry].
     *
     * @param lessonId  The lessonId of the [LessonMappingEntry]
     * @param libraryId The libraryId of the [LessonMappingEntry]
     */
    @Query("DELETE FROM lesson_mapping WHERE lessonEntryId=:lessonId AND libraryEntryId=:libraryId")
    fun deleteMappingEntry(lessonId: Int, libraryId: Int)
}