package com.markbowen.podplay.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

//This defines a method named jsonDateToShortDate that converts the date returned
//from iTunes into a simple month, date, and year format using the user’s current
//locale

object DateUtils {
    fun jsonDateToShortDate(jsonDate: String?): String {
        //First, check that the jsonDate string coming in is not null. If it is, return "-",
        //which doesn’t need to be translated
        if (jsonDate == null) {
            return "-"
        }
        // Define a SimpleDateFormat to match the date format returned by iTunes
        val inFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault())
        // Parse jsonDate string and place it into a Date object named date
        val date = inFormat.parse(jsonDate) ?: return "-"
// The output format is defined as a short date to match the currently defined
//locale.
        val outputFormat =
            DateFormat.getDateInstance(DateFormat.SHORT,
                Locale.getDefault())
        // The date is formatted and returned.
        return outputFormat.format(date)
    }

    //add a helper method to convert from an XML date string to a Date object
    fun xmlDateToDate(dateString: String?): Date {
        val date = dateString ?: return Date()
        val inFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
            Locale.getDefault())
        return inFormat.parse(date) ?: Date()
    }

}