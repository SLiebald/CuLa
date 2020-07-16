package com.sliebald.cula.data.database.pojos

import java.text.SimpleDateFormat
import java.util.*

/**
 * @param date KnowledgeLevel aggregation (0-1 is level 1, 1-2 is level 2,...).
 * @param activity How many LibraryEntries are in that range of the KnowledgeLevel.
 */
class StatisticsActivityEntry(
        var date: String = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(Calendar.getInstance().time),
        var activity: Int = 0
)