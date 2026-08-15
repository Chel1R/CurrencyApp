package com.khyzhniak.currencyapp.data.local.mapper

import com.khyzhniak.currencyapp.data.local.entity.CurrencyHistory
import com.khyzhniak.currencyapp.data.model.CurrencyRates
import com.khyzhniak.currencyapp.data.local.entity.CurrencyRatesTable
import com.khyzhniak.currencyapp.data.model.HistoryGroup
import com.khyzhniak.currencyapp.data.model.HistoryRates
import com.khyzhniak.currencyapp.data.remote.dto.CurrencyRate
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun CurrencyRate.toEntity(): CurrencyRatesTable {
    return CurrencyRatesTable(
        code = targetCurrency,
        date = date,
        rateToEur = rate,
        base = base
    )
}

fun CurrencyRatesTable.toRate(): CurrencyRates {
    return CurrencyRates(
        base = base,
        code = code,
        date = date,
        rate = rateToEur,
    )
}

fun CurrencyHistory.toRateHistory(): HistoryRates {
    return HistoryRates(
        date = date,
        baseCurrency = baseCurrency,
        targetCurrency = targetCurrency,
        rate = rate
    )
}

fun CurrencyRate.toEntityHistory(): CurrencyHistory {
    return CurrencyHistory(
        date = date,
        baseCurrency = base,
        targetCurrency = targetCurrency,
        rate = rate
    )
}

fun LocalDate.toApiDateString() : String =
    format(DateTimeFormatter.ISO_LOCAL_DATE)


fun HistoryGroup.toApiGroupValue(): String = when(this) {
    HistoryGroup.DAILY -> "day"
    HistoryGroup.WEEKLY -> "week"
    HistoryGroup.MONTHLY -> "month"
}
