package com.piggest.minecraft.bukkit.raid_command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.piggest.minecraft.bukkit.raid_command.nms.NMS_manager;
import com.piggest.minecraft.bukkit.raid_command.nms.Raid_info;

public class Raid_command extends JavaPlugin {
	public static Raid_command instance;

	public NMS_manager nms_manager;
	private final String usage = "/raid <不祥征兆等级> <位置或者玩家名>，不输入位置则默认你自己的位置";
	private final String unknown_location = "在控制台执行你必须指定一个位置";

	public Raid_command() {
		super();
		Raid_command.instance = this;
		this.nms_manager = new NMS_manager(Bukkit.getBukkitVersion());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("raid_command.run")) {
			sender.sendMessage("你没有运行袭击的权限");
		}
		int level = 1;
		Location loc = null;
		try {
			level = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(usage);
			return true;
		}
		if (args.length == 1) {
			if (sender instanceof Player) {
				loc = ((Player) sender).getLocation();
			} else if (sender instanceof BlockCommandSender) {
				loc = ((BlockCommandSender) sender).getBlock().getLocation();
			} else {
				sender.sendMessage(unknown_location);
				return true;
			}
		} else if (args.length == 2) {
			Player player = this.getServer().getPlayer(args[1]);
			if (player != null) {
				loc = player.getLocation();
			} else {
				sender.sendMessage("玩家不存在/不在线");
				return true;
			}
		} else if (args.length >= 4) {
			World world;
			if (args.length > 4) {
				world = this.getServer().getWorld(args[4]);
				if (world == null) {
					sender.sendMessage("世界不存在");
					return true;
				}
			} else {
				if (sender instanceof Player) {
					world = ((Player) sender).getWorld();
				} else if (sender instanceof BlockCommandSender) {
					world = ((BlockCommandSender) sender).getBlock().getWorld();
				} else {
					sender.sendMessage("必须指定世界名称");
					return true;
				}
			}
			int x, y, z;
			try {
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
				z = Integer.parseInt(args[3]);
			} catch (Exception e) {
				sender.sendMessage("坐标不合法");
				return true;
			}
			loc = new Location(world, x, y, z);
		} else {
			sender.sendMessage(usage);
			return true;
		}
		Raid_info raid_result = NMS_manager.raid_provider.trigger_raid(loc, level);
		if (raid_result != null) {
			String msg = "袭击触发成功";
			msg += ", 编号: " + raid_result.id;
			msg += ", 不祥征兆等级: " + raid_result.bad_omen_level;
			msg += "\n位置: " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ","
					+ loc.getWorld().getName();
			sender.sendMessage(msg);
		} else {
			sender.sendMessage("袭击触发失败");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		if (args.length == 1) {
			list.add("1");
			list.add("2");
			list.add("3");
			list.add("4");
			list.add("5");
		} else if (args.length == 2) {
			for (Player player : this.getServer().getOnlinePlayers()) {
				list.add(player.getName());
			}
			if (sender instanceof Player) {
				list.add(((Player) sender).getLocation().getBlockX() + "");
			}
		} else if (args.length == 3) {
			if (sender instanceof Player) {
				if (pattern.matcher(args[1]).matches()) {
					list.add(((Player) sender).getLocation().getBlockY() + "");
				}
			}
		} else if (args.length == 4) {
			if (sender instanceof Player) {
				if (pattern.matcher(args[1]).matches()) {
					list.add(((Player) sender).getLocation().getBlockZ() + "");
				}
			}
		} else if (args.length == 5) {
			for (World world : this.getServer().getWorlds()) {
				list.add(world.getName());
			}
		}
		return list;
	}
}
