package com.sliebald.cula.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sliebald.cula.data.database.CulaDatabase;
import com.sliebald.cula.data.database.entities.LanguageEntry;

import java.util.List;

/**
 * {@link Dao} which provides an api for all data operations with the {@link CulaDatabase}
 * related to the languages.
 */
@Dao
public interface LanguageDao {

    /**
     * Inserts a {@link LanguageEntry} into the language table. If there is a conflicting id the
     * {@link LanguageEntry} uses the {@link OnConflictStrategy} to abort if the
     * {@link LanguageEntry} already exists.
     * The required uniqueness of these values is defined in the {@link LanguageEntry}.
     *
     * @param languageEntries A list of {@link LanguageEntry}s to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEntry(LanguageEntry... languageEntries);

    //TODO: make sure only one entry has isActive set to true at a time

    /**
     * Gets all {@link LanguageEntry}s in the language database table.
     *
     * @return {@link LiveData} with all @{@link LanguageEntry}s.
     */
    @Query("SELECT language, isActive FROM language ORDER by language desc")
    LiveData<List<LanguageEntry>> getAllEntries();

    /**
     * Get the current amount of entries in the language table.
     *
     * @return The current size.
     */
    @Query("Select count(language) from language")
    int getAmountOfLanguages();

    /**
     * Delete the given {@link LanguageEntry} from the language table.
     */
    @Delete
    void deleteEntry(LanguageEntry entry);

    /**
     * Gets the active Language {@link LanguageEntry}s in the language database table.
     *
     * @return {@link LiveData} with the active @{@link LanguageEntry}s. Null if none is active.
     */
    @Query("SELECT language, isActive FROM language WHERE isActive=1 LIMIT 1")
    LiveData<LanguageEntry> getActiveLanguage();

    /**
     * Updates a {@link LanguageEntry} in the language table.
     *
     * @param language The language of the language to set to active
     */
    @Query("UPDATE language SET isActive = CASE WHEN language IS :language THEN 1 ELSE 0 END")
    void setActiveLanguage(String language);
}