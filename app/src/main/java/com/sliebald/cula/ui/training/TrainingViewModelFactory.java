package com.sliebald.cula.ui.training;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.pojos.TrainingData;
import com.sliebald.cula.utilities.InjectorUtils;

class TrainingViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final CulaRepository mCulaRepository;
    private final TrainingData mTrainingData;

    TrainingViewModelFactory(TrainingData trainingData) {
        mCulaRepository = InjectorUtils.provideRepository();
        mTrainingData = trainingData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new TrainingViewModel(mCulaRepository, mTrainingData);
    }
}
