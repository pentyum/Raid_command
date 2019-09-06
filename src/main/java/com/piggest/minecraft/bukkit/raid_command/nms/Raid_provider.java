package com.piggest.minecraft.bukkit.raid_command.nms;

import org.bukkit.Location;
import org.bukkit.Raid;

public interface Raid_provider {
	public Raid trigger_raid(Location loc,int level);
}
