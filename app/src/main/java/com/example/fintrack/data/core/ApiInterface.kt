package com.example.fintrack.data.core

import com.example.fintrack.data.core.model.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("convert")
    suspend fun getLatestRates(
        @Query("from") from: String,
        @Query("to") to: String? = null,
        @Query("amount") amount: Double? = null
    ): Response<ExchangeRatesResponse>
}