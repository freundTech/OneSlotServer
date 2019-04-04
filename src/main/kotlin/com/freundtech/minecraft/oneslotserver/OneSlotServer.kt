package com.freundtech.minecraft.oneslotserver

import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import com.freundtech.minecraft.oneslotserver.handler.*
import com.freundtech.minecraft.oneslotserver.util.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
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

    val playTime by config.delegate<Time>(PLAY_TIME)
    val pauseTime by config.delegate<Time>(PAUSE_TIME)

    val iconEmpty = ImageIO.read(getResource(ICON_EMPTY))
            ?: throw MissingResourceException("Couldn't find image $ICON_EMPTY", this::class.qualifiedName, ICON_EMPTY)
    val iconFull = ImageIO.read(getResource(ICON_FULL))
            ?: throw MissingResourceException("Couldn't find image $ICON_FULL", this::class.qualifiedName, ICON_FULL)


    override fun onEnable() {
        instance = this

        config.addDefault(PLAY_TIME, 1800)
        config.addDefault(PAUSE_TIME, 86400)
        config.options().copyDefaults(true)
        this.saveConfig()

        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(ServerListPingListener(), this)
        server.scheduler.scheduleSyncRepeatingTask(this, TickHandler(), 20, 20)

        getCommand(SPECTATE)!!.setExecutor(CommandSpectate())
        getCommand(UNSPECTATE)!!.setExecutor(CommandUnspectate())

        val uuid = config.getString(ACTIVE_PLAYER)
        if (uuid != null) {
            val player = this.server.getPlayer(UUID.fromString(uuid))
            if (player?.isOnline == true) {
                activePlayer = player
            }
        }
    }

    override fun onDisable() {
        activePlayer?.oneSlotServer?.save()
    }
}
