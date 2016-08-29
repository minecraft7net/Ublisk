package com.robinmc.ublisk.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.robinmc.ublisk.HashMaps;
import com.robinmc.ublisk.utils.perm.Permission;
import com.robinmc.ublisk.utils.perm.Perms;
import com.robinmc.ublisk.utils.variable.CMessage;

import net.md_5.bungee.api.ChatColor;

public class PlayerCommandPreprocess implements Listener {
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event){
		
		if (event.isCancelled()){
			return;
		}
		
		String cmd = event.getMessage();
		Player sender = event.getPlayer();
		String pn = sender.getName();
		
		if (cmd.startsWith("/time set")){
			sender.sendMessage("Please do not use /time set. This command has been cancelled");
			event.setCancelled(true);
			return;
		}
		
		if (cmd.length() >= 4){
			if (cmd.substring(0, 4).equalsIgnoreCase("/op ") || cmd.substring(0, 4).equalsIgnoreCase("/rl ")){
				sender.sendMessage(ChatColor.AQUA + "How about you don't!");
				event.setCancelled(true);
				return;
			}
		}
		
		if (cmd.length() >= 8){
			if (cmd.substring(0, 8).equalsIgnoreCase("/reload ")){
				sender.sendMessage(ChatColor.AQUA + "How about you don't!");
				event.setCancelled(true);
				return;
			}
		}
		
		if (cmd.equalsIgnoreCase("/rl") || cmd.equalsIgnoreCase("/reload")){
			sender.sendMessage(ChatColor.AQUA + "How about you don't!");
			event.setCancelled(true);
			return;
		}
		
		for (Player player: Bukkit.getOnlinePlayers()){
			if (Perms.getPermissionPlayer(player).hasPermission(Permission.COMMANDLOG)){
				if (!(player == sender)){
					if (!(HashMaps.disableCommandLog.get(player.getUniqueId()))){
						player.sendMessage(CMessage.commandLog(pn, cmd));
					}
				}
			}
		}
	}

}
