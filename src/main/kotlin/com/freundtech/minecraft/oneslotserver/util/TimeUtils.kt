package com.freundtech.minecraft.oneslotserver.util

import java.text.SimpleDateFormat
import java.util.*

val minutesWait = SimpleDateFormat("mm:ss").also { it.timeZone = TimeZone.getTimeZone("GMT") }
val hoursWait = SimpleDateFormat("HH:mm").also { it.timeZone = TimeZone.getTimeZone("GMT") }

fun currentTime(): Long {
    return Date().time / 1000
}