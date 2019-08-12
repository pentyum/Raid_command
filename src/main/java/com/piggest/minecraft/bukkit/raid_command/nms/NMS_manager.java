package com.piggest.minecraft.bukkit.raid_command.nms;

import com.piggest.minecraft.bukkit.raid_command.Raid_command;

public class NMS_manager {
	public static Raid raid_provider = null;

	public NMS_manager(String version) {
		Raid_command.instance.getLogger().info("当前NMS:" + version);
		NMS_version nms_version = NMS_version.parse_version(version);
		switch (nms_version) {
		case v1_14:
			Raid_command.instance.getLogger().info("已适配NMS:" + version);
			raid_provider = new Raid_1_14();
			break;
		default:
			Raid_command.instance.getLogger().warning("NMS未能适配!");
			raid_provider = new Raid_1_14();
			break;
		}
	}
}
