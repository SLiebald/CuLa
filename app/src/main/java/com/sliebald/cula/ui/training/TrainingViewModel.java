package com.sliebald.cula.ui.training;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.data.database.entities.StatisticEntry;
import com.sliebald.cula.data.database.pojos.TrainingData;
import com.sliebald.cula.ui.updateLibrary.UpdateLibraryFragment;
import com.sliebald.cula.utilities.KnowledgeLevelUtils;

/**
 * {@link ViewModel} for the {@link UpdateLibraryFragment}.
 */
class TrainingViewModel extends ViewModel {

    /**
     * The {@link TrainingData} that defines the training.
     */
    private final TrainingData mTrainingData;
    private CulaRepository mRepository;
    /**
     * Currently learned word in the list of {@link LibraryEntry}s that should be trained, which
     * is stored in the {@link TrainingData}.
     */
    private int currentIndex;

    /**
     * Constructor.
     *
     * @param repository   Takes the {@link CulaRepository}
     * @param trainingData The {@link TrainingData} that defines the training.
     */
    TrainingViewModel(CulaRepository repository, TrainingData trainingData) {
        this.mTrainingData = trainingData;
        mRepository = repository;
        currentIndex = 0;
    }

    /**
     * Checks whether there is a next LibraryEntry to learn.
     *
     * @return The next entry to learn.
     */
    boolean hasNextEntry() {
        return mTrainingData.getTrainingEntries().size() > currentIndex + 1;
    }

    /**
     * Returns the current word to learn.
     *
     * @return The current word to learn.
     */
    String getCurrentWord() {
        return mTrainingData.isReverseTraining()
                ? mTrainingData.getTrainingEntries().get(currentIndex).getForeignWord()
                : mTrainingData.getTrainingEntries().get(currentIndex).getNativeWord();
    }

    /**
     * Returns the current position to learn.
     *
     * @return The current position to learn.
     */
    int getLearningSetPosition() {
        return currentIndex + 1;
    }

    /**
     * Returns the size of the learning set.
     *
     * @return The size of the current learning set.
     */
    int getLearningSetSize() {
        return mTrainingData.getTrainingEntries().size();
    }

    private void updateStatistics(double successRate) {
        //TODO: handle lessonId for statistics correctly, not just null
        mRepository.insertStatisticsEntry(new StatisticEntry(mTrainingData.getTrainingEntries().get(currentIndex).getId(), null, successRate));
    }

    /**
     * Returns the correct translation for the current word.
     *
     * @return The correct Translation.
     */
    String getCorrectTranslation() {
        return mTrainingData.isReverseTraining()
                ? mTrainingData.getTrainingEntries().get(currentIndex).getNativeWord()
                : mTrainingData.getTrainingEntries().get(currentIndex).getForeignWord();
    }

    /**
     * Continues with the next word.
     */
    void next() {
        currentIndex++;
    }

    boolean checkTranslationCorrect(@NonNull String typedTranslation) {
        LibraryEntry currentEntry = mTrainingData.getTrainingEntries().get(currentIndex);
        boolean success = typedTranslation.trim().toLowerCase().equals(getCorrectTranslation().trim().toLowerCase());
        double updatedKnowledgeLevel = KnowledgeLevelUtils.calculateKnowledgeLevelAdjustment(currentEntry.getKnowledgeLevel(), success);
        updateStatistics(success ? 1 : 0);
        currentEntry.setKnowledgeLevel(updatedKnowledgeLevel);
        mRepository.updateLibraryEntry(currentEntry);
        return success;
    }

    boolean isTrainingOver() {
        return currentIndex >= mTrainingData.getTrainingEntries().size();
    }
}
