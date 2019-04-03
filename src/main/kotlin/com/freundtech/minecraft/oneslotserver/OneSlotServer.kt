package com.freundtech.minecraft.oneslotserver

import com.freundtech.minecraft.oneslotserver.handler.PlayerListener
import com.freundtech.minecraft.oneslotserver.handler.ServerListPingListener
import com.freundtech.minecraft.oneslotserver.handler.TickHandler
import com.freundtech.minecraft.oneslotserver.util.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO

const val ICON_EMPTY = "com/freundtech/minecraft/oneslotserver/icon-empty.png"
const val ICON_FULL = "com/freundtech/minecraft/oneslotserver/icon-full.png"

class OneSlotServer : JavaPlugin() {
    companion object {
        lateinit var instance: OneSlotServer
            private set
    }

    var activePlayer: Player? = null
        set(value) {
            field = value
            config.set(ACTIVE_PLAYER, value?.uniqueId?.toString())
            saveConfig()
        }

    val playTime by config.delegate(PLAY_TIME, 1800)
    val pauseTime by config.delegate(PAUSE_TIME, 86400)

    val iconEmpty = ImageIO.read(this.javaClass.classLoader.getResource(ICON_EMPTY))
            ?: BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
    val iconFull = ImageIO.read(this.javaClass.classLoader.getResource(ICON_FULL))
            ?: BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)

    override fun onEnable() {
        instance = this

        config.addDefault(PLAY_TIME, 1800)
        config.addDefault(PAUSE_TIME, 86400)
        config.options().copyDefaults(true)
        this.saveConfig()

        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(ServerListPingListener(), this)
        server.scheduler.scheduleSyncRepeatingTask(this, TickHandler(), 20, 20)

        val uuid = config.getString(ACTIVE_PLAYER)
        if (uuid != null) {
            val player = this.server.getPlayer(UUID.fromString(uuid))
            if (player != null && player.isOnline) {
                activePlayer = player
            }
        }
    }

    override fun onDisable() {
        activePlayer?.playerInfo?.save()
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        return when {
            cmd.name.equals(SPECTATE, ignoreCase = true) -> true
            cmd.name.equals(UNSPECTATE, ignoreCase = true) -> true
            else -> false
        }
    }
}
