package com.freundtech.minecraft.oneslotserver.handler

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.PlayerInfo
import com.freundtech.minecraft.oneslotserver.playerInfo
import com.freundtech.minecraft.oneslotserver.util.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Date

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.event.player.PlayerLoginEvent.Result
import java.nio.file.Path

val playerDataDir: Path = Paths.get("world", "playerdata")

class PlayerListener : Listener {
    private val plugin = OneSlotServer.instance

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val now = currentTime()
        val playerInfo = event.player.playerInfo
        val activePlayer = plugin.activePlayer

        // Reset time left
        if (playerInfo.firstJoin < now - plugin.pauseTime) {
            playerInfo.firstJoin = now
            playerInfo.timeLeft = plugin.playTime
        }

        if (!event.player.isOp) {
            if (playerInfo.timeLeft <= 0) {
                val waitLeft = plugin.pauseTime - (now - playerInfo.firstJoin)
                val date = Date(waitLeft * 1000)

                event.disallow(Result.KICK_OTHER,
                        "You have no time left on this server. Please wait ${hoursWait.format(date)} more hours.")
                return
            }

            if (activePlayer != null) {
                val waitLeft = activePlayer.playerInfo.timeLeft - (now - activePlayer.playerInfo.joinedAt)
                val date = Date(waitLeft * 1000)

                event.disallow(Result.KICK_FULL,
                        "A person is already playing. Please wait ${minutesWait.format(date)} more minutes.")
                return
            }
        }

        if (plugin.activePlayer == null) {
            val playerFile = playerDataDir.resolve("${event.player.uniqueId}.dat")
            val backupFile = playerDataDir.resolve("player.dat")

            if (Files.exists(backupFile)) {
                Files.copy(backupFile, playerFile, StandardCopyOption.REPLACE_EXISTING)
            }

            plugin.activePlayer = event.player
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = ""

        val activeUuid = plugin.activePlayer?.uniqueId

        if (event.player.isOp && activeUuid != null && activeUuid != event.player.uniqueId) {
            event.player.gameMode = GameMode.SPECTATOR
            event.player.isSleepingIgnored = true
        }

        if (activeUuid == event.player.uniqueId) {
            for (player in plugin.server.onlinePlayers) {
                if (activeUuid != player.uniqueId) {
                    plugin.activePlayer?.hidePlayer(plugin, player)
                }
            }

            event.player.isSleepingIgnored = false

            plugin.activePlayer?.playerInfo?.timeLeft?.let {
                Bukkit.broadcastMessage("Welcome to the one slot server.")
                Bukkit.broadcastMessage("You have ${it / 60} minutes left to play.")
                Bukkit.broadcastMessage("Read the full server rules here:")
                Bukkit.broadcastMessage("https://redd.it/3j22hq")
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage = ""
        if (plugin.activePlayer?.uniqueId == event.player.uniqueId) {
            // Wait until player is saved to disk
            plugin.server.scheduler.scheduleSyncDelayedTask(plugin) {
                val playerFile = playerDataDir.resolve("${event.player.uniqueId}.dat")
                val backupFile = playerDataDir.resolve("player.dat")

                Files.copy(playerFile, backupFile, StandardCopyOption.REPLACE_EXISTING)

                event.player.playerInfo.save()
                plugin.activePlayer = null
            }

        }
    }
}
