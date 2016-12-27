package com.robinmc.ublisk.utils;

import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.robinmc.ublisk.Message;
import com.robinmc.ublisk.Var;
import com.robinmc.ublisk.utils.Logger.LogLevel;

public class Ublisk {
	
	public static UPlayer[] getOnlinePlayers(){
		List<UPlayer> list = new ArrayList<UPlayer>();
		for (Player player : Bukkit.getOnlinePlayers()){
			list.add(new UPlayer(player));
		}
		return list.toArray(new UPlayer[0]);
	}
	
	/**
	 * Execute a command as the console (without the /)
	 * @param cmd The command to be executed
	 */
	public static void sendConsoleCommand(String cmd){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}
	
	public static Connection getNewDatabaseConnection(String reason) throws SQLException {
		Logger.log(LogLevel.DEBUG, "New connection: " + reason);
		String ip = Var.DATABASE_HOST;
		int port = Var.DATABASE_PORT;
		String user = Var.DATABASE_USER;
		String pass = Var.DATABASE_PASSWORD;
		String db = Var.DATABASE_DB_NAME;
		return DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db, user, pass);	
	}
	
	public static void dealWithException(Exception exception, String message){
		Logger.log(LogLevel.SEVERE, message);
		exception.printStackTrace();
	}
	
	public static String getProgressString(float f){
		String string;
		
		if (f > 0.9f){
			string = GREEN + "IIIIIIIII";
		} else if (f > 0.8f){
			string = GREEN + "IIIIIIII" + RED + "I";
		} else if (f > 0.7f){
			string = GREEN + "IIIIIII" + RED + "II";
		} else if (f > 0.6f){
			string = GREEN + "IIIIII" + RED + "III";
		} else if (f > 0.5f){
			string = GREEN + "IIIII" + RED + "IIII";
		} else if (f > 0.4f){
			string = GREEN + "IIII" + RED + "IIIII";
		} else if (f > 0.3f){
			string = GREEN + "III" + RED + "IIIIII";
		} else if (f > 0.2f){
			string = GREEN + "II" + RED + "IIIIIII";
		} else if (f > 0.1f){
			string = GREEN + "I" + RED + "IIIIIIII";
		} else {
			string = RED + "IIIIIIIII";
		}
		
		return DARK_GRAY + " [" + string + DARK_GRAY + "]";
	}
	
	public static OfflinePlayer getPlayerFromString(String uuid){
		return UUIDUtils.getPlayerFromId(UUID.fromString(uuid));
	}
	
	public static void broadcastMessage(Message message){
		for (Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(message.toString());
		}
	}
	
	public static void broadcastMessage(String message){
		for (Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(message.toString());
		}
	}
	
	public static Server getServer(){
		return Bukkit.getServer();
	}

}
