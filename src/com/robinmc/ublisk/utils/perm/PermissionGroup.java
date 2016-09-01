package com.robinmc.ublisk.utils.perm;

import java.util.Arrays;
import java.util.List;

import com.robinmc.ublisk.utils.exception.GroupNotFoundException;

public enum PermissionGroup {
	
	//TODO: Proper prefixes
	DEFAULT("Default", "Default: "),
	MODERATOR("Moderator", "Moderator: "),
	ADMIN("Admin", "Admin: ", Permission.COMMAND_DEBUG),
	OWNER("Owner", "Owner: ", 
			Permission.COMMAND_DEBUG, 
			Permission.COMMAND_MUTE, 
			Permission.COMMANDLOG
			);
	
	private String name;
	private String prefix;
	private List<Permission> permissions;
	
	PermissionGroup(String name, String prefix, Permission... permissions){
		this.permissions = Arrays.asList(permissions);
		this.name = name;
		this.prefix = prefix;
	}
	
	public String getName(){
		return name;
	}
	
	public String getPrefix(){
		return prefix;
	}
	
	public List<Permission> getPermissions(){
		return permissions;
	}
	
	public boolean hasPermission(Permission perm){
		return permissions.contains(perm);
	}
	
	public static PermissionGroup fromString(String string) throws GroupNotFoundException{
		for (PermissionGroup group : PermissionGroup.values()){
			if (group.getName().equalsIgnoreCase(string)){
				return group;
			}
		}
		
		throw new GroupNotFoundException();
	}

}