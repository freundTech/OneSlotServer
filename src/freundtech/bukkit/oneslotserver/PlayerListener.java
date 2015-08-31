package freundtech.bukkit.oneslotserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	private OneSlotServer plugin;

	private SimpleDateFormat minutesWait;
	private SimpleDateFormat hoursWait;

	private CachedServerIcon iconEmpty;
	private CachedServerIcon iconFull;

	public PlayerListener(OneSlotServer plugin) {
		this.plugin = plugin;

		minutesWait = new SimpleDateFormat("mm:ss");
		minutesWait.setTimeZone(TimeZone.getTimeZone("GMT"));
		hoursWait = new SimpleDateFormat("kk:mm");
		hoursWait.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try {
			iconEmpty = Bukkit.loadServerIcon(Paths.get(plugin.getDataFolder().toString(), "icon-empty.png").toFile());
			iconFull = Bukkit.loadServerIcon(Paths.get(plugin.getDataFolder().toString(), "icon-full.png").toFile());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Date now = new Date();

		Playerinfo playerinfo = new Playerinfo(event.getPlayer(), plugin);

		if (playerinfo.firstJoin < now.getTime() / 1000 - plugin.pausetime) {
			playerinfo.firstJoin = now.getTime() / 1000;
			playerinfo.timeleft = plugin.playtime;
		}
		if (playerinfo.timeleft <= 0 && !event.getPlayer().isOp()) {
			long waitleft = (plugin.pausetime - (now.getTime() / 1000 - playerinfo.firstJoin)) * 1000;

			Date date = new Date(waitleft);
			String timestring = hoursWait.format(date);

			event.disallow(Result.KICK_OTHER,
					"You have no time left on this server. Please wait " + timestring + " more hours.");
			return;
		}
		if (plugin.activePlayer != null) {
			if (!playerinfo.player.isOp()) {
				long waitleft = (plugin.activePlayer.timeleft - (now.getTime() / 1000 - plugin.activePlayer.joinedAt))
						* 1000;
				Date date = new Date(waitleft);
				String timestring = minutesWait.format(date);

				event.disallow(Result.KICK_FULL,
						"A person is already playing. Please wait " + timestring + " more minutes.");
				return;
			} else {
				event.allow();
			}
		} else {
			Path playerfile = Paths.get("world", "playerdata", event.getPlayer().getUniqueId().toString() + ".dat");
			Path backupfile = Paths.get("world", "playerdata", "player.dat");
			try {
				Files.deleteIfExists(playerfile);
				Files.copy(backupfile, playerfile, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}

			plugin.activePlayer = playerinfo;
			plugin.config.set("activePlayer", event.getPlayer().getUniqueId().toString());
			plugin.saveConfig();

			event.allow();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		if (plugin.activePlayer != null
				&& !plugin.activePlayer.player.getUniqueId().equals(event.getPlayer().getUniqueId())
				&& event.getPlayer().isOp()) {
			plugin.activePlayer.player.hidePlayer(event.getPlayer());
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
			event.getPlayer().setSleepingIgnored(true);
		}
		if (plugin.activePlayer.player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				if (!plugin.activePlayer.player.getUniqueId().equals(player.getUniqueId())) {
					plugin.activePlayer.player.hidePlayer(player);
				}
			}
			
			event.getPlayer().setSleepingIgnored(false);

			int minutes = plugin.activePlayer.timeleft / 60;

			Bukkit.broadcastMessage("Welcome to the one slot server.");
			Bukkit.broadcastMessage("You have " + minutes + " minutes left to play.");
			Bukkit.broadcastMessage("Read the full server rules here:");
			Bukkit.broadcastMessage("https://redd.it/3j22hq");
		}
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		event.setQuitMessage(null);
		if (plugin.activePlayer != null) {
			if (event.getPlayer().getUniqueId().toString().equals(plugin.config.getString("activePlayer"))) {
				// Wait until player is saved to disk
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run() {
						Path playerfile = Paths.get("world", "playerdata",
								event.getPlayer().getUniqueId().toString() + ".dat");
						Path backupfile = Paths.get("world", "playerdata", "player.dat");
						try {
							Files.deleteIfExists(backupfile);
							Files.copy(playerfile, backupfile, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							e.printStackTrace();
						}

						plugin.activePlayer.save();
						plugin.activePlayer = null;
						plugin.config.set("activePlayer", "");
						plugin.saveConfig();

					}
				});

			}
		}
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMaxPlayers(1);
		
		Iterator<Player> players = event.iterator();
		
		Player player;
		while(players.hasNext()) {
			player = players.next();
			if(plugin.activePlayer == null || !plugin.activePlayer.player.getUniqueId().equals(player.getUniqueId())) { 
				players.remove();
			}
		}
		
		if(plugin.activePlayer != null) {
			long waitleft = (plugin.activePlayer.timeleft - (new Date().getTime()/1000 - plugin.activePlayer.joinedAt)) * 1000;
			Date date = new Date(waitleft);
			String timestring = minutesWait.format(date);
			
			event.setMotd("A player is currently playing. Please wait " + timestring + " more minutes.");
			event.setServerIcon(iconFull);
		}
		else {
			event.setMotd("Nobody is playing. You can join the server.");
			event.setServerIcon(iconEmpty);
		}
	}
	
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		if(!event.getPlayer().getUniqueId().equals(plugin.activePlayer.player.getUniqueId())) {
			event.setCancelled(true);
		}
	}
}
