package com.sliebald.cula.ui.lessons;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.sliebald.cula.R;
import com.sliebald.cula.data.database.entities.LessonEntry;
import com.sliebald.cula.databinding.FragmentLessonsBinding;
import com.sliebald.cula.utilities.SortUtils;

/**
 * A fragment presenting a list of {@link LessonEntry}s and the possibility to add new ones.
 */
public class LessonsFragment extends Fragment implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        LessonsRecyclerViewAdapter.OnItemClickListener, SortUtils.OnSortChangedListener {

    /**
     * Tag for logging and fragment identification.
     */
    public static final String TAG = LessonsFragment.class.getSimpleName();

    /**
     * Data binding of the layout.
     */
    private FragmentLessonsBinding mBinding;

    /**
     * {@link androidx.lifecycle.ViewModel} of the Fragment.
     */
    private LessonsViewModel mViewModel;

    /**
     * The adapter used to show the lessons.
     */
    private LessonsRecyclerViewAdapter mAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LessonsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null || getContext() == null)
            return;
        mViewModel = new ViewModelProvider(this).get(LessonsViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getLessonEntries().observe(this, lessonEntries -> {
            if (lessonEntries == null || lessonEntries.size() == 0) {
                Snackbar snackbar = Snackbar
                        .make(mBinding.lessonCoordinatorLayout, R.string.lesson_add_first_lesson,
                                Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.add, (View view) -> onLessonEntryClick(-1));
                snackbar.show();
            }
            mAdapter.swapEntries(lessonEntries);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize Data Binding

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lessons, container, false);

        // Set the adapter
        Context context = mBinding.recyclerViewLessonsList.getContext();
        mBinding.recyclerViewLessonsList.setLayoutManager(new LinearLayoutManager(context));

        if (getContext() == null)
            return mBinding.getRoot();

        mAdapter = new LessonsRecyclerViewAdapter(this);
        mBinding.recyclerViewLessonsList.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mBinding
                .recyclerViewLessonsList);


        //Set the setOnClickListener for the Floating Action Button
        mBinding.fabAddLesson.setOnClickListener(v -> onLessonEntryClick(-1));
        mBinding.fabAddLesson.setImageDrawable(new IconicsDrawable(getContext()).icon(FontAwesome
                .Icon.faw_plus).color(Color.WHITE).sizeDp(24));

        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    /**
     * Called when the recycler view is swiped.
     * The swiped item  will be removed with an undo option.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof LessonsRecyclerViewAdapter.ViewHolder) {

            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from the viewModel
            mViewModel.removeLessonEntry(deletedIndex);
            // show undo option
            Snackbar snackbar = Snackbar
                    .make(mBinding.lessonCoordinatorLayout, R.string.lesson_deleted, Snackbar
                            .LENGTH_LONG);
//            snackbar.setAction(R.string.undo, (View view) -> {
//                Snackbar.make(mBinding.lessonCoordinatorLayout, R.string.restored, Snackbar
//                        .LENGTH_SHORT);
//                mViewModel.restoreLatestDeletedLessonEntry();
//            });
            snackbar.show();
        }
    }


    @Override
    public void onLessonEntryClick(int id) {
        LessonsFragmentDirections.ActionLessonsDestToUpdateLessonDest action =
                LessonsFragmentDirections.actionLessonsDestToUpdateLessonDest(id);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.sort_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sort) {// Open a sort dialog and set this fragment as target for the callback.
            LessonSortDialog lessonSortDialog = new LessonSortDialog();
            Bundle args = new Bundle();
            args.putString(SortUtils.KEY_ACTIVE_SORT_BY, mViewModel.getCurrentSortType().name());
            args.putBoolean(SortUtils.KEY_ACTIVE_SORT_ORDER, mViewModel.getCurrentSortOrder());
            lessonSortDialog.setArguments(args);
            lessonSortDialog.setTargetFragment(this, 1);
            lessonSortDialog.show(getParentFragmentManager(), "SortDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateSortOrderClick(SortUtils.SortType type, boolean asc) {
        Log.d("test", "called " + type + " " + asc);
        mViewModel.sortLessonsBy(type, asc);
    }
}
