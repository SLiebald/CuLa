package com.sliebald.cula.data.database.pojos;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;


@SuppressWarnings("unused")
public class StatisticsActivityEntry {

    /**
     * KnowledgeLevel aggregation (0-1 is 1, 1-2 is 2,...).
     */
    @ColumnInfo(name = "date")
    public String date;

    /**
     * How many LibraryEntries are in that range of the KnowledgeLevel.
     */
    @ColumnInfo(name = "activity")
    private int activity;

    /**
     * Default constructor.
     */
    public StatisticsActivityEntry() {
        //empty constructor for room
    }

    /**
     * Creates a new {@link StatisticsActivityEntry} with the given parameters.
     *
     * @param date     The Date of this {@link StatisticsActivityEntry} as String.
     * @param activity The activity count of the given day.
     */
    @Ignore
    public StatisticsActivityEntry(String date, int activity) {
        this.date = date;
        this.activity = activity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    @Ignore
    @Override
    public String toString() {
        return "StatisticsActivityEntry{" +
                "date=" + date +
                ", activity=" + activity +
                '}';
    }
}
