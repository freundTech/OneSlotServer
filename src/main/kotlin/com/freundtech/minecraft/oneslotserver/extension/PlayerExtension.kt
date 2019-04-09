package com.freundtech.minecraft.oneslotserver.extension

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.util.delegate
import com.freundtech.minecraft.oneslotserver.util.currentTime
import com.freundtech.minecraft.oneslotserver.util.delegateTime
import com.kizitonwose.time.*
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption.*
import java.util.*
import kotlin.collections.HashMap

val playerDataDir: Path = Paths.get("world", "playerdata")
val advancementDir: Path = Paths.get("world", "advancements")

class PlayerInfo(uuid: UUID) {
    private val plugin = OneSlotServer.instance

    private val configPath = Paths.get(plugin.dataFolder.absolutePath, "players", "$uuid.yml")
    private val userConfig = YamlConfiguration.loadConfiguration(configPath.toFile())

    var joinedAt = currentTime()
    var timeLeft = userConfig.getTime("time_left", plugin.playTime)
        get() = field - (currentTime() - joinedAt)
        set(value) {
            field = currentTime() + value - joinedAt
        }
    var firstJoin by userConfig.delegateTime("first_join", joinedAt)

    fun save() {
        userConfig.set("time_left", this.timeLeft.inMilliseconds.longValue)
        userConfig.save(configPath.toFile())
    }

    fun hasTimeRemaining(): Boolean {
        val now = currentTime()
        if (this.firstJoin < now - plugin.waitTime) {
            this.firstJoin = now
            this.timeLeft = plugin.playTime
        }

        return this.timeLeft > 0.milliseconds
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

    copyFile(playerDataDir, "$uniqueId.dat", "player.dat")

    this.oneSlotServer.save()
    playerCache.remove(this.uniqueId)
}

fun Player.loadFromSharedData() {
    copyFile(playerDataDir, "player.dat", "$uniqueId.dat")

    this.gameMode = GameMode.SURVIVAL
    this.loadData()
}

fun copyFile(path: Path, source: String, target: String): Boolean {
    val sourceFile = path.resolve(source)
    val targetFile = path.resolve(target)
    if (!Files.exists(sourceFile)) {
        return false
    }

    Files.copy(sourceFile, targetFile, REPLACE_EXISTING)
    return true
}