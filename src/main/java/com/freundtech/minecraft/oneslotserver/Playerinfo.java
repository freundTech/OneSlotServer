package com.freundtech.minecraft.oneslotserver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Playerinfo {	
	public Player player;
	
	FileConfiguration userconfig;
	Path configpath;
	
	public int timeleft;
	public long joinedAt;
	public long firstJoin;
	
	
	public Playerinfo(Player player, OneSlotServer plugin) {		
		configpath = Paths.get(plugin.getDataFolder().getAbsolutePath(), "players", player.getUniqueId().toString() + ".yml");
		userconfig = YamlConfiguration.loadConfiguration(configpath.toFile());
		
		this.player = player;
		this.joinedAt = new Date().getTime()/1000;
		this.firstJoin = userconfig.getLong("first_join", this.joinedAt);
		this.timeleft = userconfig.getInt("time_left", plugin.playtime);
	}
	
	public void save() {
		userconfig.set("first_join", this.firstJoin);
		userconfig.set("time_left", this.timeleft - (new Date().getTime()/1000 - this.joinedAt));
		try {
			userconfig.save(configpath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
