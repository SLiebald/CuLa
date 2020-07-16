package com.sliebald.cula.data.database.pojos

/**
 * Simple POJO class that holds a lesson and the average of Knowledge Levels of all words in that
 * lesson.
 *
 * @param lessonName Lesson Name.
 * @param average Average of KnowledgeLevel of all [com.sliebald.cula.data.database.entities.LibraryEntry]s assigned to the lesson.
 */
data class LessonKnowledgeLevel(var lessonName: String? = null, var average: Double = 0.0)