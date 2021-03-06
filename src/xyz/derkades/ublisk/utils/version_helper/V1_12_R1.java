package xyz.derkades.ublisk.utils.version_helper;

import java.lang.reflect.Field;

import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftChest;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class V1_12_R1 implements NMS {

	@Override
	public void setTarget(Creature creature, LivingEntity target) {
		CraftCreature craftCreature = (CraftCreature) creature;
	    EntityCreature nms = craftCreature.getHandle();
	    if (target == null) {
	        nms.setGoalTarget(null, null, false);
	    } else {
	        nms.setGoalTarget(((CraftLivingEntity)target).getHandle(), null, false);
	    }
	}

	@Override
	public void setTargetToClosestPlayer(Creature creature, Player player) {
		CraftLivingEntity craftLiving = (CraftLivingEntity) player;
		EntityLiving entityLiving = craftLiving.getHandle();
		((CraftCreature) creature).getHandle().setGoalTarget(entityLiving, TargetReason.CLOSEST_PLAYER, false);
	}
	
	@Override
	public void sendActionBarMessage(Player player, String message) {
		IChatBaseComponent dummyComponent = ChatSerializer.a("{\"text\":\"herobrine1337\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(dummyComponent, ChatMessageType.GAME_INFO);
		packet.components = new BaseComponent[] { new TextComponent(message) };
		CraftPlayer craftPlayer = (CraftPlayer) player;
		craftPlayer.getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle) {
		((CraftPlayer) player).sendTitle(title, subtitle);
	}

	@Override
	public void setChestName(Chest chest, String name) {
		CraftChest craftChest = (CraftChest) chest;
		try {
			Field inventoryField = craftChest.getClass().getDeclaredField("chest");
			inventoryField.setAccessible(true);
			craftChest.setCustomName(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
