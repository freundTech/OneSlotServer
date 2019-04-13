package com.freundtech.minecraft.oneslotserver.handler.event

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.*
import com.freundtech.minecraft.oneslotserver.util.*

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.event.player.PlayerLoginEvent.Result

class PlayerListener(private val plugin: OneSlotServer) : Listener {
    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val now = currentTime()
        val playerInfo = event.player.oneSlotServer
        val activePlayer = plugin.activePlayer

        event.player.oneSlotServer.join()

        if (!playerInfo.hasTimeRemaining()) {
            val waitLeft = plugin.waitTime - (now - playerInfo.firstJoin)

            if (!event.player.hasPermission(PERMISSION_SPECTATE)) {
                event.disallow(Result.KICK_OTHER,
                        "You have no time left on this server. Please wait ${waitLeft.format()}.")
            }
        } else if (activePlayer != null) {
            val waitLeft = activePlayer.oneSlotServer.timeLeft

            if (!event.player.hasPermission(PERMISSION_SPECTATE)) {
                event.disallow(Result.KICK_FULL,
                        "A person is already playing. Please wait ${waitLeft.format()}.")
            }
        }
        else if (event.result == Result.ALLOWED) {
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
                        "You have ${oneSlotServer.timeLeft.format()} left to play."
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
