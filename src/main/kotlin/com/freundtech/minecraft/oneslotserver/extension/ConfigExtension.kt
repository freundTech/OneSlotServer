package com.freundtech.minecraft.oneslotserver.extension

import com.kizitonwose.time.Interval
import com.kizitonwose.time.Millisecond
import com.kizitonwose.time.milliseconds
import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.getTime(path: String, default: Interval<*>): Interval<*> {
    return this.getLong(path, default.inMilliseconds.longValue).milliseconds
}