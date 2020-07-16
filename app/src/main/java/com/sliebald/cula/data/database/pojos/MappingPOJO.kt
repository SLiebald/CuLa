package com.sliebald.cula.data.database.pojos

import androidx.room.ColumnInfo

/**
 * Simple POJO that contains the required data of a [LibraryEntry] to map it to a lesson.
 * LessonID not stored, since only entries for a single lesson will be queried at a time and the
 * lessonID is therefore implicit.
 *
 * @param isPartOfLesson Defines whether this entry is part of the lesson that was given to the query returning a list of [MappingPOJO]s.
 * @param libraryId The Id of the [LibraryEntry]
 * @param nativeWord Native word of the [LibraryEntry].
 * @param foreignWord Foreign word of the [LibraryEntry].
 * @param knowledgeLevel The current Knowledge Level of the [LibraryEntry].
 */
data class MappingPOJO(
        @ColumnInfo(name = "partOfLesson") var isPartOfLesson: Boolean = false,
        @ColumnInfo(name = "id") var libraryId: Int = 0,
        var nativeWord: String = "",
        var foreignWord: String = "",
        var knowledgeLevel: Double = 0.0
)