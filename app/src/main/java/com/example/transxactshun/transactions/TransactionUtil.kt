package com.example.transxactshun.transactions

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object TransactionUtil {
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

    fun currencyFormatWithoutSign(value: Int): String {
        val cost = value.toFloat() / 100F
        val locale = Locale("en", "CA")
        val symbols = DecimalFormatSymbols(locale)
        symbols.decimalSeparator = '.'
        val decimalFormat = DecimalFormat("#0.00", symbols)
        return decimalFormat.format(cost)
    }

    /**
     * Converts milliseconds to a formatted date (ex. Thu, Jul 28, 2022 at 4:50PM)
     * @param ms - a date in milliseconds (epoch time)
     */
    fun millisecondsToDateTimeFormat(ms: Long): String {
        val unformattedDate = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd YYYY, hh:mm a", Locale.CANADA)
        return dateFormatter.format(unformattedDate)
    }

    fun millisecondsToDateFormat(ms: Long): String {
        val unformattedDate = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd YYYY", Locale.CANADA)
        return dateFormatter.format(unformattedDate)
    }

    fun millisecondsToTimeFormat(ms: Long): String {
        val unformattedDate = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.CANADA)
        return dateFormatter.format(unformattedDate)
    }
}