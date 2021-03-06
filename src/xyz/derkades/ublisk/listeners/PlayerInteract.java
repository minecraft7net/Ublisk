package xyz.derkades.ublisk.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.Chunk;
import xyz.derkades.ublisk.Main;
import xyz.derkades.ublisk.Var;
import xyz.derkades.ublisk.database.PlayerInfo;
import xyz.derkades.ublisk.iconmenus.MainMenu;
import xyz.derkades.ublisk.modules.Loot;
import xyz.derkades.ublisk.utils.Logger;
import xyz.derkades.ublisk.utils.Logger.LogLevel;
import xyz.derkades.ublisk.utils.UPlayer;
import xyz.derkades.ublisk.utils.URunnable;
import xyz.derkades.ublisk.utils.inventory.Item;
import xyz.derkades.ublisk.utils.inventory.UInventory;

public class PlayerInteract implements Listener {
	
	@EventHandler(ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent event){
		UPlayer player = new UPlayer(event);
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR){
			UInventory inv = player.getInventory();
			Material item = inv.getItemInMainHand().getType();
			if (item == Material.CHEST && !player.isInBuilderMode()){
				new MainMenu(player).open();
				event.setCancelled(true);
			} else if (item == Material.END_CRYSTAL){
				player.openEnderchest();
				event.setCancelled(true);
			}
		}
	}
	
	//ignoreCancelled = true - Still track clicks if they are cancelled
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	public void tracker(PlayerInteractEvent event){
		UPlayer player = new UPlayer(event);
		Action action = event.getAction();
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
			player.tracker(PlayerInfo.RIGHT_CLICKED);
		}
		
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
			player.tracker(PlayerInfo.LEFT_CLICKED);
		}
		
		if (action == Action.RIGHT_CLICK_BLOCK){
			if (event.getClickedBlock().getType() == Material.CHEST){
				Chest chest = (Chest) event.getClickedBlock().getState();
				if (Loot.isLoot(chest)){
					player.tracker(PlayerInfo.LOOT_FOUND);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spellTest(PlayerInteractEvent event){
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD){
				final Block block = event.getClickedBlock();
				if (block.getType() == Material.COBBLESTONE){
					@SuppressWarnings("deprecation")
					FallingBlock fall = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
					Vector velocity = fall.getVelocity();
					velocity.setY(velocity.getY() + 1.0);
					fall.setVelocity(velocity);
					block.setType(Material.AIR);
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){
						public void run(){
							Location loc = block.getLocation();
							loc.setY(loc.getY() + 1);
							ThrownPotion potion = (ThrownPotion) Var.WORLD.spawnEntity(loc, EntityType.SPLASH_POTION);
							PotionEffect effect = new PotionEffect(PotionEffectType.HARM, 1, 2);
							potion.getEffects().add(effect);
						}
					}, 2*20);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOW)
	public void staffTool(PlayerInteractEvent event){
		ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

		
		if (itemInHand.getType() != Material.COAL_ORE || event.getAction() != Action.RIGHT_CLICK_BLOCK){
			return;
		}
		
		Player player = event.getPlayer();
		String itemName = itemInHand.getItemMeta().getDisplayName();
		final Block block = event.getClickedBlock();
		
		event.setCancelled(true);
		
		if (itemName == null){
			sendStaffToolInfoMessage(player);
			return;
		}
		
		if (itemName.contains("farmland")){
			block.setType(Material.SOIL);
			block.setData((byte) 7);
			Location loc = block.getLocation();
			Block wheat = new Location(Var.WORLD, loc.getX(), loc.getY() + 1, loc.getZ()).getBlock();
			wheat.setType(Material.CROPS);
			wheat.setData((byte) 7);
		} else if (itemName.contains("invis")){
			block.setData((byte) 0);
			block.setType(Material.PISTON_MOVING_PIECE);
			event.getPlayer().sendMessage("Placed invisible block. To remove invisible block, type /u rinv while standing inside an invisible block.");
		} else if (itemName.contains("coal")){
			block.setType(Material.GLASS);
			new URunnable(){
				public void run(){
					block.setType(Material.COAL_ORE);
				}
			}.runLater(1*20);
		} else if (itemName.contains("lighting")){
			Chunk chunk = ((CraftChunk) block.getChunk()).getHandle();
			chunk.initLighting();
		} else {
			sendStaffToolInfoMessage(player);
		}
	}
	
	private static void sendStaffToolInfoMessage(Player player){
		String[] strings = new String[]{
				"",
				"Mogelijke namen:",
				"invis - Plaatst onzichtbaar block",
				"farmland - Plaatst farmland met wheat",
				"coal - Plaatst coal ore",
				"lighting - Force lighting updates in a chunk"
		};
		
		for (String string : strings) player.sendMessage(string);
		new UPlayer(player).getInventory().addItem(new Item(Material.ANVIL).setName("Use this to rename the coal block."));
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = false)
	public void onTrample(PlayerInteractEvent event){
	    if (event.getAction() == Action.PHYSICAL){
	    	Block block = event.getClickedBlock();
			if (block == null){
				return;
			}  
		
			Material material = block.getType(); 
			
			if (material == Material.SOIL){
				event.setUseInteractedBlock(PlayerInteractEvent.Result.DENY);
				event.setCancelled(true);       
				
				//Set soil as not hydrated
				block.setType(Material.SOIL);
				block.setData((byte) 0);
				final Block block2 = block;
				new BukkitRunnable(){
					public void run(){
						//Set soil as hydrated
						block2.setData((byte) 7);
					}
				}.runTaskLater(Main.getInstance(), 2*20);
				
				//The code below slowly grows the weat plant.
				Location loc = block.getLocation();
				final Block wheat = new Location(Var.WORLD, loc.getX(), loc.getY() + 1, loc.getZ()).getBlock();
				wheat.setType(Material.CROPS);
		        new BukkitRunnable(){
		        	public void run(){
		        		byte data = wheat.getData();
		        		
		        		Logger.log(LogLevel.DEBUG, "Crops", "Someone trampled me! Current current growing state: " + data);
		        		
		        		//I used >= just for safety, it shouldn't matter.
		        		if (data >= 7){
		        			this.cancel(); 
		        			return;
		        		}
		        		wheat.setData((byte) (data + 1));
		        	}
		        }.runTaskTimer(Main.getInstance(), 0, 20);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void chestOpen(PlayerInteractEvent event){
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK ||
				event.getClickedBlock().getType() != Material.CHEST)
			return;
		
		Chest chest = (Chest) event.getClickedBlock().getState();
		
		UPlayer player = new UPlayer(event);
		if (player.isInBuilderMode()){
			if (!Loot.isLoot(chest)) {
				//Send message if chest is not loot
				player.sendMessage(ChatColor.RED + "Warning: Regular players won't be able to open this chest.");
			}
			return;
		}

		if (!Loot.isLoot(chest)){
			event.setCancelled(true); //Cancel chest right click if chest is not loot
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void removePinkWool(final PlayerInteractEvent event){
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getMaterial() == Material.WOOL && event.getItem().getData().getData() == 6){
			event.setCancelled(true);
			new URunnable(){
				public void run(){
					event.getPlayer().kickPlayer("java.net.ConnectException: Connection refused no further information");
				}
			}.runLater(2*20);
		}
	}

}