package freundtech.bukkit.oneslotserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.GameMode;

public class TickHandler implements Runnable {

	private OneSlotServer plugin;
	
	private SimpleDateFormat timeLeft;
	private SimpleDateFormat hoursWait;

	public TickHandler(OneSlotServer plugin) {
		this.plugin = plugin;
		timeLeft = new SimpleDateFormat("mm:ss");
		timeLeft.setTimeZone(TimeZone.getTimeZone("GMT"));
		hoursWait = new SimpleDateFormat("kk:mm");
		hoursWait.setTimeZone(TimeZone.getTimeZone("GMT"));

	}

	@Override
	public void run() {
		if (plugin.activePlayer != null) {
			Date now = new Date();
			Playerinfo player = plugin.activePlayer;

			if (player.timeleft - (now.getTime() / 1000 - player.joinedAt) <= 0) {
				long waitleft = ((24 * 60 * 60) - (now.getTime()/1000 - player.firstJoin)) * 1000;
				Date date = new Date(waitleft);
				String timestring = hoursWait.format(date);
				
				player.player.kickPlayer("Time is up. You can play again in " + timestring + " hours.");
			}
			
			if (player.player.getGameMode() != GameMode.SPECTATOR) {
				long timeleft = (player.timeleft - (now.getTime()/1000 - player.joinedAt)) * 1000;
				Date date = new Date(timeleft);
				
				String timestring = timeLeft.format(date);
				
				player.player.setPlayerListName(timestring + " left");
			}
		}
	}
}
