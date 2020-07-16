package com.sliebald.cula.data.database.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.sliebald.cula.data.database.entities.LibraryEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO that describes all data required to initiate a training session and store statistics
 * about it.
 */
public class TrainingData implements Parcelable {

    /**
     * CREATOR for making the {@link TrainingData} Parcelable to pass it via fragments via gradle
     * safeargs fragment.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TrainingData createFromParcel(Parcel in) {
            return new TrainingData(in);
        }

        public TrainingData[] newArray(int size) {
            return new TrainingData[size];
        }
    };

    /**
     * The trained lessonId.
     */
    private int lessonId;
    /**
     * If true, the training is reversed (translate from foreign to own language).
     */
    private boolean reverseTraining;
    /**
     * List of {@link LibraryEntry}s that is trained.
     */
    private List<LibraryEntry> trainingEntries;

    /**
     * Constructor.
     *
     * @param lessonId        The trained lessonId
     * @param reverseTraining If true, the training is reversed (translate from foreign to own language).
     * @param trainingEntries List of {@link LibraryEntry}s that are trained.
     */
    public TrainingData(int lessonId, boolean reverseTraining, List<LibraryEntry> trainingEntries) {
        this.lessonId = lessonId;
        this.reverseTraining = reverseTraining;
        this.trainingEntries = trainingEntries;
    }

    // Parcelling part
    public TrainingData(Parcel in) {
        lessonId = in.readInt();
        reverseTraining = in.readByte() != 0;
        trainingEntries = new ArrayList<>();
        in.readList(trainingEntries, LibraryEntry.class.getClassLoader());
    }

    @NonNull
    @Override
    public String toString() {
        return "TrainingData{" +
                "lessonId=" + lessonId +
                ", reverseTraining=" + reverseTraining +
                ", trainingEntries=" + trainingEntries +
                '}';
    }

    /**
     * The trained lessonId.
     *
     * @return The id of the lesson. -1 means all lessons.
     */
    public int getLessonId() {
        return lessonId;
    }

    /**
     * Indicating direction of training. (foreign-> native or native->foreign).
     *
     * @return If true, the training is reversed (translate from foreign to own language).
     */
    public boolean isReverseTraining() {
        return reverseTraining;
    }

    /**
     * List of {@link LibraryEntry}s that are trained.
     *
     * @return LibraryEntrys to train.
     */
    public List<LibraryEntry> getTrainingEntries() {
        return trainingEntries;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(lessonId);
        dest.writeByte((byte) (reverseTraining ? 1 : 0));
        dest.writeList(trainingEntries);
    }
}
