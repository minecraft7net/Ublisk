package com.robinmc.ublisk;

import org.bukkit.plugin.java.JavaPlugin;

import com.robinmc.ublisk.commands.Command;
import com.robinmc.ublisk.listeners.Listeners;
import com.robinmc.ublisk.mob.Mob;
import com.robinmc.ublisk.modules.UModule;
import com.robinmc.ublisk.task.Task;
import com.robinmc.ublisk.utils.DataFile;
import com.robinmc.ublisk.utils.DoubleXP;
import com.robinmc.ublisk.utils.Guild;
import com.robinmc.ublisk.utils.Logger;
import com.robinmc.ublisk.utils.Logger.LogLevel;
import com.robinmc.ublisk.utils.TodoList;
import com.robinmc.ublisk.utils.Ublisk;

public class Main extends JavaPlugin {

	public static Main instance;

	@Override
	public void onEnable() {
		instance = this;
		
		Ublisk.RESTART_ERROR = false;

		HashMaps.resetAllPlayers();
		
		new WorldEditCUI().onEnable();

		Listeners.register();

		Command.registerAll();

		Mob.startMobSpawning();

		for (Task task : Task.values())
			task.start();

		DoubleXP.startDoubleXPPacketListener();
		Logger.startSiteLogger();
		
		TodoList.initialize(DataFile.MYSQL.getConfig().getString("todo.user"), DataFile.MYSQL.getConfig().getString("todo.password"));
		
		Logger.log(LogLevel.INFO, "Guilds", "Deleting empty guilds...");
		for (Guild guild : Guild.getGuildsList()){
			if (guild.getMembers().size() == 0){
				Logger.log(LogLevel.WARNING, "Guilds", "Automatically deleted " + guild.getName() + ", because it does not have any members.");
				guild.remove();
			}
		}
		
		for (UModule module : UModule.ALL_MODULES){
			module.initialize();
		}

	}

	@Override
	public void onDisable() {
		instance = null;
		
		Task.stopAll();
		
		for (DataFile dataFile : DataFile.values()){
			dataFile.save();
		}
		
		for (UModule module : UModule.ALL_MODULES){
			module.terminate();
		}
	}

	public static Main getInstance() {
		return instance;
	}

}
