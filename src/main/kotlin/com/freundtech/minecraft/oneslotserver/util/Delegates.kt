package com.freundtech.minecraft.oneslotserver.util

import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KProperty

class ConfigurationSectionDelegate<T>(val config: ConfigurationSection, val name: String, default: T) {
    @Suppress("UNCHECKED_CAST")
    private var value: T = config.get(name, default) as? T ?: default

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        config.set(name, value)
        this.value = value
    }
}

fun <T> ConfigurationSection.delegate(name: String, default: T): ConfigurationSectionDelegate<T> {
    return ConfigurationSectionDelegate(this, name, default)
}

