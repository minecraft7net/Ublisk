package xyz.derkades.ublisk.task;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import xyz.derkades.ublisk.database.PlayerInfo;
import xyz.derkades.ublisk.utils.UPlayer;
import xyz.derkades.ublisk.utils.Ublisk;

public class SecondsAFKStatistic extends BukkitRunnable {

	@Override
	public void run() {
		for (UPlayer player : Ublisk.getOnlinePlayers()){
			UUID uuid = player.getUniqueId();
			if (player.isAfk())
				PlayerInfo.SECONDS_AFK.put(uuid, PlayerInfo.SECONDS_AFK.get(uuid) + 1);
			else
				PlayerInfo.SECONDS_NOT_AFK.put(uuid, PlayerInfo.SECONDS_NOT_AFK.get(uuid) + 1);
		}
	}

}
