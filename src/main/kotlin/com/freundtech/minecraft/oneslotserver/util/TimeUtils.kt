package com.freundtech.minecraft.oneslotserver.util;
import com.kizitonwose.time.Interval
import com.kizitonwose.time.Millisecond
import com.kizitonwose.time.milliseconds
import java.util.*

fun currentTime(): Interval<*> = Calendar.getInstance().timeInMillis.milliseconds