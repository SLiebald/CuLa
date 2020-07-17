package com.sliebald.cula.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

/**
 * An @[Entity] Describing Statistic resulting from training.
 *
 * @param lessonEntryID The [LessonEntry] ID of the lesson to which this [StatisticEntry] belongs.
 * @param libraryEntryID The id of the [LibraryEntry] that was trained
 * @param successRate Specifies how successful was the training. Can take values between 0 and 1.
 *          If greater or smaller automatically rounded to the nearer value.
 *          1 means successful/correct trained, 0 means wrong.
 *          Intermediate values reserved for future use.
 * @param id The Id of the Entry in the Database.
 * @param trainingDate  Timestamp when the [StatisticEntry] was last updated.
 */
@Entity(
        tableName = "statistics",
        foreignKeys = [
            ForeignKey(
                    entity = LessonEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["lessonEntryID"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            ),
            ForeignKey(
                    entity = LibraryEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["libraryEntryID"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE)
        ],
        indices = [
            Index(value = ["lessonEntryID"]),
            Index(value = ["libraryEntryID"])
        ]
)
data class StatisticEntry(@PrimaryKey(autoGenerate = true) val id: Int = 0, val libraryEntryID: Int, val lessonEntryID: Int?, var successRate: Double = 0.0, val trainingDate: Date = Date()) {

    constructor(libraryEntryID: Int, lessonEntryID: Int?, successRate: Double) : this(0, libraryEntryID, lessonEntryID, successRate, Date())

    // TODO: remove secondary constructors in favor of using named arguments when the codebase is all kotlin
    init {
        if (successRate < 0) successRate = 0.0 else if (successRate > 1) successRate = 1.0
    }
}