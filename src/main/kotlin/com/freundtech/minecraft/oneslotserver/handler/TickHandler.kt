package com.freundtech.minecraft.oneslotserver.handler

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.playerInfo
import com.freundtech.minecraft.oneslotserver.util.currentTime
import com.freundtech.minecraft.oneslotserver.util.hoursWait
import com.freundtech.minecraft.oneslotserver.util.minutesWait
import org.bukkit.GameMode
import java.util.*

class TickHandler : Runnable {
    private val plugin = OneSlotServer.instance

    override fun run() {
        val now = currentTime()

        plugin.activePlayer?.let {
            if (it.playerInfo.timeLeft - (now - it.playerInfo.joinedAt) <= 0) {
                val waitLeft = (plugin.pauseTime - (now - it.playerInfo.firstJoin))
                val date = Date(waitLeft * 1000)

                it.kickPlayer("Time is up. You can play again in ${hoursWait.format(date)} hours.")
            }

            if (it.gameMode != GameMode.SPECTATOR) {
                val timeLeft = (it.playerInfo.timeLeft - (now - it.playerInfo.joinedAt))
                val date = Date(timeLeft * 1000)

                it.setPlayerListName("${minutesWait.format(date)} left")
            }
        }
    }
}
