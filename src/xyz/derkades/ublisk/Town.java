package xyz.derkades.ublisk;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.WeatherType;

import xyz.derkades.ublisk.ext.com.xxmicloxx.noteblockapi.NBSDecoder;
import xyz.derkades.ublisk.ext.com.xxmicloxx.noteblockapi.RadioSongPlayer;
import xyz.derkades.ublisk.ext.com.xxmicloxx.noteblockapi.Song;
import xyz.derkades.ublisk.ext.com.xxmicloxx.noteblockapi.SongPlayer;
import xyz.derkades.ublisk.utils.Logger;
import xyz.derkades.ublisk.utils.Logger.LogLevel;
import xyz.derkades.ublisk.utils.UPlayer;

public enum Town {
	
	// x < ..., x > ..., z < ..., z > ...
	// x < 100 x > 22 z < -10 z > 90
	INTRODUCTION("Introduction", TownType.AREA, WeatherType.CLEAR, "ComptineDunAutreEte.nbs", 100, 22, -10, -90, 69, 67, 5),
	GLAENOR("Glaenor", TownType.TOWN_SMALL, WeatherType.CLEAR, "Glaenor.nbs", 175, 100, 17, -120, 116, 68, -86),
	RHOCUS("Rhocus", TownType.TOWN_BIG, WeatherType.CLEAR, "Rhocus.nbs", 240, 100, 400, 240, 174, 82, 313),
	NO_NAME("NoName", TownType.CAPITAL, WeatherType.CLEAR, null, 645, 516, 60, -70, 604, 74, -41),
	DAWN_POINT("Dawn Point", TownType.CAPITAL, WeatherType.CLEAR, null, 1195, 1060, -220, -360, 1152, 69, -319);
	
	private String name;
	private TownType type;
	private WeatherType weather;
	private String musicFile;
	
	private int rangex;
	private int rangex2;
	private int rangez;
	private int rangez2;
	
	private int x;
	private int y;
	private int z;
	
	Town(String name, TownType type, WeatherType weather, String musicFile, int rangex, int rangex2, int rangez, int rangez2, int x, int y, int z){
		this.name = name;
		this.type = type;
		this.weather = weather;
		this.musicFile = musicFile;
		
		this.rangex = rangex;
		this.rangex2 = rangex2;
		this.rangez = rangez;
		this.rangez2 = rangez2;
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String getName(){
		return name;
	}
	
	public TownType getType(){
		return type;
	}
	
	public WeatherType getWeather(){
		return weather;
	}
	
	public String getSongFileName(){
		return musicFile;
	}
	
	public int lessX(){
		return rangex;
	}
	
	public int moreX(){
		return rangex2;
	}
	
	public int lessZ(){
		return rangez;
	}
	
	public int moreZ(){
		return rangez2;
	}
	
	public Location getSpawnLocation(){
		return new Location(Var.WORLD, x, y, z);
	}
	
	public static Town fromString(String text) throws IllegalArgumentException {
		if (text == null) {
			throw new IllegalArgumentException("Town name can not be null");
		}
		
		for (Town town: Town.values()) {
			if (text.equalsIgnoreCase(town.name)) {
				return town;
			}
		}
		return null;		
	}
	
	public void playThemeToPlayer(UPlayer player){
		if (this.getSongFileName() == null){
			return;
		}
		
		Song song = NBSDecoder.parse(new File(Main.getInstance().getDataFolder() + "/music", this.getSongFileName()));
		
		Logger.log(LogLevel.INFO, "Music", "Playing " + this.getSongFileName() + " for town with name " + this.getName() + " to " + player.getName());
		
		SongPlayer songPlayer = new RadioSongPlayer(song);
		songPlayer.setAutoDestroy(true);
		songPlayer.addPlayer(player.bukkit());
		songPlayer.setPlaying(true);
	}
	
	public static enum TownType {
		
		AREA,
		TOWN_SMALL,
		TOWN_BIG,
		CAPITAL;
		
	}
	
}
