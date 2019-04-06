package com.freundtech.minecraft.oneslotserver.handler

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import org.bukkit.GameRule.ANNOUNCE_ADVANCEMENTS
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class AdvancementListener : Listener {
    private val plugin = OneSlotServer.instance


    @EventHandler
    fun onPlayerAdvancementDone(event: PlayerAdvancementDoneEvent) {
        val showAdvancement = event.player.uniqueId == plugin.activePlayer?.uniqueId
        event.player.world.setGameRule(ANNOUNCE_ADVANCEMENTS, showAdvancement)
    }
}
