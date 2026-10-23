package com.khyzhniak.currencyapp.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit


enum class HistoryGroup(){
    DAILY,WEEKLY,MONTHLY
}
enum class DateRangePreset(val label: String) {
    ONE_WEEK("7D"),
    ONE_MONTH("1M"),
    ONE_YEAR("1Y"),
    FIVE_YEARS("5Y");

    fun toDataRange(): DateRange{
        val today = LocalDate.now()

        val safeDate = when(today.dayOfWeek.value){
            6 -> today.minusDays(1)
            7 -> today.minusDays(2)
            else -> today.minusDays(1)
        }
        return when(this){
            ONE_WEEK -> {
                DateRange(
                    from = safeDate.minusWeeks(1),
                    to = safeDate
                )
            }
            ONE_MONTH -> DateRange(
                from = safeDate.minusMonths(1),
                to = safeDate
            )
            ONE_YEAR -> DateRange(
                from = today.minusYears(1),
                to = today
            )
            FIVE_YEARS -> DateRange(
                from = today.minusYears(5),
                to = today
            )
        }
    }

}