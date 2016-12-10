package com.robinmc.ublisk.quest.npc;

import org.bukkit.Material;

import com.robinmc.ublisk.quest.NPC;
import com.robinmc.ublisk.quest.NPCInfo;
import com.robinmc.ublisk.quest.Quest;
import com.robinmc.ublisk.quest.QuestParticipant;
import com.robinmc.ublisk.quest.npcmenu.ClickAction;
import com.robinmc.ublisk.quest.npcmenu.ClickedOption;
import com.robinmc.ublisk.quest.npcmenu.NPCMenu;
import com.robinmc.ublisk.quest.npcmenu.Option;
import com.robinmc.ublisk.utils.UPlayer;

public class TestNPC extends NPC {

	@Override
	public NPCInfo getNPCInfo() {
		return new NPCInfo("TestNPC", null, false, null);
	}
	
	@Override
	public void talk(UPlayer player) {
		final QuestParticipant qp = player.getQuestParticipant(Quest.UNKNOWN, this);
		
		ClickAction action = new ClickAction(){

			@Override
			public void click(ClickedOption option) {
				qp.sendMessage("TEST!");
			}
			
		};
		
		Option option1 = new Option(10, Material.STONE, "Some random option", "Some lore!", "Line 2");
		Option option2 = new Option(11, Material.WOOD, "Option without lore");
		
		qp.openMenu(new NPCMenu("Test Menu", action, option1, option2));
	}

}