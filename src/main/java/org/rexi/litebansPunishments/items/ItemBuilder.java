package org.rexi.litebansPunishments.items;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rexi.litebansPunishments.managers.MessagesManager;

import java.util.Collections;

public class ItemBuilder {
    public static ItemStack fromConfig(org.bukkit.configuration.file.FileConfiguration config, String path, String name) {
        ConfigurationSection section = config.getConfigurationSection("punishments." + name);
        if (section == null) {
            return new ItemStack(Material.STONE); // Default item if section is not found
        }

        Material mat = Material.valueOf(section.getString("item", "STONE"));
        String display = section.getString("name", name);

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display.replace("&", "§"));
        meta.setLore(Collections.singletonList(MessagesManager.getRaw("menu.clickToSelect")));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack simpleFromConfig(ConfigurationSection section, String name) {
        if (section == null) return new ItemStack(Material.STONE);

        Material mat;
        try {
            mat = Material.valueOf(section.getString("item", "STONE").toUpperCase());
        } catch (IllegalArgumentException e) {
            mat = Material.STONE;
        }

        String display = section.getString("name", name);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display.replace("&", "§"));
        meta.setLore(Collections.singletonList(MessagesManager.getRaw("menu.clickToSelect")));
        item.setItemMeta(meta);

        return item;
    }
}
