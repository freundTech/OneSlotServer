package com.freundtech.minecraft.oneslotserver.util

import java.text.SimpleDateFormat
import java.util.*

val minutesFormat = SimpleDateFormat("mm:ss").also { it.timeZone = TimeZone.getTimeZone("GMT") }
val hoursFormat = SimpleDateFormat("HH:mm").also { it.timeZone = TimeZone.getTimeZone("GMT") }

typealias Time = Long

fun Time.format(format: SimpleDateFormat): String {
    return format.format(Date(this * 1000))
}

fun currentTime(): Time {
    return Date().time / 1000
}