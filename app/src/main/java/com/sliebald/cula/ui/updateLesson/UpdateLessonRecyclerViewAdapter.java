package com.sliebald.cula.ui.updateLesson;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sliebald.cula.R;
import com.sliebald.cula.data.database.pojos.MappingPOJO;
import com.sliebald.cula.utilities.KnowledgeLevelUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MappingPOJO}.
 */
public class UpdateLessonRecyclerViewAdapter extends RecyclerView.Adapter<UpdateLessonRecyclerViewAdapter.ViewHolder> {

    private final UpdateLessonRecyclerViewAdapter.OnItemClickListener mListener;

    private List<MappingPOJO> mValues;

    UpdateLessonRecyclerViewAdapter(UpdateLessonRecyclerViewAdapter.OnItemClickListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
    }

    @NonNull
    @Override
    public UpdateLessonRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_mapping_item, parent, false);
        return new UpdateLessonRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UpdateLessonRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mNativeWord.setText(mValues.get(position).getNativeWord());
        holder.mForeignWord.setText(mValues.get(position).getForeignWord());
        holder.mPartOfLesson.setChecked(mValues.get(position).isPartOfLesson());
        holder.viewLayout.setBackgroundColor(KnowledgeLevelUtils.getColorByKnowledgeLevel(mValues.get(position).getKnowledgeLevel()));
    }

    void swapEntries(final List<MappingPOJO> mapping) {
        if (mValues == null) {
            mValues = mapping;
            Log.d("adapter", "adapter called, notified changed");
        } else {
            /*
             * Use DiffUtil to calculate the changes and update accordingly. This
             * shows the four methods you need to override to return a DiffUtil callback. The
             * old list is the current list stored in mValues, where the new list is the new
             * values passed in from the observing the database.
             */

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mValues.size();
                }

                @Override
                public int getNewListSize() {
                    return mapping.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mValues.get(oldItemPosition).getLibraryId() ==
                            mapping.get(newItemPosition).getLibraryId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    MappingPOJO newEntry = mapping.get(newItemPosition);
                    MappingPOJO oldEntry = mValues.get(oldItemPosition);
                    return newEntry.getLibraryId() == oldEntry.getLibraryId()
                            && newEntry.getForeignWord().equals(oldEntry.getForeignWord())
                            && newEntry.getNativeWord().equals(oldEntry.getNativeWord())
                            && newEntry.isPartOfLesson() == oldEntry.isPartOfLesson();
                }
            });
            mValues = mapping;
            result.dispatchUpdatesTo(this);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mValues == null) return 0;
        return mValues.size();
    }

    public interface OnItemClickListener {
        void onLessonEntryClick(CheckBox checkBox, int id);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox mPartOfLesson;
        final TextView mNativeWord;
        final TextView mForeignWord;
        final LinearLayout viewLayout;

        ViewHolder(View view) {
            super(view);
            mPartOfLesson = view.findViewById(R.id.cb_lesson_mapping);
            mNativeWord = view.findViewById(R.id.tv_lesson_mapping_native_word);
            mForeignWord = view.findViewById(R.id.tv_lesson_mapping_foreign_word);
            viewLayout = view.findViewById(R.id.view_item);
            mPartOfLesson.setOnClickListener(v -> mListener.onLessonEntryClick(mPartOfLesson, mValues.get(getAdapterPosition()).getLibraryId()));
        }
    }
}
