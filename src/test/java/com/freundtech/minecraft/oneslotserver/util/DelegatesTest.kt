package com.freundtech.minecraft.oneslotserver.util

import org.bukkit.configuration.file.YamlConfiguration
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class YamlConfigurationDelegatorTest {
    lateinit var config: YamlConfiguration

    @BeforeTest
    fun before() {
        config = YamlConfiguration()
    }

    @Test
    fun getDefaultTest() {
        val test by config.delegate("test", 5)

        assertEquals(5, test)
        assertEquals(5, config.get("test"))
    }

    @Test
    fun getDefaultNoWriteTest() {
        val test by config.delegate("test", 5, writeDefault = false)

        assertEquals(5, test)
        assertEquals(null, config.get("test", null))
    }

    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNUSED_VALUE")
    @Test
    fun setTest() {
        var test by config.delegate("test", 5)

        test = 1

        assertEquals(1, config.get("test"))
    }

    @Test
    fun getTest() {
        config.set("test", 1)
        val test by config.delegate("test", 5)

        assertEquals(1, test)
    }
}