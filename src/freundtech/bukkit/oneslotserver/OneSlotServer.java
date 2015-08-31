package freundtech.bukkit.oneslotserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class OneSlotServer extends JavaPlugin {

	public FileConfiguration config = this.getConfig();
	public Playerinfo activePlayer;
	
	public int playtime;
	public int pausetime;

	@Override
	public void onEnable() {
		Path iconEmpty = Paths.get(this.getDataFolder().toString(), "icon-empty.png");
		Path iconFull = Paths.get(this.getDataFolder().toString(), "icon-full.png");

		if (!Files.exists(iconEmpty)) {
			InputStream stream = (this.getClass().getResourceAsStream("resources/icon-empty.png"));
			try {
				Files.copy(stream, iconEmpty);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!Files.exists(iconFull)) {
			InputStream stream = (this.getClass().getResourceAsStream("resources/icon-full.png"));
			try {
				Files.copy(stream, iconFull);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(this), 20, 20);

		config.addDefault("playtime", 1800);
		config.addDefault("pausetime", 86400);
		config.addDefault("activePlayer", "");
		config.options().copyDefaults(true);
		this.saveConfig();

		String uuid = config.getString("activePlayer", "");
		if (!uuid.equals("")) {
			Player player = this.getServer().getPlayer(UUID.fromString(uuid));
			if (player.isOnline()) {
				this.activePlayer = new Playerinfo(player, this);
			}
		}
	}

	@Override
	public void onDisable() {
		if (this.activePlayer != null) {
			this.activePlayer.save();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("spectate")) {
			return true;
		} else if (cmd.getName().equalsIgnoreCase("unspectate")) {
			return true;
		}
		return false;
	}
}
