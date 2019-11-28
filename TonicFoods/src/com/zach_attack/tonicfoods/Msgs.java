package com.zach_attack.tonicfoods;

import org.bukkit.command.CommandSender;

public class Msgs {
	static void send(CommandSender sender, String msg) {
		sender.sendMessage(Utils.convertColor(msg));
	}
	
	static void sendP(CommandSender sender, String msg) {
		sender.sendMessage(Utils.convertColor(Main.prefix + " &r" + msg));
	}
}
