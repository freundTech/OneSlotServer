package com.freundtech.minecraft.oneslotserver.handler

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.saveToSharedData
import com.freundtech.minecraft.oneslotserver.extension.setSpectator
import com.freundtech.minecraft.oneslotserver.util.*
import org.bukkit.GameMode
import java.util.*

class TickHandler : Runnable {
    private val plugin = OneSlotServer.instance

    override fun run() {
        val now = currentTime()

        var headerMessage = "No player playing"

        plugin.activePlayer?.let {
            if (it.oneSlotServer.timeLeft - (now - it.oneSlotServer.joinedAt) <= 0) {
                val waitLeft = (plugin.pauseTime - (now - it.oneSlotServer.firstJoin))
                val kickMessage = "Time is up. You can play again in ${waitLeft.format(hoursFormat)} hours."
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
                val timeLeft = (it.oneSlotServer.timeLeft - (now - it.oneSlotServer.joinedAt))
                headerMessage = "${timeLeft.format(minutesFormat)} minutes left"
            }
        }

        plugin.server.onlinePlayers.forEach {
            it.playerListHeader = headerMessage
        }
    }
}
