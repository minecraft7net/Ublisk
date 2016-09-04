package com.robinmc.ublisk.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.robinmc.ublisk.HashMaps;
import com.robinmc.ublisk.enums.Loot;
import com.robinmc.ublisk.enums.Tracker;
import com.robinmc.ublisk.utils.Area;
import com.robinmc.ublisk.utils.Config;
import com.robinmc.ublisk.utils.Exp;
import com.robinmc.ublisk.utils.LifeCrystalPlayer;
import com.robinmc.ublisk.utils.Time;
import com.robinmc.ublisk.utils.exception.GroupNotFoundException;
import com.robinmc.ublisk.utils.exception.UnknownAreaException;
import com.robinmc.ublisk.utils.inventory.BetterInventory;
import com.robinmc.ublisk.utils.mob.Mob;
import com.robinmc.ublisk.utils.mob.MobArea;
import com.robinmc.ublisk.utils.mob.MobInfo;
import com.robinmc.ublisk.utils.perm.Permission;
import com.robinmc.ublisk.utils.perm.PermissionGroup;
import com.robinmc.ublisk.utils.perm.PermissionPlayer;
import com.robinmc.ublisk.utils.perm.Perms;
import com.robinmc.ublisk.utils.scheduler.Scheduler;
import com.robinmc.ublisk.utils.third_party.Lag;
import com.robinmc.ublisk.utils.variable.CMessage;
import com.robinmc.ublisk.utils.variable.Message;
import com.robinmc.ublisk.utils.variable.Var;
import com.robinmc.ublisk.weapon.SwordsmanWeapon;

public class Debug implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Perms.getPermissionPlayer(player).hasPermission(Permission.COMMAND_DEBUG)){
				if (args.length == 3){
					if (args[0].equals("group")){
						PermissionGroup group;
						try {
							group = PermissionGroup.fromString(args[2]);
						} catch (GroupNotFoundException e) {
							player.sendMessage("unknown group");
							return true;
						}
						PermissionPlayer target = Perms.getPermissionPlayer(Bukkit.getPlayer(args[1]));
						player.sendMessage("old: " + group.getName());
						target.setGroup(group);
						player.sendMessage("group successful: " + target.getGroup().getName());
						return true;
					} else {
						player.sendMessage(Message.WRONG_USAGE.get());
						return true;
					}
				} else if (args.length == 2){
					if (args[0].equalsIgnoreCase("xp")){
						int xp = Integer.parseInt(args[1]);
						Exp.set(player, xp);
						return true;
					} else if (args[0].equalsIgnoreCase("hunger")){
						Player player2 = Bukkit.getPlayer(args[1]);
						player.sendMessage("Food: " + player2.getFoodLevel());
						return true;
					} else if (args[0].equals("refreshxp")){
						Player target = Bukkit.getPlayer(args[1]);
						Exp.refresh(player);
						player.sendMessage("XP refreshed!");
						player.sendMessage("Config XP: " + Exp.get(target));
						player.sendMessage("With division: " + Math.round(Exp.get(target) / Var.xpDivision));
						player.sendMessage("Bukkit level: " + Exp.getLevel(target));
						return true;
					} else if (args[0].equals("life")){
						int life = Integer.parseInt(args[1]);
						new LifeCrystalPlayer(player).setLifeCrystals(life);
						return true;
					} else {
						player.sendMessage(Message.WRONG_USAGE.get());
						return true;
					}
				} else if (args.length == 1){
					if (args[0].equalsIgnoreCase("kill")){
						Bukkit.broadcastMessage(Message.ENTITIES_REMOVED.get());
						Mob.removeMobs();
						return true;
					} else if (args[0].equalsIgnoreCase("cmd")){
						UUID uuid = player.getUniqueId();
						if (HashMaps.disableCommandLog.get(uuid)){
							player.sendMessage("Enabled!");
							HashMaps.disableCommandLog.put(uuid, false);
						} else {
							player.sendMessage("Disabled!");
							HashMaps.disableCommandLog.put(uuid, true);
						}
						return true;
					} else if (args[0].equals("reload")){
						Config.reload();
						return true;
					} else if (args[0].equals("loot")){
						Loot.spawnRandomLoot();
						return true;
					} else if (args[0].equals("removeloot")){
						Loot.removeLoot();
						return true;
					} else if (args[0].equals("health")){
						player.sendMessage(player.getHealth() + "");
						return true;
					} else if (args[0].equals("sync")){
						Tracker.syncAll();
						return true;
					} else if (args[0].equals("lag")){
						player.sendMessage("TPS: " + Lag.getTPS());
						return true;
					} else if (args[0].equals("mobarea")){
						try {
							player.sendMessage("");
							player.sendMessage("");
							player.sendMessage("");
							player.sendMessage("");
							player.sendMessage("");
							MobArea area = Mob.getArea(player);
							player.sendMessage("You are in area " + area.toString());
							player.sendMessage("");
							player.sendMessage("This area contains MobInfo:");
							for (MobInfo info : area.getMobInfo()){
								player.sendMessage("EntityType: " + info.getEntityType());
								player.sendMessage("Name: " + info.getName());
								player.sendMessage("Health: " + info.getHealth());
								player.sendMessage("XP: " + info.getXP());
								player.sendMessage("Level: " + info.getLevel());
								player.sendMessage("");
							}
							player.sendMessage("Coordinates:");
							Area a = area.getArea();
							player.sendMessage("LessX: " + a.lessX());
							player.sendMessage("MoreX: " + a.moreX());
							player.sendMessage("LessZ: " + a.lessZ());
							player.sendMessage("MoreZ: " + a.moreZ());
							return true;
						} catch (UnknownAreaException e) {
							player.sendMessage("Unknown area!");
							return true;
						}
					} else if (args[0].equals("list")){
						for (Entity entity : Var.world.getEntities()){
							player.sendMessage(entity.getName() + " : " + entity.getCustomName() + " : " + entity.getLocation().getBlockX() + " : " + entity.getLocation().getBlockZ() + " : " + entity.getLocation().getChunk());
						}
						return true;
					} else if (args[0].equals("sword")){
						for (SwordsmanWeapon weapon : SwordsmanWeapon.values()){
							new BetterInventory(player).addWeapon(weapon.getWeapon());
						}
						return true;
					} else if (args[0].equals("restart")){
						Bukkit.broadcastMessage(CMessage.serverRestartingWarningMinutes(1));
						Scheduler.runTaskLater(30*20, new Runnable(){
							public void run(){
								Bukkit.broadcastMessage(CMessage.serverRestartingWarningSeconds(30));
								Scheduler.runTaskLater(20*20, new Runnable(){
									public void run(){
										Bukkit.broadcastMessage(CMessage.serverRestartingWarningSeconds(10));
										Scheduler.runTaskLater(5*20, new Runnable(){
											public void run(){
												Scheduler.runTaskLater(5*20, new Runnable(){
													public void run(){
														Bukkit.getServer().shutdown();
													}
												});
											}
										});
									}
								});
							}
						});
						return true;
					} else if (args[0].equals("day")){
						while (true){
							if (Time.isDay()){
								break;
							}
							Time.add(100);
						}
						return true;
					} else {
						player.sendMessage(Message.WRONG_USAGE.get());
						return true;
					}
				} else {
					player.sendMessage(Message.WRONG_USAGE.get());
					return true;
				}
			} else {
				player.sendMessage(Message.NO_PERMISSION.get());
				return true;
			}
			
		} else {
			sender.sendMessage(Message.NOT_A_PLAYER.get());
			return true;
		}
	}

}
