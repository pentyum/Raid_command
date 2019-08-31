package com.piggest.minecraft.bukkit.raid_command;

import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import com.piggest.minecraft.bukkit.raid_command.nms.NMS_manager;

public class Raid_cycle extends BukkitRunnable {
	private Villager central_villager;
	private int times = 0;
	private int completed_times = 0;

	public Raid_cycle(Villager villager, int times) {
		this.central_villager = villager;
		this.times = times;
	}

	@Override
	public void run() {
		if (this.times > 0 && this.completed_times >= this.times) {
			Raid_command.instance.getServer().broadcastMessage("袭击保卫成功");
			this.cancel();
			Raid_command.instance.get_raid_cycle().remove(this.central_villager);
			return;
		}
		int level = completed_times + 1;
		Raid_command.instance.getServer().broadcastMessage("第" + level + "次袭击开始");
		NMS_manager.raid_provider.trigger_raid(central_villager.getLocation(), level);
		this.completed_times++;
	}

}
