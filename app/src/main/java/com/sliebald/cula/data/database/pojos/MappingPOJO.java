package com.sliebald.cula.data.database.pojos;

import androidx.room.ColumnInfo;

import com.sliebald.cula.data.database.entities.LibraryEntry;

/**
 * Simple POJO that contains the required data of a {@link LibraryEntry} to map it to a lesson.
 * LessonID not stored, since only entries for a single lesson will be queried at a time and the
 * lessonID is therefore implicit.
 */
public class MappingPOJO {

    /**
     * Info whether the {@link LibraryEntry} is part of the lesson that was queried.
     */
    @ColumnInfo(name = "partOfLesson")
    public boolean partOfLesson;
    /**
     * Id of the {@link LibraryEntry}.
     */
    @ColumnInfo(name = "id")
    private int libraryId;
    /**
     * Native word of the {@link LibraryEntry}.
     */
    private String nativeWord;
    /**
     * Foreign word of the {@link LibraryEntry}.
     */
    private String foreignWord;
    /**
     * The Knowledge Level of the {@link LibraryEntry}.
     */
    @ColumnInfo(name = "knowledgeLevel")
    private double knowledgeLevel;

    /**
     * Getter for the native Word of the {@link LibraryEntry} linked to this entry.
     *
     * @return The native Word of the {@link LibraryEntry}
     */
    public String getNativeWord() {
        return nativeWord;
    }

    public void setNativeWord(String nativeWord) {
        this.nativeWord = nativeWord;
    }

    /**
     * Getter for the foreign Word of the {@link LibraryEntry} linked to this entry.
     *
     * @return The foreign Word of the {@link LibraryEntry}
     */
    public String getForeignWord() {
        return foreignWord;
    }

    public void setForeignWord(String foreignWord) {
        this.foreignWord = foreignWord;
    }

    /**
     * Getter for the Id of the {@link LibraryEntry} linked to this entry.
     *
     * @return The Id of the {@link LibraryEntry}
     */
    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
    }

    /**
     * Getter for the knowledge day of the {@link LibraryEntry}.
     *
     * @return knowledge day of the {@link LibraryEntry}.
     */
    public double getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(double knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    /**
     * Boolean that defines whether this entry is part of the lesson that was given to the query
     * returning a list of {@link MappingPOJO}s.
     *
     * @return True if part of that lesson, false otherwise.
     */
    public boolean isPartOfLesson() {
        return partOfLesson;
    }

    public void setPartOfLesson(boolean partOfLesson) {
        this.partOfLesson = partOfLesson;
    }

    @Override
    public String toString() {
        return "MappingPOJO{" +
                "libraryId=" + libraryId +
                ", nativeWord='" + nativeWord + '\'' +
                ", foreignWord='" + foreignWord + '\'' +
                ", partOfLesson=" + partOfLesson +
                ", knowledgeLevel=" + knowledgeLevel +
                '}';
    }

}
