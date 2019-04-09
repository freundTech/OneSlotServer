package com.freundtech.minecraft.oneslotserver.extension

import com.kizitonwose.time.Interval
import com.kizitonwose.time.hours
import java.text.SimpleDateFormat
import java.util.*

val hoursFormat = SimpleDateFormat("HH:mm").also { it.timeZone = TimeZone.getTimeZone("GMT") }
val minutesFormat = SimpleDateFormat("mm:ss").also { it.timeZone = TimeZone.getTimeZone("GMT") }

fun Interval<*>.format(): String {
    val result = StringBuilder()
    return if (this > 1.hours) {
        "${hoursFormat.format(inMilliseconds.longValue)} hours"
    } else {
        "${minutesFormat.format(inMilliseconds.longValue)} minutes"
    }
}