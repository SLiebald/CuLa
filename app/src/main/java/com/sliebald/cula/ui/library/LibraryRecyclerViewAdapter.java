package com.sliebald.cula.ui.library;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sliebald.cula.R;
import com.sliebald.cula.data.database.entities.LibraryEntry;
import com.sliebald.cula.utilities.KnowledgeLevelUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link LibraryEntry}.
 */
public class LibraryRecyclerViewAdapter extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.ViewHolder> {

    private final OnItemClickListener mListener;

    private List<LibraryEntry> mValues;

    LibraryRecyclerViewAdapter(OnItemClickListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.library_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mNativeWordView.setText(mValues.get(position).getNativeWord());
        holder.mForeignWordView.setText(mValues.get(position).getForeignWord());
        holder.viewForeground.setBackgroundColor(KnowledgeLevelUtils.getColorByKnowledgeLevel
                (mValues.get(position).getKnowledgeLevel()));

    }

    void swapEntries(final List<LibraryEntry> newLibraryEntries) {
        if (mValues == null) {
            mValues = newLibraryEntries;
            Log.d("adapter", "adapter called, notified changed");
        } else {
            /*
             * Use DiffUtil to calculate the changes and update accordingly. This
             * shows the four methods you need to override to return a DiffUtil callback. The
             * old list is the current list stored in mValues, where the new list is the new
             * values passed in from the observing the database.
             * Based on the Diffultil used in parts of the Udacity Android nanodegree course.
             */
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mValues.size();
                }

                @Override
                public int getNewListSize() {
                    return newLibraryEntries.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mValues.get(oldItemPosition).getId() ==
                            newLibraryEntries.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    LibraryEntry newEntry = newLibraryEntries.get(newItemPosition);
                    LibraryEntry oldEntry = mValues.get(oldItemPosition);
                    return newEntry.equals(oldEntry);
                }
            });
            mValues = newLibraryEntries;
            result.dispatchUpdatesTo(this);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mValues == null)
            return 0;
        return mValues.size();
    }

    public interface OnItemClickListener {
        void onLibraryEntryClick(int id);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView mNativeWordView;
        final TextView mForeignWordView;
        final LinearLayout viewForeground;

        ViewHolder(View view) {
            super(view);
            mNativeWordView = view.findViewById(R.id.nativeWord);
            mForeignWordView = view.findViewById(R.id.foreignWord);
            viewForeground = view.findViewById(R.id.view_foreground);
            viewForeground.setOnClickListener(v -> mListener.onLibraryEntryClick(mValues.get
                    (getAdapterPosition()).getId()));
        }
    }

}
