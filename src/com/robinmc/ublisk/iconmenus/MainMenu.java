package com.robinmc.ublisk.iconmenus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.robinmc.ublisk.Main;
import com.robinmc.ublisk.utils.logging.LogLevel;
import com.robinmc.ublisk.utils.logging.Logger;
import com.robinmc.ublisk.utils.scheduler.Scheduler;
import com.robinmc.ublisk.utils.third_party.IconMenu;
import com.robinmc.ublisk.utils.third_party.IconMenu.OptionClickEvent;
import com.robinmc.ublisk.utils.variable.Message;

public class MainMenu {
	
	private static IconMenu menu = new IconMenu("Main menu", 3*9, new IconMenu.OptionClickEventHandler(){

		@Override
		public void onOptionClick(OptionClickEvent event) {
			String name = event.getName().toLowerCase();
			final Player player = event.getPlayer();
			Logger.log(LogLevel.DEBUG, name);
			/*
			if (name.contains("music")){
					try {
						if (Setting.PLAY_MUSIC.get(player)){
							Setting.PLAY_MUSIC.put(player, false);
							player.sendMessage(Message.MUSIC_DISABLED.get());
						} else {
							Setting.PLAY_MUSIC.put(player, true);
							player.sendMessage(Message.MUSIC_ENABLED.get());
							String town = Config.getString("last-town." + player.getUniqueId());
						    Music.playSong(player, town);
						}
					} catch (NotSetException e) {
						player.sendMessage(Message.MUSIC_ENABLED.get());
						Setting.PLAY_MUSIC.put(player, true);
					}
			} else if (name.contains("pm")){
				try {
					if (Setting.PM_SOUND.get(player)){
						//TODO: Message for disabling  and enabling PM sound
						player.sendMessage("disabled");
						Setting.PM_SOUND.put(player, false);
					} else {
						player.sendMessage("enabled");
						Setting.PM_SOUND.put(player, true);
					}
				} catch (NotSetException e) {
					Setting.PM_SOUND.put(player, true);
					player.sendMessage("enabled");
				}
				*/
			if (name.equals("settings")){
				Scheduler.runTaskLater(5, new Runnable(){ 
					public void run(){ 
						SettingsMenu.open(player); 
					}
				});
			} else {
				player.sendMessage(Message.ERROR_MENU.get());
			}
		}
	}, Main.getInstance());
	
	public static void open(Player player){
		Logger.log(LogLevel.INFO, "Menu", "MainMenu has been opened for " + player.getName());
		fillMenu();
		menu.open(player);
	}
	
	private static void fillMenu(){
		//menu.setOption(0, new ItemStack(Material.JUKEBOX), "Toggle music");
		//menu.setOption(1, new ItemStack(Material.JUKEBOX), "Toggle PM sounds");
		menu.setOption(0, new ItemStack(Material.BRICK), "Settings", "Toggle various options on and off");
	}

}
