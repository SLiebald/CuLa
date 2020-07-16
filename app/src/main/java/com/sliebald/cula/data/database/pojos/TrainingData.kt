package com.sliebald.cula.data.database.pojos

import android.os.Parcelable
import com.sliebald.cula.data.database.entities.LibraryEntry
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * POJO that describes all data required to initiate a training session and store statistics
 * about it.
 *
 * @param lessonId The id of the lesson to train. -1 means all lessons.
 * @param isReverseTraining Direction of training. (true: foreign-> native - false: native->foreign).
 * @param trainingEntries List of [LibraryEntry]s that are trained.
 */
@Parcelize
data class TrainingData(
        val lessonId: Int,
        val isReverseTraining: Boolean,
        val trainingEntries: List<@RawValue LibraryEntry?>
) : Parcelable