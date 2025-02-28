package com.example.fintrack.ui.util

import java.text.SimpleDateFormat
import java.util.Date

fun DateConverter(currentMilliseconds: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy")

    val currentDate = Date(currentMilliseconds)

    return sdf.format(currentDate)
}