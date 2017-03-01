package com.robinmc.ublisk.quest.npc;

import org.bukkit.entity.Villager.Profession;

import com.robinmc.ublisk.Town;
import com.robinmc.ublisk.quest.NPC;
import com.robinmc.ublisk.quest.NPCInfo;
import com.robinmc.ublisk.quest.NPCInfo.NPCLocation;
import com.robinmc.ublisk.utils.UPlayer;

public class Roy extends NPC {

	@Override
	public NPCInfo getNPCInfo() {
		return new NPCInfo("Roy", Profession.NITWIT, true, new NPCLocation(322, 82, 410));
	}

	@Override
	public void talk(UPlayer player) {
		player.sendMessage("Hoi " + player.getName() + ", ik ben Roy");
		Town town = player.getTown();
		String townNaam = town.getName();
		player.sendMessage("Je bent in: " + townNaam);
	}
}
