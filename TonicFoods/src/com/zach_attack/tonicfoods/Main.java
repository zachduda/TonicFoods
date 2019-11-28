package com.zach_attack.tonicfoods;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.zach_attack.tonicfoods.Utils.FResult;
import com.zach_attack.tonicfoods.MetricsLite;

public class Main extends JavaPlugin implements Listener {
	public TonicFoodsAPI api;
	
	static String prefix = "&8[&aTonicFoods&8]&r";
	private boolean sounds = true;
	private boolean updatecheck = true;
	private String popsound = "ENTITY_CHICKEN_EGG";
	private String basssound = "BLOCK_NOTE_BLOCK_BASS";
	
	public void onEnable() {
		if(!Bukkit.getBukkitVersion().contains("1.14")) {
			getLogger().warning("THIS PLUGIN IS DESIGNED FOR 1.14.4 ONLY. It may not work for 1.13 or below!");
		}
		
		api = new TonicFoodsAPI();
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		Utils.generateExample();
		Utils.allFilesUppercase();
		
		updateConfig();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		if(Utils.getFoods().size() == 0) {
			getLogger().info("Couldn't find any custom food files! To regenerate the default template for a food file, delete your food folder, and then simply reload the plugin.");
		} else {
			getLogger().info("Found " + Utils.getFoods().size() + " custom foods.");
		}
		
		if(getConfig().getBoolean("Settings.Update-Check")) {
			new Updater(this).checkForUpdate();
			updatecheck = true;
		} else {
			getLogger().info("[!] Update Checking was disabled.");
			updatecheck = false;
		}
		
		// Metrics to help support me. <3
		@SuppressWarnings("unused")
		MetricsLite metrics = new MetricsLite(this);
	}
	
	private void updateConfig() {
		prefix = getConfig().getString("Messages.Prefix", "&8[&aTonicFoods&8]&r");
		sounds = getConfig().getBoolean("Settings.Sounds.Enabled", true);
		popsound = getConfig().getString("Settings.Sounds.Pop", "ENTITY_CHICKEN_EGG");
		basssound = getConfig().getString("Settings.Sounds.Bass", "BLOCK_NOTE_BLOCK_BASS");
		updatecheck = getConfig().getBoolean("Settings.Update-Check");
	}
	
	private void pop(CommandSender sender) {
		if(sounds) {
			try {
				if(sender instanceof Player) {
					Player p = (Player)sender;
					p.playSound(p.getLocation(), Sound.valueOf(popsound), 2.0F, 2.0F);
				}
			} catch (Exception err) {
				sounds = false;
				getLogger().warning("Incorrect sound name for the POP sound in the config.yml, disabling sounds.");
			}
		}
	}
	
	private void bass(CommandSender sender) {
		if(sounds) {
			try {
				if(sender instanceof Player) {
					Player p = (Player)sender;
					p.playSound(p.getLocation(), Sound.valueOf(basssound), 2.0F, 1.3F);
				}
			} catch (Exception err) {
				sounds = false;
				getLogger().warning("Incorrect sound name for the BASS sound in the config.yml, disabling sounds.");
			}
		}
	}
	
	private void noPermission(CommandSender sender) {
		bass(sender);
		Msgs.sendP(sender, getConfig().getString("Messages.No-Permission"));
	}
	
