package com.freundtech.minecraft.oneslotserver

import com.freundtech.minecraft.oneslotserver.util.delegate
import com.freundtech.minecraft.oneslotserver.util.currentTime
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap

class PlayerInfo(uuid: UUID) {
    private val plugin = OneSlotServer.instance

    private val configPath = Paths.get(plugin.dataFolder.absolutePath, "players", "$uuid.yml")
    private val userConfig = YamlConfiguration.loadConfiguration(configPath.toFile())

    val joinedAt = currentTime()
    var timeLeft = userConfig.getInt("time_left", plugin.playTime)
    var firstJoin by userConfig.delegate("first_join", joinedAt)

    fun save() {
        userConfig.set("time_left", this.timeLeft - (currentTime() - this.joinedAt))
        userConfig.save(configPath.toFile())
    }
}

val playerCache = HashMap<UUID, PlayerInfo>()

val Player.playerInfo: PlayerInfo
    get() = playerCache.getOrPut(uniqueId) { PlayerInfo(uniqueId) }
