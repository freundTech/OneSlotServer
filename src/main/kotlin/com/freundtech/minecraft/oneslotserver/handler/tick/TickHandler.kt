package com.freundtech.minecraft.oneslotserver.handler.tick

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.format
import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.saveToSharedData
import com.freundtech.minecraft.oneslotserver.extension.setSpectator
import com.freundtech.minecraft.oneslotserver.util.*

class TickHandler(private val plugin: OneSlotServer) : Runnable {
    override fun run() {
        val now = currentTime()

        var headerMessage = "No player playing"

        plugin.activePlayer?.let {
            if (!it.oneSlotServer.hasTimeRemaining()) {
                val waitLeft = (plugin.waitTime - (now - it.oneSlotServer.firstJoin))
                val kickMessage = "Time is up. You can play again in ${waitLeft.format()}."
                if (!it.hasPermission(PERMISSION_SPECTATE)) {
                    it.kickPlayer(kickMessage)
                }
                else {
                    it.sendMessage(kickMessage)
                    it.saveToSharedData()
                    it.setSpectator()
                    plugin.activePlayer = null
                }
            }
            else {
                val timeLeft = (it.oneSlotServer.timeLeft)
                headerMessage = "${timeLeft.format()} left"
            }
        }

        plugin.server.onlinePlayers.forEach {
            it.playerListHeader = headerMessage
        }
    }
}
