package com.sliebald.cula.data.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sliebald.cula.utilities.StringUtils;

/**
 * An @{@link Entity} Describing a foreign language that can be trained.
 */
@Entity(tableName = "language")
public class LanguageEntry {

    /**
     * The foreign language.
     */
    @PrimaryKey
    @NonNull
    private final String language;

    /**
     * Defines whether this entry is the currently active entry.
     */
    private final boolean isActive;

    /**
     * Constructor for an {@link LanguageEntry}.
     *
     * @param language The stored native language word.
     * @param isActive Defines whether this entry is the currently active entry.
     */
    public LanguageEntry(@NonNull String language, boolean isActive) {
        this.language = StringUtils.toFirstCharacterUpperCase(language);
        this.isActive = isActive;
    }

    /**
     * Getter for the foreign language.
     *
     * @return The foreign language.
     */
    @NonNull
    public String getLanguage() {
        return language;
    }

    /**
     * Defines whether this entry is the currently active entry.
     */
    public boolean isActive() {
        return isActive;
    }

    @NonNull
    @Override
    public String toString() {
        return "LanguageEntry{" +
                "language='" + language + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}