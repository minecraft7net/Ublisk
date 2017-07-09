package xyz.derkades.ublisk;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.derkades.ublisk.commands.Command;
import xyz.derkades.ublisk.database.PlayerInfo;
import xyz.derkades.ublisk.database.ServerInfo;
import xyz.derkades.ublisk.database.SyncQueue;
import xyz.derkades.ublisk.ext.com.coloredcarrot.api.sidebar.SidebarAPI;
import xyz.derkades.ublisk.listeners.Listeners;
import xyz.derkades.ublisk.mob.MobSpawn;
import xyz.derkades.ublisk.mob.Mobs;
import xyz.derkades.ublisk.modules.UModule;
import xyz.derkades.ublisk.task.Task;
import xyz.derkades.ublisk.utils.DoubleXP;
import xyz.derkades.ublisk.utils.Guild;
import xyz.derkades.ublisk.utils.Logger;
import xyz.derkades.ublisk.utils.Logger.LogLevel;
import xyz.derkades.ublisk.utils.PacketListener;
import xyz.derkades.ublisk.utils.TodoList;
import xyz.derkades.ublisk.utils.UPlayer;
import xyz.derkades.ublisk.utils.URunnable;
import xyz.derkades.ublisk.utils.Ublisk;
import xyz.derkades.ublisk.utils.caching.Cache;
import xyz.derkades.ublisk.utils.version_helper.V1_12_R1;

public class Main extends JavaPlugin {

	private static Main instance;

	@Override
	public void onEnable() {
		instance = this;
		
		Ublisk.RESTART_ERROR = false;
		
		PacketListener.RUNNING = true;

		Listeners.register();

		Command.registerAll();

		Mobs.clearMobs();
		MobSpawn.startMobSpawning();

		for (Task task : Task.values())
			task.start();

		DoubleXP.startDoubleXPPacketListener();
		Logger.startSiteLogger();
		
		TodoList.initialize(
				DataFile.MYSQL.getConfig().getString("todo.ip"), 
				DataFile.MYSQL.getConfig().getInt("todo.port"),
				DataFile.MYSQL.getConfig().getString("todo.database"), 
				DataFile.MYSQL.getConfig().getString("todo.user"), 
				DataFile.MYSQL.getConfig().getString("todo.password"));
		
		for (UModule module : UModule.ALL_MODULES){
			try {
				module.initialize();
			} catch (Exception e){
				Logger.log(LogLevel.SEVERE, "Modules", "An error occured while initializing " + module.getClass().getSimpleName() + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		Bukkit.clearRecipes();
		
		Ublisk.NMS = new V1_12_R1();
		
		new SidebarAPI().onEnable();
		
		new URunnable(){
			public void run(){
				Logger.log(LogLevel.INFO, "Cache", "Building up cache, expect some lag..");
				for (Guild guild : Guild.getGuildsList()){
					guild.getDescription();
					guild.getMembers();
					guild.getOwner();
					guild.getPoints();
				}
				
				for (UPlayer player : Ublisk.getOnlinePlayers()){
					player.getXP();
				}
				Logger.log(LogLevel.INFO, "Cache", "Complete! Now containing " + Cache.size() + " objects.");
				
				Logger.log(LogLevel.INFO, "Guilds", "Deleting empty guilds...");
				for (Guild guild : Guild.getGuildsList()){
					if (guild.getMembers().size() == 0){
						Logger.log(LogLevel.WARNING, "Guilds", "Automatically deleted " + guild.getName() + ", because it does not have any members.");
						guild.remove();
					}
				}
				
			}
		}.runLater(10*20);
		
		for (UPlayer player : Ublisk.getOnlinePlayers()) {
			PlayerInfo.resetHashMaps(player);
		}
	
		Ublisk.openDatabaseConnection();
		
	}
	
	@Override
	public void onDisable() {
		Logger.log(LogLevel.INFO, "Core", "Shutting down...");    

		Task.stopAll();
		
		// Save data files
		for (DataFile dataFile : DataFile.values()){
			dataFile.save();
		}
		
		// Stop all running modules
		for (UModule module : UModule.ALL_MODULES){
			if (!module.isRunning()){
				Logger.log(LogLevel.WARNING, "Modules", module.getClass().getSimpleName() + " is already terminated.");
			}
			
			module.terminate();
		}
		
		// Close all open sockets
		PacketListener.RUNNING = false;
		
		// Clear remaining tasks in sync queue
		SyncQueue.clear();
		
		try {
			ServerInfo.syncWithDatabase();
		} catch (SQLException e) {
			Ublisk.exception(e, getClass());
		}
		
		Ublisk.closeDatabaseConnection();
		
		Logger.log(LogLevel.INFO, "Core", "Plugin has been shut down.");
		
		instance = null;
	}

	public static Main getInstance() {
		return instance;
	}

}
