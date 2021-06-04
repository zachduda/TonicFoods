package com.zach_attack.tonicfoods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.io.Files;

public class Utils {
    private static Main plugin = Main.getPlugin(Main.class);

    static enum FoodResult {
        NO_FOLDER,
        NO_FOOD,
        INVALID_FOOD,
        DONE
    }

    static boolean foodExists(String food) {
        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        if (!folder.exists()) {
            return false;
        }

        File f = new File(folder, File.separator + food.toUpperCase() + ".yml");

        if (!f.exists()) {
            return false;
        }

        return true;
    }

    static void allFilesUppercase() {
        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        int renamed = 0;

        for (File foodfile: folder.listFiles()) {
            String path = foodfile.getPath();

            if (!Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                // NOT A VALID FILE
            } else {

                File f = new File(path);
                String name = FilenameUtils.removeExtension(f.getName());
                if (!StringUtils.isAllUpperCase(name)) {
                    f.renameTo(new File(folder, name.toUpperCase() + ".yml"));
                    renamed++;
                }
            }
        }

        if (renamed != 0) {
            plugin.getLogger().warning("Renamed " + renamed + " files to all UPPERCASE. This is required to function properly!");
        }
    }

    static FoodResult giveFood(Player p, String food, int amount) {
        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        if (!folder.exists()) {
            return FoodResult.NO_FOLDER;
        }

        File f = new File(folder, File.separator + food.toUpperCase() + ".yml");

        if (!f.exists()) {
            return FoodResult.NO_FOOD;
        }

        FileConfiguration setfood = YamlConfiguration.loadConfiguration(f);

        String itemname = convertColor(setfood.getString("Item.Name"));
        Material itemtype = Material.getMaterial(setfood.getString("Item.Type").toUpperCase());

        if (itemtype == null) {
            plugin.getLogger().warning("Invalid Material/Food: " + setfood.getString("Item.Type").toUpperCase() + " (Found in file " + food.toUpperCase() + ".yml");
            return FoodResult.INVALID_FOOD;
        }

        List < String > lore = setfood.getStringList("Item.Lore");

        ItemStack item = new ItemStack(itemtype);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName(itemname);

        itemm.setLore(convertListColor(lore));
        item.setItemMeta(itemm);

        for (int i = 0; i < amount; i++) {
            p.getInventory().addItem(item);
        }

        return FoodResult.DONE;
    }

    static void generateExample() {
        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        if (folder.exists()) {
            // Only generates on 1st run.
            return;
        }

        File f = new File(folder, File.separator + "MYTESTFOOD.yml");
        FileConfiguration setfood = YamlConfiguration.loadConfiguration(f);

        setfood.options().header("\nThis is an example food. For the latest item types, please use:\nhttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n\nAdditionally, you can find the Potion Types here:\nhttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html\n");
        setfood.set("Item.Type", "COOKED_BEEF");
        setfood.set("Item.Name", "&c&l&o&nFancy Beef");

        ArrayList < String > lore = new ArrayList < String > ();
        lore.add("&7Only the best meat");
        lore.add("&7shall provide you with");
        lore.add("&f&lgreat &7strength.");

        setfood.set("Item.Lore", lore);
        setfood.set("Effect.Type", "DAMAGE_RESISTANCE");
        setfood.set("Effect.Duration", 20);
        setfood.set("Effect.Power", 1);

        setfood.set("Permission.Node", "mycustomperm.steak");
        setfood.set("Permission.No-Permission-Msg", "None");

        setfood.set("Message", "None");
        
        try {
            setfood.save(f);
        } catch (IOException e) {}

        lore.clear();
    }

    static ArrayList < Material > getFoods() {

        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        ArrayList < Material > foods = new ArrayList < Material > ();

        for (File foodfile: folder.listFiles()) {
            String path = foodfile.getPath();

            if (!Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                // NOT A VALID FILE
            } else {

                File f = new File(path);
                FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
                foods.add(Material.getMaterial(setcache.getString("Item.Type")));
            }
        }
        return foods;
    }

    static ArrayList < String > getFoodFileNames() {

        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        ArrayList < String > foods = new ArrayList < String > ();

        for (File foodfile: folder.listFiles()) {
            String path = foodfile.getPath();

            if (!Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                // NOT A VALID FILE
            } else {

                File f = new File(path);
                foods.add(FilenameUtils.removeExtension(f.getName().toString()));
            }
        }
        return foods;
    }

    static String foodTypeToFileName(Material food) {
        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        for (File foodfile: folder.listFiles()) {
            String path = foodfile.getPath();

            if (!Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                // NOT A VALID FILE
            } else {

                File f = new File(path);
                FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
                if (setcache.contains("Item.Type") && setcache.getString("Item.Type") != null) {
                    if (Material.getMaterial(setcache.getString("Item.Type").toUpperCase()) == food) {
                        return f.getName();
                    }
                }
            }
        }
        return null;
    }

    static String convertColor(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private static List < String > convertListColor(List < String > msg) {
        ArrayList < String > result = new ArrayList < String > ();
        for (String msgs: msg) {
            result.add(convertColor(msgs));
        }
        return result;
    }

    static void ateItem(Player p, ItemStack i) {
        File folder = new File(plugin.getDataFolder(), File.separator + "Foods");

        if (!folder.exists()) {
            return;
        }

        Material mat = i.getType();

        if (getFoods().contains(mat)) {
            String filename = foodTypeToFileName(mat);

            if (filename == null) {
                return;
            }

            File f = new File(folder, File.separator + foodTypeToFileName(mat));
            FileConfiguration setfood = YamlConfiguration.loadConfiguration(f);

            String itemname = convertColor(setfood.getString("Item.Name"));
            Material itemtype = Material.getMaterial(setfood.getString("Item.Type").toUpperCase());
            List < String > lore = setfood.getStringList("Item.Lore");

            ItemStack item = new ItemStack(itemtype);
            ItemMeta itemm = item.getItemMeta();
            itemm.setDisplayName(itemname);

            itemm.setLore(convertListColor(lore));
            item.setItemMeta(itemm);

            if (!item.isSimilar(i)) {
                return;
            }


            if (setfood.contains("Permission")) {
                String perm = setfood.getString("Permission.Node");
                if (perm != null && !perm.equalsIgnoreCase("none")) {
                    if (!p.hasPermission(perm)) {
                        String permmsg = setfood.getString("Permission.No-Permission-Msg");
                        if (permmsg != null && !permmsg.equalsIgnoreCase("none")) {
                            Msgs.send(p, permmsg);
                        }
                        return;
                    }
                }
            }

            // API EVENT
            TonicFoodEatEvent fse = new TonicFoodEatEvent(p, i);
            Bukkit.getPluginManager().callEvent(fse);
            if (fse.isCancelled()) {
                return;
            }
            // END API EVENT 

            Potions.add(p, setfood.getString("Effect.Type"), setfood.getInt("Effect.Duration"), setfood.getInt("Effect.Power"));

            lore.clear();

            String message = setfood.getString("Message");
            if (message != null && setfood.contains("Message") && !message.equalsIgnoreCase("None")) {
                Msgs.send(p, message);
            }
        }
    }
}
