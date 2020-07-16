package com.sliebald.cula.ui.library;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.databinding.FragmentLibraryBinding;
import com.sliebald.cula.utilities.SortUtils;

/**
 * A fragment presenting a list of {@link LibraryEntry}s and the possibility to add new ones.
 */
public class LibraryFragment extends Fragment implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        LibraryRecyclerViewAdapter.OnItemClickListener, SortUtils.OnSortChangedListener {

    public static final String TAG = LibraryFragment.class.getSimpleName();
    private int mPosition = RecyclerView.NO_POSITION;
    private FragmentLibraryBinding mBinding;

    private LibraryViewModel mViewModel;

    private LibraryRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LibraryFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null || getContext() == null)
            return;
        mViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getLibraryEntries().observe(this, libraryEntries -> {
            if (libraryEntries == null || libraryEntries.size() == 0) {
                Snackbar snackbar = Snackbar.make(mBinding.libraryCoordinatorLayout, R.string.library_add_first_word, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.add, (View view) -> onLibraryEntryClick(-1));
                snackbar.show();
                return;
            }
            mAdapter.swapEntries(libraryEntries);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            mBinding.recyclerViewLibraryList.smoothScrollToPosition(mPosition);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize Data Binding
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false);

        // Set the adapter
        Context context = mBinding.recyclerViewLibraryList.getContext();
        mBinding.recyclerViewLibraryList.setLayoutManager(new LinearLayoutManager(context));

        if (getContext() == null)
            return mBinding.getRoot();

        mAdapter = new LibraryRecyclerViewAdapter(this);
        mBinding.recyclerViewLibraryList.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mBinding
                .recyclerViewLibraryList);

        //Set the setOnClickListener for the Floating Action Button
        mBinding.fabAddWord.setOnClickListener(v -> onLibraryEntryClick(-1));
        mBinding.fabAddWord.setImageDrawable(new IconicsDrawable(getContext()).icon(FontAwesome
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
        if (viewHolder instanceof LibraryRecyclerViewAdapter.ViewHolder) {

            final int deletedIndex = viewHolder.getAdapterPosition();
            // remove the item from the viewModel
            mViewModel.removeLibraryEntry(deletedIndex);
            // show undo option
            Snackbar snackbar = Snackbar.make(mBinding.libraryCoordinatorLayout, R.string.library_word_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, (View view) -> {
                mViewModel.restoreLatestDeletedLibraryEntry();
                Snackbar.make(mBinding.libraryCoordinatorLayout, R.string.restored, Snackbar.LENGTH_SHORT)
                        .show();
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onLibraryEntryClick(int id) {
        LibraryFragmentDirections.ActionLibraryDestToUpdateLibraryDest action =
                LibraryFragmentDirections.actionLibraryDestToUpdateLibraryDest(id);
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
            LibrarySortDialog librarySortDialog = new LibrarySortDialog();
            Bundle args = new Bundle();
            args.putString(SortUtils.KEY_ACTIVE_SORT_BY, mViewModel.getCurrentSortType().name());
            args.putBoolean(SortUtils.KEY_ACTIVE_SORT_ORDER, mViewModel.getCurrentSortOrder());
            librarySortDialog.setArguments(args);
            librarySortDialog.setTargetFragment(this, 1);
            librarySortDialog.show(getParentFragmentManager(), "SortDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onUpdateSortOrderClick(SortUtils.SortType type, boolean asc) {
        mViewModel.sortLibraryBy(type, asc);
    }
}
