package org.rexi.litebansPunishments.menus;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.rexi.litebansPunishments.items.ItemBuilder;
import org.rexi.litebansPunishments.managers.ConfigManager;
import org.rexi.litebansPunishments.managers.MenuManager;
import org.rexi.litebansPunishments.managers.MessagesManager;

import java.util.Set;

public class PunishMenu {
    public static void openMainMenu(Player staff, String target) {
        var config = ConfigManager.getConfig();
        var punishmentsSection = config.getConfigurationSection("punishments");
        if (punishmentsSection == null) {
            Bukkit.getLogger().warning((MessagesManager.get("errors.nopunishmentsection")));
            staff.sendMessage(MessagesManager.get("errors.nopunishmentsection-player"));
            return;
        }

        Set<String> keys = ConfigManager.getConfig().getConfigurationSection("punishments").getKeys(false);
        Inventory inv = Bukkit.createInventory(null, 54, MessagesManager.getRaw("menu.menu-title", "%target%", target));

        int slot = 0;
        for (String key : keys) {
            inv.setItem(slot++, ConfigManager.getItem("punishments." + key, key));
        }

        ConfigurationSection historySection = ConfigManager.getConfig().getConfigurationSection("history");
        ItemStack historyItem = ItemBuilder.simpleFromConfig(historySection, "History");
        inv.setItem(53, historyItem);

        MenuManager.openedMenus.put(staff.getUniqueId(), target);
        staff.openInventory(inv);
    }
}