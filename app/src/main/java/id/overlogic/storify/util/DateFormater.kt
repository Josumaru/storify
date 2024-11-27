package id.overlogic.storify.util

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DateFormatter(private val date: String) {
    fun format(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val parsedDate = inputFormat.parse(date)
            parsedDate?.let {
                DateFormat.format("dd MMM yyyy, HH:mm", it).toString()
            } ?: date
        } catch (e: Exception) {
            date
        }
    }
}
