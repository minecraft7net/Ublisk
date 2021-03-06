package xyz.derkades.ublisk.listeners;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_AQUA;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.derkades.ublisk.utils.UPlayer;

public class PlayerQuit implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event){
		final UPlayer player = new UPlayer(event);
		
		event.setQuitMessage(DARK_AQUA + "" + BOLD + player.getName() + AQUA + " has left");
		
		player.refreshLastSeenDate();
	}

}
