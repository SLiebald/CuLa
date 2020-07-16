package com.sliebald.cula.ui.startTraining;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.sliebald.cula.R;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.data.database.pojos.TrainingData;
import com.sliebald.cula.databinding.FragmentStartTrainingBinding;

/**
 * The fragment for starting a training session.
 */
public class StartTrainingFragment extends Fragment {

    /**
     * Tag for logging and fragment identification.
     */
    public static final String TAG = StartTrainingFragment.class.getSimpleName();

    /**
     * Key for the savedInstanceState selected lesson.
     */
    private static final String SAVED_INSTANCE_STATE_SELECTED_LESSON_KEY = "selectedLesson";

    /**
     * The data binding for the layout.
     */
    private FragmentStartTrainingBinding mBinding;

    /**
     * The viewModel for the {@link StartTrainingFragment}.
     */
    private StartTrainingViewModel mViewModel;

    public StartTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null || getContext() == null)
            return;
        // Create the ViewModel for this fragment.
        mViewModel = new ViewModelProvider(this).get(StartTrainingViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start_training, container, false);

        // set listener for start training button
        mBinding.btStartTraining.setOnClickListener(view -> startTraining());

        //TODO: set initially selected options based on last session (sharedParameters).

        // Set the correct Entries for the number of words to learn spinner.
        ArrayAdapter<CharSequence> num_words_adapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.training_number_of_words, android.R.layout.simple_spinner_item);
        num_words_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spStartTrainingNumberOfWords.setAdapter(num_words_adapter);
        mBinding.spStartTrainingNumberOfWords.setSelectedIndex(1);

        // Set the correct Entries for the Knowledge Level Range Spinner.
        ArrayAdapter<CharSequence> knowledgeLevel_range_adapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.knowledgeLevel_range, android.R.layout.simple_spinner_item);
        knowledgeLevel_range_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spStartTrainingKnowledgeLevelRange.setAdapter(knowledgeLevel_range_adapter);
        mBinding.spStartTrainingKnowledgeLevelRange.setSelectedIndex(2);

        // Set the correct Entries for the Knowledge Level Spinner.
        ArrayAdapter<CharSequence> knowledgeLevel_adapter =
                ArrayAdapter.createFromResource(requireContext(), R.array.knowledgeLevel, android.R.layout.simple_spinner_item);
        knowledgeLevel_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spStartTrainingKnowledgeLevel.setAdapter(knowledgeLevel_adapter);
        mBinding.spStartTrainingKnowledgeLevel.setSelectedIndex(4);

        // Set the correct Entries for the Lessons taken from the database.
        mViewModel.getLessonEntries().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null) {
                String[] lessonNames = new String[entries.size() + 1];
                lessonNames[0] = getResources().getString(R.string.start_training_lessons_any);
                int index = 1;
                for (LessonEntry entry : entries) {
                    lessonNames[index] = entry.getLessonName();
                    index++;
                }
                ArrayAdapter<CharSequence> lesson_adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, lessonNames);
                lesson_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBinding.spStartTrainingLesson.setAdapter(lesson_adapter);
                if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_SELECTED_LESSON_KEY))
                    mBinding.spStartTrainingLesson.setSelectedIndex(savedInstanceState.getInt(SAVED_INSTANCE_STATE_SELECTED_LESSON_KEY, 0));
            }
        });
        return mBinding.getRoot();
    }

    /**
     * Start the training fragment and pass the elements that should be trained to it.
     */
    private void startTraining() {
        LiveData<TrainingData> data = mViewModel.getTrainingData(getSelectedNumWords(),
                getSelectedMinKnowledgeLevel(), getSelectedMaxKnowledgeLevel(),
                getSelectedLessonId(), getSelectedReverseTraining());
        data.observe(getViewLifecycleOwner(), new Observer<TrainingData>() {
            @Override
            public void onChanged(TrainingData trainingData) {
                data.removeObserver(this);
                if (trainingData.getTrainingEntries().size() > 0) {
                    StartTrainingFragmentDirections.ActionStartTrainingDestToTrainingDest action =
                            StartTrainingFragmentDirections.actionStartTrainingDestToTrainingDest();
                    action.setTrainingData(trainingData);
                    Navigation.findNavController(requireView()).navigate(action);
                } else {
                    Snackbar.make(mBinding.fragmentStartTraining, R.string
                            .start_training_warning_no_matching_words, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        //TODO: save selected options to sharedParameters for next session
    }

    /**
     * Calculate the minimum KnowledgeLevel based on the selected spinner.
     *
     * @return The minimum KnowledgeLevel to check
     */
    private double getSelectedMinKnowledgeLevel() {
        int selectedLevel = mBinding.spStartTrainingKnowledgeLevel.getSelectedIndex() + 1;
        return mBinding.spStartTrainingKnowledgeLevelRange.getSelectedIndex() == 2 || selectedLevel == 1 ? 0 : selectedLevel - 0.5;

    }

    /**
     * Calculate the Maximum KnowledgeLevel based on the selected spinner.
     *
     * @return The maximum KnowledgeLevel to check
     */
    private double getSelectedMaxKnowledgeLevel() {
        int selectedLevel = mBinding.spStartTrainingKnowledgeLevel.getSelectedIndex() + 1;
        return mBinding.spStartTrainingKnowledgeLevelRange.getSelectedIndex() == 0 || selectedLevel == 5 ? 5 : selectedLevel + 0.5;
    }

    /**
     * Returns the id of the selected lesson.
     *
     * @return The id of the selected lesson (-1 if any is selected).
     */
    private int getSelectedLessonId() {
        return mViewModel.getLessonEntries().getValue() == null || mBinding.spStartTrainingLesson.getSelectedIndex() == 0
                ? -1
                : mViewModel.getLessonEntries().getValue().get(mBinding.spStartTrainingLesson.getSelectedIndex() - 1).getId();
    }

    /**
     * Returns how many words should be trained.
     *
     * @return The number of words to learn.
     */
    private int getSelectedNumWords() {
        return Integer.parseInt(mBinding.spStartTrainingNumberOfWords.getItems().get(mBinding
                .spStartTrainingNumberOfWords.getSelectedIndex())
                .toString());
    }

    /**
     * Returns whether reverse training is selected.
     *
     * @return true if reverse training is activated, false otherwise.
     */
    private boolean getSelectedReverseTraining() {
        return mBinding.swStartTrainingReverseTraining.isChecked();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_SELECTED_LESSON_KEY, mBinding.spStartTrainingLesson.getSelectedIndex());
    }
}
