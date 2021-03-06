package xyz.derkades.ublisk.modules;

import net.md_5.bungee.api.ChatColor;
import xyz.derkades.derkutils.ListUtils;
import xyz.derkades.ublisk.utils.UPlayer;
import xyz.derkades.ublisk.utils.URunnable;
import xyz.derkades.ublisk.utils.Ublisk;

public class RandomTip extends UModule {
	
	private static final String[] TIP_LIST = {
			//"Change class using /class (Note: you can only switch class every 15 minutes)",
			//"Please set your in-game language to US English to enable custom item names",
			"Experiencing lag? Vote for a restart using /voterestart",
			"Enable or disable music by right clicking the chest",
			"Toggle showing friend's health in the friends menu",
			"Add a friend using /friend add [player]",
			"Open your friends menu using /friend",
			"Create a guild using /guild create <name>",
	};
	
	@Override
	public void onEnable(){
		new URunnable(){
			public void run(){
				String randomTip = ListUtils.getRandomValueFromArray(TIP_LIST);
				for (UPlayer player : Ublisk.getOnlinePlayers()){
					player.sendActionBarMessage(ChatColor.GOLD + randomTip, 60);
				}
			}
		}.runTimer(30*20, 5*60*20);
	}
	

}
