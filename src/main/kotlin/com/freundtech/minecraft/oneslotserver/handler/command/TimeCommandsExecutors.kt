package com.freundtech.minecraft.oneslotserver.handler.command

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.PlayerInfo
import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import com.kizitonwose.time.seconds
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SetPlayTimeCommand(private val plugin: OneSlotServer) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size != 1) {
            return false
        }
        val time = args[0].toLongOrNull()?.seconds ?: return false
        plugin.playTime = time
        sender.sendMessage("Set play time to $time")
        return true
    }
}

class SetWaitTimeCommand(private val plugin: OneSlotServer) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size != 1) {
            return false
        }
        val time = args[0].toLongOrNull()?.seconds ?: return false
        plugin.waitTime = time
        sender.sendMessage("Set wait time to $time")
        return true
    }
}

class SetTimeLeftCommand(private val plugin: OneSlotServer) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size !in 1..2) {
            return false
        }
        val time = args[0].toLongOrNull()?.seconds ?: return false
        if (args.size == 1) {
            val player = plugin.activePlayer ?: run {
                sender.sendMessage("Nobody is currently playing")
                return false
            }
            player.oneSlotServer.timeLeft = time
            sender.sendMessage("Set time left to $time for active player")
        } else {
            val playerName = args[1]
            val player = sender.server.offlinePlayers.firstOrNull { it.name.equals(playerName, ignoreCase = true) } ?: run {
                sender.sendMessage("Player $playerName is isn't known to this server")
                return false
            }
            player.oneSlotServer.also { it.timeLeft = time }.save()
            sender.sendMessage("Set time left to $time for $playerName")
        }
        return true
    }
}

