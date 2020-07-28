package com.sliebald.cula.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sliebald.cula.data.database.entities.LanguageEntry

/**
 * [Dao] which provides an api for all data operations with the [CulaDatabase]
 * related to the languages.
 */
@Dao
interface LanguageDao {
    /**
     * Inserts a [LanguageEntry] into the language table. If there is a conflicting id the
     * [LanguageEntry] uses the [OnConflictStrategy] to abort if the
     * [LanguageEntry] already exists.
     * The required uniqueness of these values is defined in the [LanguageEntry].
     *
     * @param languageEntries A list of [LanguageEntry]s to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEntry(vararg languageEntries: LanguageEntry)
    //TODO: make sure only one entry has isActive set to true at a time

    /**
     * Gets all [LanguageEntry]s in the language database table.
     *
     * @return [LiveData] with all @[LanguageEntry]s.
     */
    @Query("SELECT language, isActive FROM language ORDER by language desc")
    fun getAllEntries(): LiveData<List<LanguageEntry>>

    /**
     * Get the current amount of entries in the language table.
     *
     * @return The current size.
     */
    @Query("Select count(language) from language")
    fun getAmountOfLanguages(): Int

    /**
     * Delete the given [LanguageEntry] from the language table.
     */
    @Delete
    fun deleteEntry(entry: LanguageEntry)

    /**
     * Gets the active Language [LanguageEntry]s in the language database table.
     *
     * @return [LiveData] with the active @[LanguageEntry]s. Null if none is active.
     */
    @Query("SELECT language, isActive FROM language WHERE isActive=1 LIMIT 1")
    fun getActiveLanguage(): LiveData<LanguageEntry?>

    /**
     * Updates a [LanguageEntry] in the language table.
     *
     * @param language The language of the language to set to active
     */
    @Query("UPDATE language SET isActive = CASE WHEN language IS :language THEN 1 ELSE 0 END")
    fun setActiveLanguage(language: String)
}