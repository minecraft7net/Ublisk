package xyz.derkades.ublisk.iconmenus;

import static net.md_5.bungee.api.ChatColor.BOLD;
import static net.md_5.bungee.api.ChatColor.DARK_AQUA;
import static net.md_5.bungee.api.ChatColor.GOLD;
import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.RED;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import xyz.derkades.ublisk.Message;
import xyz.derkades.ublisk.utils.IconMenu;
import xyz.derkades.ublisk.utils.UPlayer;
import xyz.derkades.ublisk.utils.Ublisk;
import xyz.derkades.ublisk.utils.IconMenu.OptionClickEvent;
import xyz.derkades.ublisk.utils.inventory.Item;
import xyz.derkades.ublisk.utils.settings.Setting;

public class FriendsMenu {
	
	private static IconMenu menu = new IconMenu("Friends", 3*9, new IconMenu.OptionClickEventHandler(){

		@Override
		public void onOptionClick(OptionClickEvent event) {
			UPlayer player = new UPlayer(event.getPlayer());
			Material item = event.getItem().getType();
			OfflinePlayer friend = Ublisk.getOfflinePlayer(event.getName());
			
			if (item == Material.SPECKLED_MELON){
				event.setWillClose(false);
				if (player.getSetting(Setting.FRIENDS_SHOW_HEALTH)){
					player.setSetting(Setting.FRIENDS_SHOW_HEALTH, false);
					player.sendMessage(Message.FRIEND_HEALTH_DISABLED);
				} else {
					player.setSetting(Setting.FRIENDS_SHOW_HEALTH, true);
					player.sendMessage(Message.FRIEND_HEALTH_ENABLED);
				}
			} else {
				BaseComponent[] text = new ComponentBuilder("Click here")
						.bold(true)
						.color(DARK_AQUA)
						.event(new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder("Click to open website").color(GOLD).create()))
						.event(new ClickEvent(
								ClickEvent.Action.OPEN_URL,
								"http://ublisk.robinmc.com/stats/player.php?player=" + friend.getName()))
						.create();
				player.sendMessage(text);
			}
		}
	});
	
	public static void open(UPlayer player){
		if (player.getName().equals("TheBigBadJosh")){
			menu.setName("Friendzone");
		}
		
		fillMenu(player);
		menu.open(player);
	}
	
	private static void fillMenu(UPlayer player){
		
		if (player.getFriends().isEmpty()){
			ItemStack head = new Item(player.getName()).getItemStack();
			menu.setOption(0, head, GOLD + "You don't have any friends!");
		} else {
			addFriendsToMenu(player);
		}
		
		String displayName = "error";
		
		if (player.getSetting(Setting.FRIENDS_SHOW_HEALTH))
			 displayName = GOLD + "Show friend's health: " + GREEN + BOLD + "Enabled";
		else displayName = GOLD + "Show friend's health: " + RED + BOLD + "Disabled";
		
		ItemStack friendsHealth = new ItemStack(Material.SPECKLED_MELON, 1);
		menu.setOption(18, friendsHealth, displayName);
	}
	
	private static void addFriendsToMenu(UPlayer player){
		int i = 0;
		for (OfflinePlayer friend : player.getFriends()){
			ItemStack head = new Item(player.getName()).getItemStack();
			
			menu.setOption(i, head, friend.getName());
			
			i++;
			
			if (i > 17){
				break;
			}	
		}
	}

}