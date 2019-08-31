package com.piggest.minecraft.bukkit.raid_command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Trigger_raid_listener implements Listener {
	@EventHandler
	public void on_entity_death(EntityDeathEvent event) {
		if (event.getEntityType() == EntityType.VILLAGER) {
			Raid_cycle raid_cycle = Raid_command.instance.get_raid_cycle().get(event.getEntity());
			if (raid_cycle == null) {
				return;
			}
			Raid_command.instance.getServer().broadcastMessage("村民已经被杀害！");
			raid_cycle.cancel();
			Raid_command.instance.get_raid_cycle().remove(event.getEntity());
		}
	}

	@EventHandler
	public void on_name_tag(PlayerInteractAtEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof Villager) {
			Villager villager = (Villager) entity;
			Player player = event.getPlayer();
			if (!player.hasPermission("raid_command.run")) {
				return;
			}
			EquipmentSlot slot = event.getHand();
			ItemStack item = null;
			if (slot == EquipmentSlot.HAND) {
				item = player.getInventory().getItemInMainHand();
			} else if (slot == EquipmentSlot.OFF_HAND) {
				item = player.getInventory().getItemInOffHand();
			}
			if (item != null) {
				if (item.getType() == Material.NAME_TAG) {
					ItemMeta meta = item.getItemMeta();
					String displayname = meta.getDisplayName();
					int times = 1;
					String pattern = "raid:([1-9]\\d*|0)";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(displayname);
					if (m.find()) {
						times = Integer.parseInt(m.group(1));
					}
					meta.setDisplayName("袭击中心村民");
					item.setItemMeta(meta);
					Raid_cycle raid_cycle = new Raid_cycle(villager, times);
					Raid_command.instance.get_raid_cycle().put(villager, raid_cycle);
				}
			}

		}
	}
}
