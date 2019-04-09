package com.freundtech.minecraft.oneslotserver.util

import com.kizitonwose.time.Interval
import com.kizitonwose.time.Millisecond
import com.kizitonwose.time.TimeUnit
import com.kizitonwose.time.milliseconds
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KProperty

class ConfigurationSectionDelegate<T>(private val config: ConfigurationSection, private val name: String) {
    private var value: T;

    init {
        @Suppress("UNCHECKED_CAST")
        value = config.get(name) as T
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        config.set(name, value)
        this.value = value
    }
}

class ConfigurationSectionTimeDelegate(private val config: ConfigurationSection, private val name: String) {
    private var value: Interval<*>;

    init {
        value = config.getLong(name).milliseconds
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Interval<*> {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Interval<*>) {
        config.set(name, value.inMilliseconds.longValue)
        this.value = value
    }
}

fun <T> ConfigurationSection.delegate(name: String, default: T, writeDefault: Boolean = true): ConfigurationSectionDelegate<T> {
    if (writeDefault) {
        if (this.get(name, null) == null) {
            this.set(name, default)
        }
    }
    else {
        this.addDefault(name, default)
    }
    return ConfigurationSectionDelegate(this, name)
}

fun <T> ConfigurationSection.delegate(name: String): ConfigurationSectionDelegate<T> {
    return ConfigurationSectionDelegate(this, name)
}

fun ConfigurationSection.delegateTime(name: String, default: Interval<*>, writeDefault: Boolean = true): ConfigurationSectionTimeDelegate {
    if (writeDefault) {
        if (this.get(name, null) == null) {
            this.set(name, default.inMilliseconds.longValue)
        }
    }
    else {
        this.addDefault(name, default.inMilliseconds.longValue)
    }
    return ConfigurationSectionTimeDelegate(this, name)
}

fun <T> ConfigurationSection.delegateTime(name: String): ConfigurationSectionTimeDelegate {
    return ConfigurationSectionTimeDelegate(this, name)
}
