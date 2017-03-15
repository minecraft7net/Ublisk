package com.robinmc.ublisk.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.robinmc.ublisk.Clazz;
import com.robinmc.ublisk.HashMaps;
import com.robinmc.ublisk.Main;
import com.robinmc.ublisk.Message;
import com.robinmc.ublisk.Town;
import com.robinmc.ublisk.Var;
import com.robinmc.ublisk.VoteRestart;
import com.robinmc.ublisk.modules.AFK;
import com.robinmc.ublisk.money.Money;
import com.robinmc.ublisk.quest.NPC;
import com.robinmc.ublisk.quest.Quest;
import com.robinmc.ublisk.quest.QuestParticipant;
import com.robinmc.ublisk.quest.npcmenu.NPCMenu;
import com.robinmc.ublisk.utils.Logger.LogLevel;
import com.robinmc.ublisk.utils.exception.GroupNotFoundException;
import com.robinmc.ublisk.utils.exception.LastSenderUnknownException;
import com.robinmc.ublisk.utils.exception.MobNotFoundException;
import com.robinmc.ublisk.utils.exception.NotEnoughManaException;
import com.robinmc.ublisk.utils.exception.NotInATownException;
import com.robinmc.ublisk.utils.exception.PlayerNotFoundException;
import com.robinmc.ublisk.utils.inventory.InvUtils;
import com.robinmc.ublisk.utils.perm.Permission;
import com.robinmc.ublisk.utils.perm.PermissionGroup;
import com.robinmc.ublisk.utils.settings.Setting;
import com.robinmc.ublisk.weapons.abilities.Ability;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_11_R1.ChatComponentText;
import net.minecraft.server.v1_11_R1.Packet;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.PacketPlayOutGameStateChange;

public class UPlayer {

	private Player player;

	public UPlayer(Player player) {
		if (player == null)
			throw new IllegalArgumentException("Player must not be null");
		
		this.player = player;
	}

	public UPlayer(UUID uuid) {
		if (uuid == null)
			throw new IllegalArgumentException("UUID must not be null");
		this.player = Bukkit.getPlayer(uuid);
	}

	public UPlayer(String name) throws PlayerNotFoundException {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null");
		
		Player player = Bukkit.getPlayer(name);
		
		if (player == null)
			throw new PlayerNotFoundException();
		
		this.player = player;
	}

