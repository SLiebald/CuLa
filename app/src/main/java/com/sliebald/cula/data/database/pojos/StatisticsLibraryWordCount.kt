package com.sliebald.cula.data.database.pojos

/**
 * @param level KnowledgeLevel aggregation (0-1 is level 1, 1-2 is level 2,...).
 * @param count How many LibraryEntries are in that range of the KnowledgeLevel.
 */
data class StatisticsLibraryWordCount(var level: Double = 0.0, var count: Int = 0)