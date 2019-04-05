package com.freundtech.minecraft.oneslotserver.handler

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import com.freundtech.minecraft.oneslotserver.util.currentTime
import com.freundtech.minecraft.oneslotserver.util.format
import com.freundtech.minecraft.oneslotserver.util.minutesFormat
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

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
            val waitLeft = it.oneSlotServer.timeLeft

            event.motd = "A player is currently playing. Please wait ${waitLeft.format(minutesFormat)} more minutes."
            event.setServerIcon(iconFull)
        } ?: run {
            event.motd = "Nobody is playing. You can join the server."
            event.setServerIcon(iconEmpty)
        }
    }
}