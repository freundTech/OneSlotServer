package com.freundtech.minecraft.oneslotserver.extension

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.handler.playerDataDir
import com.freundtech.minecraft.oneslotserver.util.currentTime
import com.freundtech.minecraft.oneslotserver.util.delegate
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.collections.HashMap

class PlayerInfo(uuid: UUID) {
    private val plugin = OneSlotServer.instance

    private val configPath = Paths.get(plugin.dataFolder.absolutePath, "players", "$uuid.yml")
    private val userConfig = YamlConfiguration.loadConfiguration(configPath.toFile())

    var joinedAt = currentTime()
    var timeLeft = userConfig.getLong("time_left", plugin.playTime)
        get() = field - (currentTime() - joinedAt)
    var firstJoin by userConfig.delegate("first_join", joinedAt)

    fun save() {
        userConfig.set("time_left", this.timeLeft)
        userConfig.save(configPath.toFile())
    }

    fun hasTimeRemaining(): Boolean {
        val now = currentTime()
        if (this.firstJoin < now - OneSlotServer.instance.pauseTime) {
            this.firstJoin = now
            this.timeLeft = OneSlotServer.instance.playTime
        }

        return this.timeLeft > 0
    }
}

val playerCache = HashMap<UUID, PlayerInfo>()

val Player.oneSlotServer: PlayerInfo
    get() = playerCache.getOrPut(uniqueId) { PlayerInfo(uniqueId) }

fun Player.setSpectator() {
    this.gameMode = GameMode.SPECTATOR
    this.isSleepingIgnored = true
}


fun Player.saveToSharedData() {
    this.saveData()

    val playerFile = playerDataDir.resolve("${this.uniqueId}.dat")
    val backupFile = playerDataDir.resolve("player.dat")

    Files.copy(playerFile, backupFile, StandardCopyOption.REPLACE_EXISTING)

    this.oneSlotServer.save()
    playerCache.remove(this.uniqueId)
}

fun Player.loadFromSharedData() {
    val playerFile = playerDataDir.resolve("${this.uniqueId}.dat")
    val backupFile = playerDataDir.resolve("player.dat")

    if (Files.exists(backupFile)) {
        Files.copy(backupFile, playerFile, StandardCopyOption.REPLACE_EXISTING)
    }

    this.gameMode = GameMode.SURVIVAL
    this.loadData()
}
