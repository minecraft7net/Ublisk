package com.robinmc.ublisk.utils.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.robinmc.ublisk.money.MoneyItem;
import com.robinmc.ublisk.quest.QuestParticipant;
import com.robinmc.ublisk.utils.inventory.item.Item;
import com.robinmc.ublisk.utils.inventory.item.weapon.Weapon;
import com.robinmc.ublisk.weapon.SwordsmanWeapon;

import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;

public class BetterInventory {
	
	private PlayerInventory inv;
	
	public BetterInventory(Player player){
		this.inv = player.getInventory();
	}
	
	public BetterInventory(PlayerInventory inv){
		this.inv = inv;
	}
	
	public BetterInventory(QuestParticipant qp){
		this.inv = qp.getBukkitInventory();
	}
	
	public static BetterInventory getInventory(Player player){
		return new BetterInventory(player);
	}
	
	public static BetterInventory getInventory(PlayerInventory inv){
		return new BetterInventory(inv);
	}
	
	public static BetterInventory getInventory(QuestParticipant qp){
		return new BetterInventory(qp.getBukkitInventory());
	}
	
	public PlayerInventory getBukkitInventory(){
		return inv;
	}
	
	public void add(ItemStack item){
		inv.addItem(item);
	}
	
	public void add(Material item, int amount){
		add(new ItemStack(item, amount));
	}
	
	public void add(Material material){
		add(new ItemStack(material, 1));
	}
	
	public void add(Item item){
		add(item.getBukkitItem());
	}
	
	public void add(MoneyItem item){
		add(item.getItem());
	}
	
	public void set(int slot, Item item){
		inv.setItem(slot, item.getBukkitItem());
	}
	
	public void remove(ItemStack item){
		inv.remove(item);
	}
	
	public void remove(Item item){
		remove(item.getBukkitItem());
	}
	
	public void remove(Material material, int amount){
		remove(new ItemStack(material, amount));
	}
	
	public void remove(MoneyItem item){
		remove(item.getItem());
	}
	
	/**
	 * Checks if a player's inventory contains an item
	 * @param material
	 * @return
	 * True: if a player's inventory contains at least one of the specified material
	 * <br>
	 * False: if it does not
	 */
	public boolean contains(Material material) {
		return inv.contains(material);
	}
	
	/**
	 * Will return true if the player's inventory contains <b>at least</b> [amount] of an item
	 * @see #contains(ItemStack)
	 * @param material
	 * @param amount
	 * @return 
	 */
	public boolean contains(Material material, int amount){
		return inv.containsAtLeast(new ItemStack(material), amount);
	}
	
	public boolean containsExact(ItemStack item){
		return inv.contains(item, item.getAmount());
	}
	
	public boolean contains(ItemStack... items){
		boolean hasItems = true;
		for (ItemStack item : items){
			if (!inv.containsAtLeast(item, item.getAmount())){
				hasItems = false;
			}
		}
		return hasItems;
	}
	
	public void clear(){
		for (ItemStack item : inv.getContents()) inv.remove(item);
		for (ItemStack item : inv.getArmorContents()) inv.remove(item);
	}
	
	public void addWeapon(SwordsmanWeapon swordsmanWeapon){
		Weapon weapon = swordsmanWeapon.getWeapon();
		Item item = new Item(weapon.getType().getMaterial());
		item.setItemInfo(weapon.getItemInfo());
		
		NBTTagList modifiers = new NBTTagList();

		NBTTagCompound damage = new NBTTagCompound();
		damage.setString("AttributeName", "generic.attackDamage");
		damage.setString("Name", "generic.attackDamage");
		damage.setDouble("Amount", weapon.getWeaponInfo().getDamage());
		damage.setInt("Operation", 1);
		damage.setInt("UUIDLeast", 652);
		damage.setInt("UUIDMost", 12098);
		modifiers.add(damage);
		
		if (weapon.getWeaponInfo().getMovementSpeed() != -1){
			NBTTagCompound speed = new NBTTagCompound();
			speed.setString("AttributeName", "generic.movementSpeed");
			speed.setString("Name", "generic.movementSpeed");
			speed.setDouble("Amount", weapon.getWeaponInfo().getMovementSpeed());
			speed.setInt("Operation", 1);
			speed.setInt("UUIDLeast", 652);
			speed.setInt("UUIDMost", 12098);
			modifiers.add(speed);
		}
		
		if (weapon.getWeaponInfo().getAttackSpeedValue() != -1){
			NBTTagCompound attackSpeed = new NBTTagCompound();
			attackSpeed.setString("AttributeName", "generic.attackSpeed");
			attackSpeed.setString("Name", "generic.attackSpeed");
			attackSpeed.setDouble("Amount", weapon.getWeaponInfo().getAttackSpeedValue());
			attackSpeed.setInt("Operation", 1);
			attackSpeed.setInt("UUIDLeast", 652);
			attackSpeed.setInt("UUIDMost", 12098);
			modifiers.add(attackSpeed);
		}
		
		if (weapon.getWeaponInfo().getKnockbackResistance() != -1){
			NBTTagCompound knockback = new NBTTagCompound();
			knockback.setString("AttributeName", "generic.knockbackResistance");
			knockback.setString("Name", "generic.knockbackResistance");
			knockback.setDouble("Amount", weapon.getWeaponInfo().getKnockbackResistance());
			knockback.setInt("Operation", 1);
			knockback.setInt("UUIDLeast", 652);
			knockback.setInt("UUIDMost", 12098);
			modifiers.add(knockback);
		}
		
		NBTTagCompound compound = item.getCompound();
		compound.set("AttributeModifiers", modifiers);
		compound.setInt("HideFlags", 7);
		compound.setBoolean("Unbreakable", true);
		item.setCompound(compound);
		
		add(item);
	}

}
