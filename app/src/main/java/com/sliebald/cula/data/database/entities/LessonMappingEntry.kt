package com.sliebald.cula.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An @[Entity] Describing a mapping between a [LessonEntry] and a [LibraryEntry].
 *
 * @param lessonEntryId  The id of the [LessonEntry] mapped to a [LibraryEntry].
 * @param libraryEntryId The id of the [LibraryEntry] mapped to the [LessonEntry].
 * @param id The id of the mapping entry in the database. Set to 0 or omit for autogeneration.
 */
@Entity(
        tableName = "lesson_mapping",
        foreignKeys = [
            ForeignKey(
                    entity = LessonEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["lessonEntryId"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            ),
            ForeignKey(
                    entity = LibraryEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["libraryEntryId"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ],
        indices = [
            Index(value = ["lessonEntryId"]),
            Index(value = ["libraryEntryId"])
        ]
)
data class LessonMappingEntry(
        val lessonEntryId: Int,
        val libraryEntryId: Int,
        @PrimaryKey(autoGenerate = true) val id: Int = 0
) {
    // TODO: remove secondary constructors in favor of using named arguments when the codebase is all kotlin
    constructor(lessonEntryId: Int, libraryEntryId: Int) : this(lessonEntryId, libraryEntryId, 0)
}