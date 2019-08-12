package com.piggest.minecraft.bukkit.raid_command.nms;

import org.bukkit.Location;

public interface Raid {
	public Raid_info trigger_raid(Location loc,int level);
}
