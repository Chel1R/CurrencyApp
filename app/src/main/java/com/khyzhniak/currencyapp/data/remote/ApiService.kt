package com.khyzhniak.currencyapp.data.remote

import com.khyzhniak.currencyapp.data.remote.dto.CurrencyRate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("v2/rates")
    suspend fun getAllRates(
    ): List<CurrencyRate>

    @GET(value = "v2/rates")
    suspend fun getSpecificRateInRange(
        @Query("base") base : String,
        @Query("quotes") quotes : String,
        @Query("from") firstDate : String,
        @Query("to") secondDate : String,
        @Query("group") group : String
    ) : List<CurrencyRate>


}