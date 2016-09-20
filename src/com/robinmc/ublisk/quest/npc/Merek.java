package com.robinmc.ublisk.quest.npc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.robinmc.ublisk.quest.Quest;
import com.robinmc.ublisk.quest.QuestCharacter;
import com.robinmc.ublisk.quest.QuestCharacterClass;
import com.robinmc.ublisk.quest.QuestParticipant;
import com.robinmc.ublisk.utils.UPlayer;
import com.robinmc.ublisk.utils.inventory.BetterInventory;	

public class Merek implements QuestCharacterClass {
	
	public void talk(UPlayer player){
		//QuestParticipant qp = new QuestParticipant(player, Quest.INTRODUCTION, QuestCharacter.MEREK);
		QuestParticipant qp = player.getQuestParticipant(Quest.INTRODUCTION, QuestCharacter.MEREK);
		BetterInventory inv = qp.getInventory();
		if (inv.contains(
				new ItemStack(Material.LOG, 10), 
				new ItemStack(Material.STRING, 16), 
				new ItemStack(Material.GOLD_NUGGET, 10))){
			qp.sendMessage("Great! Now go to Ulric to craft a weapon.");
		} else if (inv.contains(
				new ItemStack(Material.STRING, 16), 
				new ItemStack(Material.GOLD_NUGGET, 10))){
			qp.sendMessage("Finally get 10 wood logs at the saw");
		} else if (inv.contains(
				new ItemStack(Material.WOOL, 4), 
				new ItemStack(Material.GOLD_NUGGET, 10))){
			qp.sendMessage("Great job, now please break down your wool into 16 string by using the windmill.");
		} else if (inv.contains(Material.GOLD_NUGGET, 10)){
			qp.sendMessage("Now bring me 4 wool by killing sheep.");
		} else {
			qp.sendMessage("What are you doing out here? You don�t even have a weapon yet! I�ll tell you what you�ll need to make a weapon. First, get 10 golden nuggets by killing animals.");
		}
			
	}

}