	public UPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			this.player = (Player) sender;
		} else {
			throw new IllegalArgumentException("CommandSender is not a player");
		}
	}

	public UPlayer(PlayerEvent event) {
		if (event == null)
			throw new IllegalArgumentException("Event must not be null");
		this.player = event.getPlayer();
	}

	public UPlayer(EntityEvent event) {
		if (event == null)
			throw new IllegalArgumentException("Event must not be null");
		
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			this.player = (Player) entity;
		} else {
			throw new IllegalArgumentException("Entity is not a player");
		}
	}

	public UPlayer(Entity entity) {
		if (entity == null)
			throw new IllegalArgumentException("Entity must not be null");
		
		if (entity instanceof Player) {
			this.player = (Player) entity;
		} else {
			throw new IllegalArgumentException("Entity is not a player");
		}
	}

	public UPlayer(HumanEntity human) {
		if (human == null)
			throw new IllegalArgumentException("HumanEntity must not be null");
		
		if (human instanceof Player) {
			this.player = (Player) human;
		} else {
			throw new IllegalArgumentException("Human is not a player");
		}
	}

	public UPlayer(OfflinePlayer offline) {
		if (offline == null)
			throw new IllegalArgumentException("OfflinePlayer must not be null");
		
		if (offline.isOnline()) {
			this.player = offline.getPlayer();
		} else {
			throw new IllegalArgumentException("Player " + player.getName() + " is not online.");
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void setLifeCrystals(int amount) {
		DataFile.LIFE_CRYSTAL.getConfig().set("life." + getUniqueId(), amount);
	}

	public int getLifeCrystals() {
		if (DataFile.LIFE_CRYSTAL.getConfig().isSet("life." + getUniqueId())) {
			return DataFile.LIFE_CRYSTAL.getConfig().getInt("life." + getUniqueId());
		} else {
			return 5;
		}
	}

	public PlayerInventory getInventory() {
		return player.getInventory();
	}

	public int getVotingPoints() {
		String path = "voting." + getUniqueId();
		if (DataFile.VOTING.getConfig().isSet(path)) {
			return DataFile.VOTING.getConfig().getInt(path);
		} else {
			setVotingPoints(0);
			return 0;
		}
	}

	public void setVotingPoints(int i) {
		String path = "voting." + getUniqueId();
		DataFile.VOTING.getConfig().set(path, i);
	}

	public boolean hasVotingPoints(int i) {
		return getVotingPoints() >= i;
	}

	public PermissionGroup getGroup() {
		try {
			return PermissionGroup.fromString(DataFile.PERMISSIONS.getConfig().getString("groups." + this.getUniqueId()));
		} catch (GroupNotFoundException e) {
			Logger.log(LogLevel.WARNING, "Permissions", "Could not get group of " + player.getName());
			return PermissionGroup.DEFAULT;
		}
	}

	public void setGroup(PermissionGroup group) {
		DataFile.PERMISSIONS.getConfig().set("groups." + getUniqueId(), group.getName().toLowerCase());
	}

	public boolean hasPermission(Permission perm) {
		return getGroup().hasPermission(perm);
	}

	public Location getLocation() {
		return player.getLocation();
	}

	public void teleport(Location loc) {
		player.teleport(loc);
	}

	public void teleport(double x, double y, double z) {
		teleport(new Location(Var.WORLD, x, y, z));
	}

	public void teleport(double x, double y, double z, int pitch, int yaw) {
		teleport(new Location(Var.WORLD, x, y, z, pitch, yaw));
	}

	public QuestParticipant getQuestParticipant(Quest quest, NPC npc) {
		return new QuestParticipant(player, quest, npc);
	}

	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	public void sendMessage(String msg) {
		player.sendMessage(msg);
	}

	public void sendMessage(TextComponent text) {
		player.spigot().sendMessage(text);
	}
	
	public void sendMessage(Message message) {
		player.sendMessage(message.toString());
	}

	public void sendMessage(BaseComponent[] text) {
		player.spigot().sendMessage(text);
	}

	public void sendMessage(Object o) {
		player.sendMessage(o + "");
	}

	public void sendPrefixedMessage(String message){
		player.sendMessage(Ublisk.getPrefix() + message);
	}
	
	public void sendPrefixedMessage(String prefix, String message){
		player.sendMessage(Ublisk.getPrefix(prefix) + message);
	}
	
	public void sendSpacer() {
		player.sendMessage(" ");
	}

	public void sendSpacers(int spacers) {
		for (int i = 0; i <= spacers; i++) {
			sendSpacer();
		}
	}

	public boolean isSneaking() {
		return player.isSneaking();
	}

	public int getLevel() {
		return Exp.getLevel(player);
	}

	private void setXP(int xp) {
		Exp.set(player, xp);
	}

	public int getXP() {
		return Exp.get(player);
	}

	public void refreshXP() {
		Exp.refresh(player);
	}

	public void addXP(int xp) {
		setXP(getXP() + xp);
	}

	public void giveMobXP(Entity entity) throws MobNotFoundException {
		Exp.giveMobExp(this, entity);
	}

	public String getName() {
		return player.getName();
	}

	public void addFriend(OfflinePlayer newFriend) {
		final List<String> list = DataFile.FRIENDS.getConfig().getStringList("friends." + this.getUniqueId());
		
		if (list.contains(newFriend.getUniqueId())){
			throw new UnsupportedOperationException("Friend is already in friends list");
		}
		
		list.add(newFriend.getUniqueId().toString());
		
		DataFile.FRIENDS.getConfig().set("friends." + this.getUniqueId(), list);
	}

	public void removeFriend(int index) {
		final List<String> friendsUUIDList = DataFile.FRIENDS.getConfig().getStringList("friends." + this.getUniqueId());
		
		if (index > friendsUUIDList.size()){
			throw new IllegalArgumentException("Index can't be more than list size");
		}
		
		friendsUUIDList.remove(index);
		
		DataFile.FRIENDS.getConfig().set("friends." + this.getUniqueId(), friendsUUIDList);
	}

	public void removeFriend(OfflinePlayer friendToRemove) {
		final List<String> list = DataFile.FRIENDS.getConfig().getStringList("friends." + this.getUniqueId());
		
		if (!list.contains(friendToRemove.getUniqueId().toString())){
			throw new IllegalArgumentException(friendToRemove.getName() + " is not " + this.getName() + "'s friend");
		}
		
		list.remove(friendToRemove);
		
		DataFile.FRIENDS.getConfig().set("friends." + this.getUniqueId(), list);
	}

	public List<OfflinePlayer> getFriends() {
		final List<String> uuidStrings = DataFile.FRIENDS.getConfig().getStringList("friends." + getUniqueId());
		final List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		for (String uuidString : uuidStrings){
			UUID uuid = UUID.fromString(uuidString);
			players.add(Bukkit.getOfflinePlayer(uuid));
		}
		return players;
	}
	
	public boolean isFriend(OfflinePlayer offlinePlayer){
		for (OfflinePlayer friend : this.getFriends()){
			if (friend.getName().equals(offlinePlayer.getName())){
				return true;
			}
		}
		return false;
	}

	/**
	 * Bans a player for a given amount of time
	 * 
	 * @param player
	 * @param time
	 *        Time in seconds
	 */
	@Deprecated
	public void tempBan(final int time) {
		player.setBanned(true);
		Logger.log(LogLevel.WARNING, "Banning", player.getName() + " has been banned for " + time + " seconds.");
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {

			public void run() {
				player.setBanned(false);
				Logger.log(LogLevel.INFO, "Banning",
						player.getName() + " has been unbanned after " + time + " seconds.");
			}
		}, time * 20);
	}

	public boolean getSetting(Setting setting) {
		return setting.get(player);
	}

	public void setSetting(Setting setting, boolean bool) {
		setting.put(player, bool);
	}

	public double getHealth() {
		return player.getHealth();
	}

	public GameMode getGameMode() {
		return player.getGameMode();
	}

	public void setGameMode(GameMode gamemode) {
		player.setGameMode(gamemode);
	}

	public void setLastSender(UPlayer player) {
		HashMaps.LAST_MESSAGE_SENDER.put(this.player, player.getPlayer());
	}

	public UPlayer getLastSender() throws LastSenderUnknownException {
		if (!HashMaps.LAST_MESSAGE_SENDER.containsKey(player)) {
			throw new LastSenderUnknownException();
		}

		return new UPlayer(HashMaps.LAST_MESSAGE_SENDER.get(player));
	}

	public void sendPrivateMessage(UPlayer sender, String msg) {
		player.sendMessage(Ublisk.getPrefix("Private Message") + sender.getName() + ChatColor.DARK_GRAY + ": "
				+ ChatColor.RESET + ChatColor.BOLD + msg);
		sender.sendMessage(Ublisk.getPrefix("Private Message") + ChatColor.AQUA + " -> " + player.getName()
				+ ChatColor.DARK_GRAY + ": " + ChatColor.RESET + ChatColor.BOLD + msg);
	}



	public boolean isInBuilderMode() {
		// Check if an inventory file exists, because the item is deleted when a player goes out of builder mode.
		return new File(InvUtils.path, player.getName() + ".yml").exists();
	}

	public void setBuilderModeEnabled(boolean bool) {
		if (bool) { // Enable builder mode
			this.saveInventoryToFile(Main.getInstance().getDataFolder() + "\\inv"); // Save inventory to file
			this.clearInventory();
			this.setGameMode(GameMode.CREATIVE);
			this.sendMessage(Message.BUILDER_MODE_ACTIVATED);
		} else { // Disable builder mode
			this.fillInventoryFromFile(Main.getInstance().getDataFolder() + "\\inv");
			this.setGameMode(GameMode.ADVENTURE);
			this.sendMessage(Message.BUILDER_MODE_DEACTIVATED);
		}
	}

	/**
	 * If player is not in builder mode. put player in builder mode and visa versa
	 */
	public void toggleBuilderMode() {
		setBuilderModeEnabled(!isInBuilderMode());
	}

	public void refreshLastSeenDate() {
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date dateobj = new Date();
		String date = df.format(dateobj);
		DataFile.LAST_PLAYED.getConfig().set("last-played." + player.getUniqueId(), date);
	}

	public String getLastSeenDate() {
		if (DataFile.LAST_PLAYED.getConfig().isSet("last-played." + player.getUniqueId())) {
			return DataFile.LAST_PLAYED.getConfig().getString("last-played." + player.getUniqueId());
		} else {
			return "Never";
		}
	}

	public void tracker(Map<UUID, Integer> map) {
		map.put(this.getUniqueId(), map.get(this.getUniqueId()) + 1);
	}

	public Clazz getClazz() {
		return Clazz.getClass(player);
	}

	public void setMoney(int amount) {
		Money.set(player, amount);
	}

	public int getMoney() {
		return Money.get(player);
	}

	public boolean hasMoney(int amount) {
		return Money.get(player) >= amount;
	}

	public void sendPacket(Packet<?> packet) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
	public void sendActionBarMessage(String message) {
		this.sendPacket(new PacketPlayOutChat(new ChatComponentText(message), (byte) 2));
	}
	
	public void displayMobAppearanceEffect(){
		this.sendPacket(new PacketPlayOutGameStateChange(10, 0));
	}

	public void playNotMovingParticle(Particle particle, double x, double y, double z) {
		player.spawnParticle(particle, x, y, z, 0, 0, 0, 0, 1);
	}

	public boolean hasVotedForRestart() {
		return VoteRestart.hasVotedForRestart(this);
	}

	public void voteRestart() {
		VoteRestart.voteForRestart(this);
	}

	public void openNpcMenu(NPCMenu menu) {
		menu.open(this);
	}

	/**
	 * @return Mana, value between 0 and 20
	 */
	public int getMana() {
		return player.getFoodLevel();
	}

	/**
	 * @param mana
	 *        An integer, 0-20
	 */
	public void setMana(int mana) {
		player.setFoodLevel(mana);
	}

	public void removeMana(int mana) throws NotEnoughManaException {
		if (getMana() - mana < 0)
			throw new NotEnoughManaException();

		setMana(getMana() - mana);
	}

	public void setAfk(boolean setAfk) {
		AFK.setAfk(this, setAfk);
		if (setAfk) {
			Ublisk.broadcastPrefixedMessage(this.getName() + " is now AFK.");
		} else {
			Ublisk.broadcastPrefixedMessage(this.getName() + " is no longer AFK.");
		}
	}

	public boolean isAfk() {
		return AFK.isAfk(this);
	}

	/**
	 * Please avoid using this, unless you are sure that you need this instead of UPlayer#getTown()
	 * 
	 * @throws NotInATownException
	 *         If the player is not in a town
	 */
	public Town getCurrentTown() throws NotInATownException {
		for (Town town : Town.values()) {
			Location loc = player.getLocation();
			if (loc.getX() < town.lessX() && loc.getX() > town.moreX() && loc.getZ() < town.lessZ()
					&& loc.getZ() > town.moreZ()) {
				return town;
			}
		}
		throw new NotInATownException();
	}

	/**
	 * getLastTown()
	 */
	public Town getTown() {
		String s = DataFile.TOWN.getConfig().getString("last-town." + player.getUniqueId());

		if (s == null)
			return Town.INTRODUCTION;

		return Town.fromString(s);
	}

	public void setLastTown(Town town) {
		DataFile.TOWN.getConfig().set("last-town." + player.getUniqueId(), town.getName());
	}

	public void sendTitle(String title, String subtitle) {
		((CraftPlayer) player).sendTitle(title, subtitle);
	}

	public void sendTitle(String title) {
		((CraftPlayer) player).sendTitle(title, "");
	}

	public void sendSubTitle(String subtitle) {
		((CraftPlayer) player).sendTitle("", subtitle);
	}

	public boolean isDead() {
		return player.isDead();
	}

	public void setHealth(double health) {
		player.setHealth(health);
	}

	public void setMaxHealth(double maxHealth) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
	}

	public double getMaxHealth() {
		return player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
	}

	public int getCorrectMaxHealth() {
		if (!Var.LEVEL_HEALTH.containsKey(this.getLevel())) {
			return 1;
		} else {
			return Var.LEVEL_HEALTH.get(this.getLevel());
		}
	}

	public Spigot spigot() {
		return player.spigot();
	}

	public void setResourcePack(String pack) {
		player.setResourcePack(pack);
	}

	public void setAttribute(Attribute attribute, double d) {
		player.getAttribute(attribute).setBaseValue(d);
	}

	public double getAttribute(Attribute attribute) {
		return player.getAttribute(attribute).getBaseValue();
	}

	public void playSound(Sound sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}

	public void playSound(Sound sound, float pitch) {
		this.playSound(sound, 1.0f, pitch);
	}

	public void playSound(Sound sound) {
		this.playSound(sound, 1.0f);
	}

	public void setCollidable(boolean bool) {
		((CraftPlayer) player).setCollidable(bool);
	}

	public void saveInventoryToFile(String path) {
		InvUtils.saveIntentory(path, player);
	}

	public void fillInventoryFromFile(String path) {
		this.clearInventory();
		InvUtils.restoreInventory(path, player);
	}

	public void openEnderchest() {
		player.openInventory(player.getEnderChest());
	}

	/**
	 * Clears inventory and armor slots.
	 */
	public void clearInventory() {
		PlayerInventory inv = player.getInventory();
		for (ItemStack item : inv.getContents())
			inv.remove(item);
		for (ItemStack item : inv.getArmorContents())
			inv.remove(item);
	}

	public boolean inventoryContains(ItemStack... items) {
		boolean hasItems = true;
		for (ItemStack item : items) {
			if (!player.getInventory().containsAtLeast(item, item.getAmount())) {
				hasItems = false;
			}
		}
		return hasItems;
	}

	/**
	 * Checks if the player has enough mana and if their level is high enough. If both or one of these conditions is not true, it will send message(s). Null value is permitted.
	 * 
	 * @param ability
	 */
	public void doAbility(Ability ability) {
		if (ability == null) {
			return;
		}

		if (ability.getMinimumLevel() > player.getLevel()) {
			this.sendMessage(Message.ABILITY_NOT_ENOUGH_LEVEL);
			return;
		}

		try {
			this.removeMana(ability.getMana());
		} catch (NotEnoughManaException e) {
			this.sendMessage(Message.ABILITY_NOT_ENOUGH_MANA);
			return;
		}

		ability.run(this);
	}
	
	public void setVelocity(Vector vector) {
		player.setVelocity(vector);
	}
	
	public Vector getVelocity(){
		return player.getVelocity();
	}
	
	/**
	 * Gives the player a potion effect without particles
	 * @param type The potion effect type
	 * @param duration How long the effect should last for, <b>in ticks</b>
	 * @param amplifier How strong the effect should be. Amplifier 1 -> Speed II
	 */
	public void givePotionEffect(PotionEffectType type, int duration, int amplifier){
		player.addPotionEffect(new PotionEffect(type, duration, amplifier, true, false));
	}
	
	public void setGuild(Guild guild){
		DataFile.GUILDS.getConfig().set("guild." + this.getUniqueId(), guild.getName());
	}
	
	/**
	 * Gets the guild the player is in
	 * @return A guild if player is in a guild, null if the player is not in a guild.
	 */
	public Guild getGuild(){
		String guildName = DataFile.GUILDS.getConfig().getString("guild." + this.getUniqueId());
		
		if (guildName == null){
			return null;
		}
		
		Guild guild = new Guild(guildName);
		
		// Removes guild from file if the guild no longer exists
		if (!guild.exists()){
			DataFile.GUILDS.getConfig().set("guild." + this.getUniqueId(), null);
			return null;
		} else {
			return guild;
		}
	}
	
	public boolean isInGuild(){
		return this.getGuild() != null;
	}
	
	public void leaveGuild(){
		DataFile.GUILDS.getConfig().set("guild." + this.getUniqueId(), null);
	}
	
	public boolean onGround(){
		return !player.isFlying() && player.getLocation().getBlock().getType().isSolid();
	}

	@Override
	public String toString() {
		return player.getUniqueId().toString();
	}

}