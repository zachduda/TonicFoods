package com.zachduda.tonicfoods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.io.Files;

public class Utils {
    private static final Main plugin = Main.getPlugin(Main.class);


    private static File getFolder(boolean update) {
        if(folder == null || update) {
            folder = new File(plugin.getDataFolder(), File.separator + "Foods");
        }
        return folder;
    }

    static void updateFoodFolder() {
        getFolder(true);
    }

    private static File folder = getFolder(false);

    enum FoodResult {
        NO_FOLDER,
        NO_FOOD,
        INVALID_FOOD,
        DONE
    }

    static boolean foodExists(String food) {
        if (!folder.exists()) {
            return false;
        }

        File f = new File(folder, File.separator + food.toUpperCase() + ".yml");

        return f.exists();
    }

    static void allFilesUppercase() {
        int renamed = 0;

        for (File foodfile: Objects.requireNonNull(folder.listFiles())) {
            String path = foodfile.getPath();

            if (Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                File f = new File(path);
                String name = Files.getNameWithoutExtension(f.getName());
                if (!name.equals(name.toUpperCase())) {
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
        if (!folder.exists()) {
            return FoodResult.NO_FOLDER;
        }

        File f = new File(folder, File.separator + food.toUpperCase() + ".yml");

        if (!f.exists()) {
            return FoodResult.NO_FOOD;
        }

        FileConfiguration setfood = YamlConfiguration.loadConfiguration(f);

        String itemname = convertColor(setfood.getString("Item.Name"));
        Material itemtype = Material.getMaterial(Objects.requireNonNull(setfood.getString("Item.Type")).toUpperCase());

        if (itemtype == null) {
            plugin.getLogger().warning("Invalid Material/Food: " + Objects.requireNonNull(setfood.getString("Item.Type")).toUpperCase() + " (Found in file " + food.toUpperCase() + ".yml");
            return FoodResult.INVALID_FOOD;
        }

        List < String > lore = setfood.getStringList("Item.Lore");

        ItemStack item = new ItemStack(itemtype);
        ItemMeta itemm = item.getItemMeta();
        assert itemm != null;
        itemm.setDisplayName(itemname);

        itemm.setLore(convertListColor(lore));
        item.setItemMeta(itemm);

        for (int i = 0; i < amount; i++) {
            p.getInventory().addItem(item);
        }

        return FoodResult.DONE;
    }

    static void generateExample() {
        if (folder.exists()) {
            // Only generates on 1st run.
            return;
        }

        File f = new File(folder, File.separator + "MYTESTFOOD.yml");
        FileConfiguration setfood = YamlConfiguration.loadConfiguration(f);

        setfood.options().header("\nThis is an example food. For the latest item types, please use:\nhttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n\nAdditionally, you can find the Potion Types here:\nhttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html\n");
        setfood.set("Item.Type", "COOKED_BEEF");
        setfood.set("Item.Name", "&c&l&o&nFancy Beef");

        ArrayList <String> lore = new ArrayList<>();
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
        } catch (IOException e) {
            plugin.getLogger().info("Error trying to create example food.yml: ");
            e.printStackTrace();
        }

        lore.clear();
    }

    static ArrayList < Material > getFoods() {
        ArrayList < Material > foods = new ArrayList<>();

        for (File foodfile: Objects.requireNonNull(folder.listFiles())) {
            String path = foodfile.getPath();

            if (Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                File f = new File(path);
                FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
                foods.add(Material.getMaterial(setcache.getString("Item.Type")));
            }
        }
        return foods;
    }

    static ArrayList < String > getFoodFileNames() {
        ArrayList < String > foods = new ArrayList < String > ();

        for (File foodfile: Objects.requireNonNull(folder.listFiles())) {
            String path = foodfile.getPath();

            if (Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                File f = new File(path);
                foods.add(Files.getNameWithoutExtension(f.getName()));
            }
        }
        return foods;
    }

    static String foodTypeToFileName(Material food) {
        for (File foodfile: Objects.requireNonNull(folder.listFiles())) {
            String path = foodfile.getPath();

            if (Files.getFileExtension(path).equalsIgnoreCase("yml")) {
                File f = new File(path);
                FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
                if (setcache.contains("Item.Type") && setcache.getString("Item.Type") != null) {
                    if (Material.getMaterial(Objects.requireNonNull(setcache.getString("Item.Type")).toUpperCase()) == food) {
                        return f.getName();
                    }
                }
            }
        }
        return null;
    }

    static String convertColor(String msg) {
		msg = msg.replaceAll("&#", "#");
		Pattern pattern = Pattern.compile("(&#|#|&)[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(msg);
		while (matcher.find()) {
			String hexCode = msg.substring(matcher.start(), matcher.end());
			String replaceAmp = hexCode.replaceAll("&#", "x");
			String replaceSharp = replaceAmp.replace('#', 'x');

			char[] ch = replaceSharp.toCharArray();
			StringBuilder builder = new StringBuilder();
			for (char c : ch) {
				builder.append("&").append(c);
			}

			msg = msg.replace(hexCode, builder.toString());
			matcher = pattern.matcher(msg);
		}
		return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private static List < String > convertListColor(List < String > msg) {
        ArrayList < String > result = new ArrayList<>();
        for (String msgs: msg) {
            result.add(convertColor(msgs));
        }
        return result;
    }

    static void ateItem(Player p, ItemStack i) {
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
            Material itemtype = Material.getMaterial(Objects.requireNonNull(setfood.getString("Item.Type")).toUpperCase());
            List < String > lore = setfood.getStringList("Item.Lore");

            assert itemtype != null;
            ItemStack item = new ItemStack(itemtype);
            ItemMeta itemm = item.getItemMeta();
            assert itemm != null;
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
