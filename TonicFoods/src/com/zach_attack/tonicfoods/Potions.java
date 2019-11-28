package com.zach_attack.tonicfoods;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potions {
	
	static void add(Player p, String effect, int adduration, int power) {
		PotionEffectType potion = (PotionEffectType.getByName(effect));
    
		int duration = 0;
    	if(p.hasPotionEffect(potion)) {
    		duration = (duration+p.getPotionEffect(potion).getDuration())/20;
    		p.removePotionEffect(potion);
    	}
    	
    	duration = duration+adduration;
    	
		p.addPotionEffect(new PotionEffect(potion, (duration*20)+20, (power-1)));
	}

}
