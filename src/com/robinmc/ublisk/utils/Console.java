package com.robinmc.ublisk.utils;

import org.bukkit.Bukkit;

@Deprecated
public class Console {
	
	/**
	 * Execute a command as the console (without the /)
	 * @param cmd The command to be executed
	 */
	@Deprecated
	public static void sendCommand(String cmd){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

}