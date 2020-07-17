package com.sliebald.cula.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sliebald.cula.data.database.entities.LibraryEntry

/**
 * [Dao] which provides an api for all data operations with the [CulaDatabase]
 * related to the library.
 */
@Dao
interface LibraryDao {
    /**
     * Inserts a [LibraryEntry] into the library table. If there is a conflicting id the
     * libraryEntry uses the [OnConflictStrategy] of replacing the word pair.
     * The required uniqueness of these values is defined in the [LibraryEntry].
     *
     * @param libraryEntry A list of [LibraryEntry]s to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(vararg libraryEntry: LibraryEntry)

    /**
     * Gets all [LibraryEntry]s in the library database table matching the current language.
     *
     * @return [LiveData] with all matching @[LibraryEntry]s.
     */
    @Query("SELECT id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated " +
            "FROM library " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "ORDER BY nativeWord DESC")
    fun getAllEntries(): LiveData<List<LibraryEntry>>

    /**
     * Updates a [LibraryEntry] into the library table.
     *
     * @param libraryEntries A list of [LibraryEntry]s to update
     */
    @Update
    fun updateEntry(vararg libraryEntries: LibraryEntry)

    /**
     * Gets the [LibraryEntry] in the library database table with the given id.
     *
     * @param id The id of the entry.
     * @return [LiveData] with the @[LibraryEntry].
     */
    @Query("SELECT id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated FROM " +
            "library WHERE id=:id")
    fun getEntryById(id: Int): LiveData<LibraryEntry>

    /**
     * Get the current amount of entries in the library table.
     *
     * @return The current size.
     */
    @Query("Select count(id) from library")
    fun getLibrarySize(): Int

    /**
     * Delete the given entry from the library table.
     */
    @Delete
    fun deleteEntry(entry: LibraryEntry)

    @Query("SELECT id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated " +
            "FROM library " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "AND knowledgeLevel>=:minKnowledgeLevel " +
            "AND knowledgeLevel<=:maxKnowledgeLevel " +
            "ORDER by RANDOM() " +
            "LIMIT :number")
    fun getTrainingEntries(number: Int, minKnowledgeLevel: Double, maxKnowledgeLevel: Double): LiveData<List<LibraryEntry>>

    @Query("SELECT library.id, nativeWord, foreignWord, language, knowledgeLevel, lastUpdated " +
            "FROM library LEFT OUTER JOIN lesson_mapping ON library.id  = libraryEntryId " +
            "WHERE language = (SELECT language FROM language WHERE isActive=1 LIMIT 1) " +
            "AND knowledgeLevel>=:minKnowledgeLevel " +
            "AND knowledgeLevel<=:maxKnowledgeLevel " +
            "AND :lessonId=lessonEntryId " +
            "ORDER by RANDOM() " +
            "LIMIT :number")
    fun getTrainingEntries(number: Int, minKnowledgeLevel: Double, maxKnowledgeLevel: Double, lessonId: Int): LiveData<List<LibraryEntry>>
}