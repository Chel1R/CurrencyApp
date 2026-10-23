package com.khyzhniak.currencyapp.utils

import com.khyzhniak.currencyapp.domain.model.DateRange
import com.khyzhniak.currencyapp.domain.model.HistoryGroup
import java.time.temporal.ChronoUnit

fun DateRange.convertToGroup() : HistoryGroup{
    val dayMargin = ChronoUnit.DAYS.between(from,to)

    return when {
        dayMargin <= 31 -> HistoryGroup.DAILY
        dayMargin <= 240 -> HistoryGroup.WEEKLY
        else -> HistoryGroup.MONTHLY
    }
}

