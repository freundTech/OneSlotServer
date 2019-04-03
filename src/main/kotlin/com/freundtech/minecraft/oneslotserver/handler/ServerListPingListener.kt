package com.freundtech.minecraft.oneslotserver.handler

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.playerInfo
import com.freundtech.minecraft.oneslotserver.util.currentTime
import com.freundtech.minecraft.oneslotserver.util.minutesWait
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import java.util.*

class ServerListPingListener : Listener{
    private val plugin = OneSlotServer.instance

    private var iconEmpty = Bukkit.loadServerIcon(plugin.iconEmpty)
    private var iconFull = Bukkit.loadServerIcon(plugin.iconFull)

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        event.maxPlayers = 1

        event.removeAll { player -> Boolean
            player.uniqueId != plugin.activePlayer?.player?.uniqueId
        }

        plugin.activePlayer?.let {
            val waitLeft = (it.playerInfo.timeLeft - (currentTime() - it.playerInfo.joinedAt))
            val date = Date(waitLeft * 1000)
            val timeString = minutesWait.format(date)

            event.motd = "A player is currently playing. Please wait $timeString more minutes."
            event.setServerIcon(iconFull)
        } ?: run {
            event.motd = "Nobody is playing. You can join the server."
            event.setServerIcon(iconEmpty)
        }
    }
}