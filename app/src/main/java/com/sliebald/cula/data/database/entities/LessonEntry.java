package com.sliebald.cula.data.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * An @{@link Entity} Describing a lesson.
 */
@Entity(tableName = "lesson",
        foreignKeys = @ForeignKey(entity = LanguageEntry.class,
                parentColumns = "language",
                childColumns = "language",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        indices = {@Index(value = {"language"})})
public class LessonEntry {

    /**
     * The name of the lesson.
     */
    @NonNull
    private final String lessonName;
    /**
     * The description of the lesson.
     */
    @NonNull
    private final String lessonDescription;
    /**
     * To which language as described in an {@link LanguageEntry} does this word belong.
     */
    @NonNull
    private final String language;
    /**
     * The Id of the {@link LessonEntry} in the Database.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * Constructor for an {@link LessonEntry}.
     *
     * @param id                The Id of the @{@link LessonEntry}.
     * @param lessonName        The name of the {@link LessonEntry}.
     * @param lessonDescription The description of the {@link LessonEntry}.
     * @param language          The language this {@link LessonEntry} belongs to.
     */
    public LessonEntry(int id, @NonNull String lessonName, @NonNull String lessonDescription,
                       @NonNull String language) {
        this(lessonName, lessonDescription, language);
        this.id = id;
    }

    /**
     * Constructor for an {@link LessonEntry}.
     *
     * @param lessonName        The name of the {@link LessonEntry}.
     * @param lessonDescription The description of the {@link LessonEntry}.
     * @param language          The language this {@link LessonEntry} belongs to.
     */
    @Ignore
    public LessonEntry(@NonNull String lessonName, @NonNull String lessonDescription, @NonNull
            String
            language) {
        this.lessonName = lessonName;
        this.lessonDescription = lessonDescription;
        this.language = language;
    }

    /**
     * Getter for the id of this {@link LessonEntry}.
     *
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the name of the lesson.
     *
     * @return The name of the lesson.
     */
    @NonNull
    public String getLessonName() {
        return lessonName;
    }

    /**
     * Getter for the description of the lesson.
     *
     * @return The description of the lesson.
     */
    @NonNull
    public String getLessonDescription() {
        return lessonDescription;
    }

    /**
     * To which language as described in an {@link LanguageEntry} does this word belong.
     *
     * @return The language as String.
     */
    @NonNull
    public String getLanguage() {
        return language;
    }

    @NonNull
    @Override
    public String toString() {
        return "LessonEntry{" +
                "id=" + id +
                ", lessonName='" + lessonName + '\'' +
                ", lessonDescription='" + lessonDescription + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}