package com.sliebald.cula.data.database.pojos;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;


public class StatisticsLibraryWordCount {

    /**
     * KnowledgeLevel aggregation (0-1 is 1, 1-2 is 2,...).
     */
    @ColumnInfo(name = "level")
    public double level;

    /**
     * How many LibraryEntries are in that range of the KnowledgeLevel.
     */
    @ColumnInfo(name = "count")
    private int count;

    @SuppressWarnings("unused")
    public double getLevel() {
        return level;
    }

    @SuppressWarnings("unused")
    public void setLevel(double level) {
        this.level = level;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @NonNull
    @Override
    public String toString() {
        return "StatisticsLibraryWordCount{" +
                "level=" + level +
                ", count=" + count +
                '}';
    }
}