    public boolean onCommand(CommandSender sender, Command command, String cmdstring, String[] args)
    {
   	 	if(command.getName().equalsIgnoreCase("tonicfoods")) {
   	 		if(args.length == 0) {
	 				if(!sender.hasPermission("tonicfoods.give") && !sender.isOp() && !sender.hasPermission("tonicfoods.admin")) {
  	 					pop(sender);
  	 	   	 			Msgs.send(sender, "&f");
  	 	   	 			Msgs.send(sender, "&a&lT&r&aonic&f&lF&r&food");
  	 	   	 			Msgs.send(sender, "&8&l> &7A plugin by &f&lzach_attack &c\u2764");
  	 	   	 			Msgs.send(sender, "&f");
  	 					return true;
  	 				}
   	 			Msgs.send(sender, "&f");
   	 			Msgs.send(sender, "&a&lT&r&aonic&f&lF&r&food");
   	 			Msgs.send(sender, "&8&l> &7Do command &f&l/tfood help &7for help.");
   	 			Msgs.send(sender, "&f");
   	 			pop(sender);
   	 			return true;
   	 		}
   	 		
   	 		if(args.length >= 1) {
   	 			if(args[0].equalsIgnoreCase("help")) {
  	 				if(!sender.hasPermission("tonicfoods.give") && !sender.isOp() && !sender.hasPermission("tonicfoods.admin")) {
  	 					noPermission(sender);
  	 					return true;
  	 				}
  	 				
   	 				Msgs.send(sender, "&f");
   	 				Msgs.send(sender, "&a&lT&r&aonic&f&lF&r&food");
   	 				Msgs.send(sender, "   &8&l> &f&l/tfood help &7Shows this help page.");
   	 				Msgs.send(sender, "   &8&l> &f&l/tfood reload &7Reloads the configuration.");
   	 				Msgs.send(sender, "   &8&l> &f&l/tfood version &7Shows your plugin version.");
   	 				Msgs.send(sender, "   &8&l> &f&l/tfood food &7Shows available food.");
   	 				Msgs.send(sender, "   &8&l> &f&l/tfood give (user) (food) (amount) &7Gives a user a food item!");
   	 				Msgs.send(sender, "&f");
   	 				pop(sender);
   	 				return true;
   	 			}
   	 			
   	 			if(args[0].equalsIgnoreCase("version")) {
   	 				Msgs.send(sender, "&f");
   	 				Msgs.send(sender, "&a&lT&r&aonic&f&lF&r&food");
   	 				Msgs.send(sender, "   &8&l> &7You are running &f&lv" + getDescription().getVersion());
   	 				Msgs.send(sender, "&f");
   	 				pop(sender);
   	 				return true;
   	 			}
   	 			
  	 			if(args[0].equalsIgnoreCase("reload")) {
  	 				if(!sender.hasPermission("tonicfoods.admin") && !sender.isOp()) {
  	 					noPermission(sender);
  	 					return true;
  	 				}
  	 				
   	 				reloadConfig();
   	 				updateConfig();
   	 				Utils.generateExample();  // Just in case :)
   	 				Utils.allFilesUppercase(); // Just gotta double check.
   	 				
   	 				Msgs.send(sender, "&f");
   	 				Msgs.send(sender, "&a&lT&r&aonic&f&lF&r&food");
   	 				Msgs.send(sender, "   &8&l> &7Reloaded the configuration.");
   	 				Msgs.send(sender, "&f");
   	 				pop(sender);
   	 				return true;
   	 			}
   	 			
   	 			if(args[0].equalsIgnoreCase("food")) {
  	 				if(!sender.hasPermission("tonicfoods.give") && !sender.isOp()) {
  	 					noPermission(sender);
  	 					return true;
  	 				}
  	 				
   	 				Msgs.send(sender, "&f");
   	 				
   	 				if(Utils.getFoodFileNames().size() == 0) {
   	 					Msgs.send(sender, "&c&lNo Available Food");
   	 					Msgs.send(sender, "   &8&l> &fIt looks like your food folder is empty...");
   	 					Msgs.send(sender, "&f");
   	 					Msgs.send(sender, "&7&oTo automatically generate a food file, simply delete your current food folder and reload the plugin.");
   	 					bass(sender);
   	 				} else {
   	   	 				Msgs.send(sender, "&a&lAvailable Food:");
   	 					for(String food : Utils.getFoodFileNames()) {
   	 						Msgs.send(sender, "   &8&l> &f&l" + food);
   	 					}
   	 					pop(sender);
   	 				}
   	 				
	 				Msgs.send(sender, "&f");
   	 				return true;
   	 			}
   	 			
   	 			if(args[0].equalsIgnoreCase("give")) {
  	 				if(!sender.hasPermission("tonicfoods.give") && !sender.isOp()) {
  	 					noPermission(sender);
  	 					return true;
  	 				}
  	 				
   	 				if(args.length == 1) {
   	 					Msgs.sendP(sender, "&c&lOops! &fYou must provide a player to do that to.");
   	 					bass(sender);
   	 					return true;
   	 				}
   	 				
   	 				Player target = Bukkit.getServer().getPlayer(args[1]);
   				
   					if (target == null) {
   						Msgs.sendP(sender, "&c&lOops! &fPlayer " + args[1] + " &fisn't online to do that to.");
   						bass(sender);
   						return true;
   					}
   					
   					if(args.length == 2) {
   						Msgs.sendP(sender, "&c&lMissing Food Name. &fMake sure you do: &e/tfood give " + target.getName() + " &6&l(food)");
   						bass(sender);
   						return true;
   					}
   					
   					String food = args[2].toUpperCase();
   					
   					if(!Utils.foodExists(food)) {
   						Msgs.sendP(sender, "&c&lNot Found. &fDo &7&l/tfood food &fto see the available food.");
   						bass(sender);
   						return true;
   					}
   					
   					int amount = 1;
   					
   					if(args.length == 4) {
   						if(!args[0].equals("0") && !(args[0].startsWith("0") && args[0].endsWith("0"))) {
   			        	if (args[3].matches("[0-9]+")) {
   			        		amount = Integer.parseInt(args[3]);
   			        	}}
   					}
   					
   					if(target.getInventory().firstEmpty() == -1) {
   						bass(sender);
   						Msgs.sendP(sender, "&c&lFull Inv. &f" + target.getName() + " has a full inventory & cannot hold this item.");
   						return true;
   					}
   					
   					FResult r = Utils.giveFood(target, food, amount);
   					
   					if(r == FResult.INVALID_FOOD) {
   						bass(sender);
   						Msgs.sendP(sender, "&c&lError. &fThat food has an invalid type for a material/food.");
   					} else if(r == FResult.NO_FOOD) {
   						// This should NEVER happen, as .foodExists(food) should catch it.
   						Msgs.sendP(sender, "&c&lNot Found. &fThat food does not exist.");
   						bass(sender);
   					} else if(r == FResult.NO_FOLDER) {
   						bass(sender);
   						Msgs.sendP(sender, "&c&lError. &fThe foods folder doesn't exist. &7Try reloading your plugin!");
   					} else if(r == FResult.DONE){
   						// All went well.
   						
   						// API EVENT
   						TonicFoodGiveEvent fse = new TonicFoodGiveEvent(sender, target, amount, food);
   						Bukkit.getPluginManager().callEvent(fse);
   						if (fse.isCancelled()) {
   							return true;
   						}
   						// END API EVENT 
   						
   						pop(sender);
   						pop(target);
   						Msgs.sendP(sender, "&fGave " + target.getName() + " &7&l" + amount + "x &fof &7&l" + food);
   					} else {
   						// Unknown Error, hopefully we never see this messages.
   						Msgs.sendP(sender, "&c&lError. &fSomething didn't go right here.");
   					}
   					
   					return true;
   	 			}
   	 		}
   	 		
   	 		Msgs.sendP(sender, "&c&lError. &fThat sub-command doesn't exist.");
   	 		bass(sender);
   		 	return true;
   	 	}
   	 	
   	 	return true;
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onEat(PlayerItemConsumeEvent e) {
    	Utils.ateItem(e.getPlayer(), e.getItem());
    }
    
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if(updatecheck) {
		if(p.hasPermission("puuids.admin") || p.isOp()) {
		if (Updater.outdated) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
			  {
			    public void run()
			     {
			    	try {
			    		Msgs.sendP(p, "&c&lOutdated Plugin! &7Running v" + getDescription().getVersion()
							+ " while the latest is &f&l" + Updater.outdatedversion);
			    		pop(p);
			    	} catch (Exception err) {
			    		sounds = false;
			    	}
			}}, 50L);
		}}}
		
		if (p.getUniqueId().toString().equals("6191ff85-e092-4e9a-94bd-63df409c2079")) {
			Msgs.send(p, "&7This server is running &fTonicFoods &6v" + getDescription().getVersion()
			+ " &7for " + Bukkit.getBukkitVersion().replace("-SNAPSHOT", ""));
		}
	}
}
