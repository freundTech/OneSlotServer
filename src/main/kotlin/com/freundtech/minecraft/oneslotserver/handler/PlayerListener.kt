package com.freundtech.minecraft.oneslotserver.handler

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.loadFromSharedData
import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.saveToSharedData
import com.freundtech.minecraft.oneslotserver.extension.setSpectator
import com.freundtech.minecraft.oneslotserver.util.*
import java.nio.file.Paths
import java.util.Date

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
        val playerInfo = event.player.oneSlotServer
        val activePlayer = plugin.activePlayer

        event.player.oneSlotServer.joinedAt = currentTime()

        // Reset time left
        if (playerInfo.firstJoin < now - plugin.pauseTime) {
            playerInfo.firstJoin = now
            playerInfo.timeLeft = plugin.playTime
        }

        if (playerInfo.timeLeft <= 0) {
            val waitLeft = plugin.pauseTime - (now - playerInfo.firstJoin)

            if (!event.player.hasPermission(PERMISSION_SPECTATE)) {
                event.disallow(Result.KICK_OTHER,
                        "You have no time left on this server. Please wait ${waitLeft.format(hoursFormat)} more hours.")
            }
        } else if (activePlayer != null) {
            val waitLeft = activePlayer.oneSlotServer.timeLeft - (now - activePlayer.oneSlotServer.joinedAt)

            if (!event.player.hasPermission(PERMISSION_SPECTATE)) {
                event.disallow(Result.KICK_FULL,
                        "A person is already playing. Please wait ${waitLeft.format(minutesFormat)} more minutes.")
            }
        } else {
            plugin.activePlayer = event.player
            event.player.loadFromSharedData()
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = ""

        if (plugin.activePlayer?.uniqueId == event.player.uniqueId) {
            event.player.isSleepingIgnored = false

            if (!event.player.hasPermission(PERMISSION_SEE_SPECTATORS)) {
                plugin.server.onlinePlayers.filter {
                    event.player.uniqueId != it.uniqueId
                }.forEach {
                    event.player.hidePlayer(plugin, it)
                }
            }

            event.player.apply {
                sendMessage(arrayOf(
                        "Welcome to the one slot server.",
                        "You have ${this.oneSlotServer.timeLeft / 60} minutes left to play."
                ))
            }
        } else if (event.player.hasPermission(PERMISSION_SPECTATE)) {
            event.player.setSpectator()
            if (plugin.activePlayer?.hasPermission(PERMISSION_SEE_SPECTATORS) == false) {
                plugin.activePlayer?.hidePlayer(plugin, event.player)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage = ""

        if (plugin.activePlayer?.uniqueId == event.player.uniqueId) {
            event.player.saveToSharedData()
            plugin.activePlayer = null
        }
        event.player.oneSlotServer.save()
    }
}
