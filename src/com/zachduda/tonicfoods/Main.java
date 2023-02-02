package com.zachduda.tonicfoods;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin implements Listener {
    public TonicFoodsAPI api;

    static String prefix = "&8[&aTonicFoods&8]&r";
    static String header = "&r&a&lT&r&aonic&f&lF&r&foods&r";
    private boolean sounds = true;
    private boolean update_check = true;
    private boolean can_metrics = true;
    private String pop_sound = "ENTITY_CHICKEN_EGG";
    private String bass_sound = "BLOCK_NOTE_BLOCK_BASS";
    
    private final String version = Bukkit.getBukkitVersion().replace("-SNAPSHOT", "");

    private final boolean supported = version.contains("1.16") || version.contains("1.17") || version.contains("1.18") || version.contains("1.19");

    private boolean seen_lengthy_cmd_tip = false;
    public void onEnable() {
    	if (!supported) {
        	Bukkit.getScheduler().runTask(this, () -> getLogger().warning("> This version of TonicFoods may not work for this version of Minecraft. (Supports 1.19 through 1.16)"));
        }

        api = new TonicFoodsAPI();
        getConfig().options().copyDefaults(true);
        saveConfig();

        Utils.generateExample();
        Utils.allFilesUppercase();
        Utils.updateFoodFolder();

        updateConfig();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        if (Utils.getFoods().size() == 0) {
            getLogger().info("Couldn't find any custom food files! To regenerate the default template for a food file, delete your food folder, and then simply reload the plugin.");
        } else {
            getLogger().info("Found " + Utils.getFoods().size() + " custom foods.");
        }

        if (update_check) {
            new Updater(this).checkForUpdate();
        } else {
            getLogger().info("[!] Update Checking was disabled.");
        }

        // Metrics to help support me. <3
        if(can_metrics) {
            new Metrics(this, 72274);
        } else {
            getLogger().warning("You've disabled Metrics. Please consider enabling Metrics as it shows me how many servers support me!");
        }
    }

    private void updateConfig() {
        FileConfiguration c = getConfig();
        if(c.getInt("Version", 0) != 2) {
            c.options().copyDefaults(true);
            saveDefaultConfig();
            c.set("Version", 2);
            saveConfig();
            reloadConfig();
        }
        prefix = getConfig().getString("Messages.Prefix", "&8[&aTonicFoods&8]&r");
        sounds = getConfig().getBoolean("Settings.Sounds.Enabled", true);
        pop_sound = getConfig().getString("Settings.Sounds.Pop", "ENTITY_CHICKEN_EGG");
        bass_sound = getConfig().getString("Settings.Sounds.Bass", "BLOCK_NOTE_BLOCK_BASS");
        update_check = getConfig().getBoolean("Settings.Others.Update-Check", true);
        can_metrics = getConfig().getBoolean("Settings.Others.Metrics", true);
        header = getConfig().getString("Messages.Header", "&r&a&lT&r&aonic&f&lF&r&foods&r");
    }

    private void pop(CommandSender sender) {
        if (sounds) {
            try {
                if (sender instanceof Player p) {
                    p.playSound(p.getLocation(), Sound.valueOf(pop_sound), 2.0F, 2.0F);
                }
            } catch (Exception err) {
                sounds = false;
                getLogger().warning("Incorrect sound name for the POP sound in the config.yml, disabling sounds.");
            }
        }
    }

    private void bass(CommandSender sender) {
        if (sounds) {
            try {
                if (sender instanceof Player p) {
                    p.playSound(p.getLocation(), Sound.valueOf(bass_sound), 2.0F, 1.3F);
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

    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String cmdstring, String[] args) {
        if (command.getName().equalsIgnoreCase("tonicfoods")) {
            if (args.length == 0) {
                if (!sender.hasPermission("tonicfoods.give") && !sender.isOp() && !sender.hasPermission("tonicfoods.admin")) {
                    pop(sender);
                    Msgs.send(sender, "&f");
                    Msgs.send(sender, header);
                    Msgs.send(sender, "   &8&l> &7A plugin by &f&lzach_attack &c❤");
                    Msgs.send(sender, "&f");
                    return true;
                }
                Msgs.send(sender, "&f");
                Msgs.send(sender, header);
                Msgs.send(sender, "&8&l> &7Do command &f&l/" + cmdstring.toLowerCase() + " help &7for help.");
                Msgs.send(sender, "&f");
                pop(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("tonicfoods.give") && !sender.isOp() && !sender.hasPermission("tonicfoods.admin")) {
                    noPermission(sender);
                    return true;
                }

                final String cm = cmdstring.toLowerCase();

                Msgs.send(sender, "&f");
                Msgs.send(sender, header);
                Msgs.send(sender, "   &8&l> &f&l/" + cm + " help &7Shows this help page.");
                Msgs.send(sender, "   &8&l> &f&l/" + cm + " reload &7Reloads the configuration.");
                Msgs.send(sender, "   &8&l> &f&l/" + cm + " version &7Shows your plugin version.");
                Msgs.send(sender, "   &8&l> &f&l/" + cm + " food &7Shows available food.");
                Msgs.send(sender, "   &8&l> &f&l/" + cm + " give (user) (food) (amount) &7Gives a user a food item!");
                if(!seen_lengthy_cmd_tip && cm.equals("tonicfoods")) {
                    seen_lengthy_cmd_tip = true;
                    Msgs.send(sender, " ");
                    Msgs.send(sender, "&7&oTip: It might be easier to use &r&a/tf &7&oinstead of &r&7/tonicfoods");
                }
                Msgs.send(sender, " ");
                pop(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("version")) {
                Msgs.send(sender, "&f");
                Msgs.send(sender, header);
                Msgs.send(sender, "   &8&l> &7You are running &f&lv" + getDescription().getVersion());
                Msgs.send(sender, "&f");
                pop(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("tonicfoods.admin") && !sender.isOp()) {
                    noPermission(sender);
                    return true;
                }

                reloadConfig();
                updateConfig();
                Utils.generateExample(); // Just in case :)
                Utils.allFilesUppercase(); // Just gotta double check.
                Utils.updateFoodFolder(); // update files

                Msgs.send(sender, "&f");
                Msgs.send(sender, header);
                Msgs.send(sender, "   &8&l> &7Reloaded the configuration.");
                Msgs.send(sender, "&f");
                pop(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("food")) {
                if (!sender.hasPermission("tonicfoods.give") && !sender.isOp()) {
                    noPermission(sender);
                    return true;
                }

                Msgs.send(sender, "&f");

                if (Utils.getFoodFileNames().size() == 0) {
                    Msgs.send(sender, "&c&lNo Available Food");
                    Msgs.send(sender, "   &8&l> &fIt looks like your food folder is empty...");
                    Msgs.send(sender, "&f");
                    Msgs.send(sender, "&7&oTo automatically generate a food file, simply delete your current food folder and reload the plugin.");
                    bass(sender);
                } else {
                    Msgs.send(sender, "&a&lAvailable Food:");
                    for (String food: Utils.getFoodFileNames()) {
                        Msgs.send(sender, "   &8&l> &f&l" + food);
                    }
                    pop(sender);
                }

                Msgs.send(sender, "&f");
                return true;
            }

            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission("tonicfoods.give") && !sender.isOp()) {
                    noPermission(sender);
                    return true;
                }

                if (args.length == 1) {
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

                if (args.length == 2) {
                    Msgs.sendP(sender, "&c&lMissing Food Name. &fMake sure you do: &e/tfood give " + target.getName() + " &6&l(food)");
                    bass(sender);
                    return true;
                }

                String food = args[2].toUpperCase();

                if (!Utils.foodExists(food)) {
                    Msgs.sendP(sender, "&c&lNot Found. &fDo &7&l/tfood food &fto see the available food.");
                    bass(sender);
                    return true;
                }

                int amount = 1;

                if (args.length == 4) {
                    if (!args[0].equals("0") && !(args[0].startsWith("0") && args[0].endsWith("0"))) {
                        if (args[3].matches("[0-9]+")) {
                            amount = Integer.parseInt(args[3]);
                        }
                    }
                }

                if (target.getInventory().firstEmpty() == -1) {
                    bass(sender);
                    Msgs.sendP(sender, "&c&lFull Inv. &f" + target.getName() + " has a full inventory & cannot hold this item.");
                    return true;
                }

                final Utils.FoodResult r = Utils.giveFood(target, food, amount);

                if (r == Utils.FoodResult.INVALID_FOOD) {
                    bass(sender);
                    Msgs.sendP(sender, "&c&lError. &fThat food has an invalid type for a material/food.");
                } else if (r == Utils.FoodResult.NO_FOOD) {
                    // This should NEVER happen, as .foodExists(food) should catch it.
                    Msgs.sendP(sender, "&c&lNot Found. &fThat food does not exist.");
                    bass(sender);
                } else if (r == Utils.FoodResult.NO_FOLDER) {
                    bass(sender);
                    Msgs.sendP(sender, "&c&lError. &fThe foods folder doesn't exist. &7Try reloading your plugin!");
                } else if (r == Utils.FoodResult.DONE) {
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

            Msgs.sendP(sender, "&c&lError. &fThat sub-command doesn't exist.");
            bass(sender);
            return true;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEat(PlayerItemConsumeEvent e) {
        Utils.ateItem(e.getPlayer(), e.getItem());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        try {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                Player p = e.getPlayer();
                if (update_check) {
                    if (p.hasPermission("puuids.admin") || p.isOp()) {
                        if (Updater.isOutdated()) {
                                Msgs.sendP(p, "&c&lOutdated Plugin! &7Running v" + getDescription().getVersion() +
                                    " while the latest is &f&l" + Updater.getPostedVersion());
                        }
                    }
                }

                if (p.getUniqueId().toString().equals("6191ff85-e092-4e9a-94bd-63df409c2079")) {
                    Msgs.send(p, "&7This server is running &fTonicFoods &6v" + getDescription().getVersion() +
                        " &7for " + Bukkit.getBukkitVersion().replace("-SNAPSHOT", ""));
                }
            });
        } catch (Exception ignore) {}
    }
}
