package com.robinmc.ublisk.iconmenus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.robinmc.ublisk.Main;
import com.robinmc.ublisk.enums.Setting;
import com.robinmc.ublisk.utils.exception.NotSetException;
import com.robinmc.ublisk.utils.java.MiscUtils;
import com.robinmc.ublisk.utils.logging.LogLevel;
import com.robinmc.ublisk.utils.logging.Logger;
import com.robinmc.ublisk.utils.third_party.IconMenu;
import com.robinmc.ublisk.utils.third_party.IconMenu.OptionClickEvent;

public class SettingsMenu {

	private static IconMenu menu = new IconMenu("Settings", 1*9, new IconMenu.OptionClickEventHandler(){

		@Override
		public void onOptionClick(OptionClickEvent event) {
			String name = event.getName().toLowerCase();
			Setting setting = Setting.fromName(name);
			Player player = event.getPlayer();
			
			//TODO: Message
			
			try {
				//If setting is set to true, set to false and if set to false, set to true
				setting.put(player, MiscUtils.invertBoolean(setting.get(player)));
			} catch (NotSetException e) {
				setting.put(player, false);
			}
		}
	}, Main.getInstance());
	
	public static void open(Player player){
		Logger.log(LogLevel.INFO, "Menu", "Settings has been opened for " + player.getName());
		fillMenu(player);
		menu.open(player);
	}
	
	private static void fillMenu(Player player){
		int slot = 0;
		for (Setting setting : Setting.values()){
			//If setting is on, set dye color to green, otherwise to gray
			short damage;
			try {
				if (setting.get(player)){
					damage = 10;
				} else {
					damage = 8;
				}
			} catch (NotSetException e) {
				damage = 5;
			}
			
			ItemStack item = new ItemStack(Material.INK_SACK);
			item.setDurability(damage);
			
			menu.setOption(slot, item, setting.getName(), setting.getInfo());
			
			slot++;
		}
	}
	
}
