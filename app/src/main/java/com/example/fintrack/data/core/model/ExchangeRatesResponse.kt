package com.example.fintrack.data.core.model

data class ExchangeRatesResponse(
    val timestamp: Long? = null,
    val base: String? = null,
    val success: Boolean? = null,
    val date: String? = null,
    val rates: Map<String, Double>? = null
)
