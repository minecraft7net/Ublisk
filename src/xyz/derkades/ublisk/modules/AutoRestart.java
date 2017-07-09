package xyz.derkades.ublisk.modules;

import java.sql.SQLException;

import xyz.derkades.ublisk.database.ServerInfo;
import xyz.derkades.ublisk.utils.Logger.LogLevel;
import xyz.derkades.ublisk.utils.URunnable;
import xyz.derkades.ublisk.utils.Ublisk;

public class AutoRestart extends UModule {
	
	private final URunnable TASK = new URunnable(){
		public void run(){
			if (Ublisk.getOnlinePlayers().length == 0){
				//If there are no online players, restart.
				AutoRestart.this.log(AutoRestart.this, LogLevel.WARNING, "Restarting server!");
				Ublisk.runAsync(() -> {
					ServerInfo.AUTORESTART_COUNT++;
					try {
						ServerInfo.syncWithDatabase();
					} catch (SQLException e) {
						Ublisk.exception(e, AutoRestart.class);
					} finally {
						Ublisk.runSync(() -> {
							Ublisk.getServer().spigot().restart();
						});
					}
				});
			} else {
				AutoRestart.this.log(AutoRestart.this, LogLevel.INFO, "Did not restart because there were players online.");
			}
		}
	};
	
	@Override
	public void onEnable(){
		TASK.runTimer(60*60*20, 60*60*20); //Run every hour
	}
	
	@Override
	public void onDisable(){
		TASK.cancel();
	}

}
