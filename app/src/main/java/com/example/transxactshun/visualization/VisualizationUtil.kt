package com.example.transxactshun.visualization

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object VisualizationUtil {
    /**
     * Converts a currency amount (in Cents) to a formatted string (ex. $0.50)
     * @param value - Currency amount in Cents
     * @return a formatted string (ex. $0.45)
     */
    fun currencyFormat(value: Int): String {
        val cost = value.toFloat() / 100F
        val locale = Locale("en", "CA")
        val symbols = DecimalFormatSymbols(locale)
        symbols.decimalSeparator = '.'
        val decimalFormat = DecimalFormat("$#0.00", symbols)
        return decimalFormat.format(cost)
    }

    /**
     * Converts milliseconds to a formatted date (ex. Thu, Jul 28, 2022)
     * @param ms - a date in milliseconds (epoch time)
     */
    fun millisecondsToDateFormat(ms: Long): String {
        val unformattedDate = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
        val dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.CANADA)
        return dateFormatter.format(unformattedDate)
    }
}