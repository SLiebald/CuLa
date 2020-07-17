package com.sliebald.cula.data.database.entities

import androidx.room.*

/**
 * An @[Entity] Describing a lesson.
 *
 * @param id The Id of the [LessonEntry] in the Database.
 * @param lessonName The name of the lesson.
 * @param lessonDescription The description of the lesson.
 * @param language The language the lesson belongs to.
 */
@Entity(
        tableName = "lesson",
        foreignKeys = [
            ForeignKey(
                    entity = LanguageEntry::class,
                    parentColumns = ["language"],
                    childColumns = ["language"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE)
        ],
        indices = [Index(value = ["language"])])
data class LessonEntry(
        val lessonName: String,
        val lessonDescription: String,
        val language: String,
        @PrimaryKey(autoGenerate = true) val id: Int = 0
) {
    // TODO: remove secondary constructors in favor of using named arguments when the codebase is all kotlin
    @Ignore
    constructor(
            lessonName: String,
            lessonDescription: String,
            language: String
    ) : this(lessonName, lessonDescription, language, 0)

}