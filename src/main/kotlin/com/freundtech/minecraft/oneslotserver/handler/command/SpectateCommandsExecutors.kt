package com.freundtech.minecraft.oneslotserver.handler.command

import com.freundtech.minecraft.oneslotserver.OneSlotServer
import com.freundtech.minecraft.oneslotserver.extension.*
import com.freundtech.minecraft.oneslotserver.util.SPECTATE
import com.freundtech.minecraft.oneslotserver.util.currentTime
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpectateCommand(private val plugin: OneSlotServer) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        assert(label.equals(SPECTATE, ignoreCase = true))

        val target = when (args.size) {
            0 -> if (sender is Player) sender else run {
                sender.sendMessage("Only players can enter spectator mode")
                return false
            }
            1 -> {
                val player = plugin.server.getPlayer(args[0])
                player ?: run {
                    sender.sendMessage("Couldn't find player ${args[0]}")
                    return false
                }
            }
            else -> return false
        }

        if (!setSpectator(target)) {
            sender.sendMessage("Only the active player can be changed to spectator")
        }
        return true
    }

    private fun setSpectator(player: Player): Boolean {
        if (player.uniqueId == plugin.activePlayer?.uniqueId) {
            player.saveToSharedData()
            player.setSpectator()
            plugin.activePlayer = null
            return true
        }

        return false
    }
}

class UnspectateCommand(private val plugin: OneSlotServer) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val target = when (args.size) {
            0 -> if (sender is Player) sender else run {
                sender.sendMessage("Only players can leave spectator mode")
                return false
            }
            1 -> {
                val player = plugin.server.getPlayer(args[0])
                player ?: run {
                    sender.sendMessage("Couldn't find player ${args[0]}")
                    return false
                }
            }
            else -> return false
        }

        target.oneSlotServer.join()
        if (!target.oneSlotServer.hasTimeRemaining()) {
            val waitLeft = plugin.waitTime - (currentTime() - target.oneSlotServer.firstJoin)
            sender.sendMessage("You have no time left on this server. Please wait ${waitLeft.format()}.")
        }
        else if (!unsetSpectator(target)) {
            sender.sendMessage("A player is already playing")
        }
        return true
    }

    private fun unsetSpectator(player: Player): Boolean {
        if (plugin.activePlayer == null) {
            plugin.activePlayer = player
            player.isSleepingIgnored = false
            player.loadFromSharedData()
            player.teleport(player.location)
            return true
        }

        return false
    }
}