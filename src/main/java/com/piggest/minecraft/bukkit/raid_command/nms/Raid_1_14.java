package com.piggest.minecraft.bukkit.raid_command.nms;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.craftbukkit.v1_14_R1.CraftRaid;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import net.minecraft.server.v1_14_R1.PersistentRaid;
import net.minecraft.server.v1_14_R1.WorldServer;

public class Raid_1_14 implements Raid_provider {
	Field next_id_field;
	Field bad_omen_field;

	public Raid_1_14() {
		try {
			next_id_field = PersistentRaid.class.getDeclaredField("c");
			next_id_field.setAccessible(true);
			bad_omen_field = net.minecraft.server.v1_14_R1.Raid.class.getDeclaredField("o");
			bad_omen_field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public Raid trigger_raid(Location loc, int bad_omen_level) {
		CraftBlock craft_block = (CraftBlock) loc.getBlock();
		CraftWorld world = (CraftWorld) loc.getWorld();
		boolean disable_raids = world.getGameRuleValue(GameRule.DISABLE_RAIDS);
		if (disable_raids == true) {
			return null;
		}
		WorldServer world_nms = world.getHandle();
		PersistentRaid persistentraid = world_nms.C();

		int next_id = 0;
		Map<Integer, net.minecraft.server.v1_14_R1.Raid> raid_map = persistentraid.a;
		try {
			next_id = (int) next_id_field.get(persistentraid);
			next_id++;
			next_id_field.set(persistentraid, next_id);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		net.minecraft.server.v1_14_R1.Raid raid = new net.minecraft.server.v1_14_R1.Raid(next_id, world_nms,
				craft_block.getPosition());
		CraftRaid craftraid = new CraftRaid(raid);
		if (bad_omen_level > raid.l()) {
			bad_omen_level = raid.l();
		}
		craftraid.setBadOmenLevel(bad_omen_level);
		raid_map.put(next_id, raid);
		persistentraid.b();
		return craftraid;
	}

	public Raid_info get_info(net.minecraft.server.v1_14_R1.Raid raid) {
		if (raid == null) {
			return null;
		}
		Raid_info info = new Raid_info();
		info.id = raid.u();
		info.started = raid.j();
		info.active = raid.v();
		info.bad_omen_level = raid.m();
		info.groups_spawned = raid.k();
		return info;
	}
}
