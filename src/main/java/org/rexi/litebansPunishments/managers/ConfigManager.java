package org.rexi.litebansPunishments.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.rexi.litebansPunishments.LitebansPunishments;
import org.rexi.litebansPunishments.items.ItemBuilder;

public class ConfigManager {
    public static FileConfiguration getConfig() {
        return LitebansPunishments.getInstance().getConfig();
    }

    public static ItemStack getItem(String path, String name) {
        return ItemBuilder.fromConfig(getConfig(), path, name);
    }
}
