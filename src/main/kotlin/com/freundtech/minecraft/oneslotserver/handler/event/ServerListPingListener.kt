package com.freundtech.minecraft.oneslotserver.handler.event

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.format
import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPingListener(private val plugin: OneSlotServer) : Listener{
    private var iconEmpty = Bukkit.loadServerIcon(plugin.iconEmpty)
    private var iconFull = Bukkit.loadServerIcon(plugin.iconFull)

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        event.maxPlayers = 1

        event.removeAll { player -> Boolean
            player.uniqueId != plugin.activePlayer?.uniqueId
        }

        plugin.activePlayer?.let {
            val waitLeft = it.oneSlotServer.timeLeft

            event.motd = "A player is currently playing. Please wait ${waitLeft.format()}."
            event.setServerIcon(iconFull)
        } ?: run {
            event.motd = "Nobody is playing. You can join the server."
            event.setServerIcon(iconEmpty)
        }
    }
}