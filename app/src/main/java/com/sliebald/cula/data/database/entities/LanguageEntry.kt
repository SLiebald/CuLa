package com.sliebald.cula.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * An @[Entity] Describing a foreign language that can be trained.
 *
 * @param language The foreign language.
 * @param isActive Whether this language is the currently active language.
 */
@ExperimentalStdlibApi // required for capitalize
@Entity(tableName = "language")
data class LanguageEntry(@PrimaryKey var language: String, val isActive: Boolean) {
    init {
        language = language.capitalize(Locale.getDefault())
    }
}