package com.sliebald.cula.data.database.entities

import androidx.room.*
import java.util.*

/**
 * An @[Entity] Describing a word pair.
 *
 * @param nativeWord The word in the native language.
 * @param foreignWord The word in the foreign language.
 * @param language To which language as described in an [LanguageEntry] does this word belong.
 * @param id The Id of the Entry in the Database.
 * @param knowledgeLevel The Knowledge Level of the [LibraryEntry].
 * @param lastUpdated Timestamp when the [LibraryEntry] was last updated.
 */
@Entity(
        tableName = "library",
        foreignKeys = [
            ForeignKey(
                    entity = LanguageEntry::class,
                    parentColumns = ["language"],
                    childColumns = ["language"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE)
        ],
        indices = [Index(value = ["language"])]
)
data class LibraryEntry(
        val nativeWord: String,
        val foreignWord: String,
        val language: String,
        var knowledgeLevel: Double,
        var lastUpdated: Date = Date(),
        @PrimaryKey(autoGenerate = true) val id: Int = 0
) {

    // TODO: remove secondary constructors in favor of using named arguments when the codebase is all kotlin
    @Ignore
    constructor(
            nativeWord: String,
            foreignWord: String,
            language: String,
            knowledgeLevel: Double,
            lastUpdated: Date
    ) : this(nativeWord, foreignWord, language, knowledgeLevel, lastUpdated, 0)

    // TODO: remove secondary constructors in favor of using named arguments when the codebase is all kotlin
    @Ignore
    constructor(
            nativeWord: String,
            foreignWord: String,
            language: String,
            knowledgeLevel: Double
    ) : this(nativeWord, foreignWord, language, knowledgeLevel, Date())
}