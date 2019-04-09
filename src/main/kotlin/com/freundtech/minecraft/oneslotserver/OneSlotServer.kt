package com.freundtech.minecraft.oneslotserver

import com.freundtech.minecraft.oneslotserver.extension.oneSlotServer
import com.freundtech.minecraft.oneslotserver.handler.command.*
import com.freundtech.minecraft.oneslotserver.handler.event.AdvancementListener
import com.freundtech.minecraft.oneslotserver.handler.event.PlayerListener
import com.freundtech.minecraft.oneslotserver.handler.event.ServerListPingListener
import com.freundtech.minecraft.oneslotserver.handler.tick.TickHandler
import com.freundtech.minecraft.oneslotserver.util.*
import com.kizitonwose.time.Interval
import com.kizitonwose.time.Second
import com.kizitonwose.time.seconds
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

    var playTime by config.delegateTime(PLAY_TIME, 1800.seconds)
    var waitTime by config.delegateTime(WAIT_TIME, 86400.seconds)

    val iconEmpty = ImageIO.read(getResource(ICON_EMPTY))
            ?: throw MissingResourceException("Couldn't find image $ICON_EMPTY", this::class.qualifiedName, ICON_EMPTY)
    val iconFull = ImageIO.read(getResource(ICON_FULL))
            ?: throw MissingResourceException("Couldn't find image $ICON_FULL", this::class.qualifiedName, ICON_FULL)


    override fun onEnable() {
        instance = this

        server.pluginManager.registerEvents(PlayerListener(this), this)
        server.pluginManager.registerEvents(ServerListPingListener(this), this)
        server.pluginManager.registerEvents(AdvancementListener(this), this)
        server.scheduler.scheduleSyncRepeatingTask(this, TickHandler(this), 20, 20)

        getCommand(SPECTATE)!!.setExecutor(SpectateCommand(this))
        getCommand(UNSPECTATE)!!.setExecutor(UnspectateCommand(this))
        getCommand(SET_PLAY_TIME)!!.setExecutor(SetPlayTimeCommand(this))
        getCommand(SET_WAIT_TIME)!!.setExecutor(SetWaitTimeCommand(this))
        getCommand(SET_TIME_LEFT)!!.setExecutor(SetTimeLeftCommand(this))

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
