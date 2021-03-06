package xyz.derkades.ublisk.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.derkades.ublisk.database.PlayerInfo;
import xyz.derkades.ublisk.utils.UPlayer;

public class InventoryClick implements Listener {
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void tracker(InventoryClickEvent event){
		UPlayer player = new UPlayer(event.getWhoClicked());
		player.tracker(PlayerInfo.INV_CLICK);
	}
	
	@EventHandler
	public void onItemClick(InventoryClickEvent event){

		UPlayer player = new UPlayer(event.getWhoClicked());
		
		if (player.isInBuilderMode()){
			return;
		}
		
		if (event.getInventory() != null && event.getInventory().getName() != null && event.getInventory().getName().contains("Box")){
			event.setCancelled(true);
			return;
		}
		
		Material[] cancel = {
				Material.NETHER_STAR,
				Material.CHEST
				};
		
		Material clicked = event.getCurrentItem().getType();
		for (Material material : cancel){
			if (clicked.equals(material)){
				event.setCancelled(true);
			}
		}
	}

}
