package com.zach_attack.tonicfoods;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class TonicFoodsAPI {
	
	public static Utils.FResult giveFood(Player p, String food, int amount) {
		return Utils.giveFood(p, food, amount);
	}
	
	public static ArrayList<String> getFoods() {
		return Utils.getFoodFileNames();
	}
}
