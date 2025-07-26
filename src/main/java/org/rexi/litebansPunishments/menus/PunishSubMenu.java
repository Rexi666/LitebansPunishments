package org.rexi.litebansPunishments.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.rexi.litebansPunishments.items.ItemBuilder;
import org.rexi.litebansPunishments.managers.ConfigManager;
import org.rexi.litebansPunishments.managers.MessagesManager;
import org.rexi.litebansPunishments.managers.MenuManager;

import java.util.Set;

public class PunishSubMenu {

    public static void open(Player staff, String target, String reason, String type) {
        Inventory inv = Bukkit.createInventory(null, 27,
                MessagesManager.getRaw("menu.submenu-title",
                        "%target%", target,
                        "%reason%", reason,
                        "%type%", type));

        ConfigurationSection timesSection = ConfigManager.getConfig()
                .getConfigurationSection("punishments." + reason + ".actions." + type + ".times");

        if (timesSection == null) {
            staff.sendMessage(MessagesManager.get("errors.nosubmenusection"));
            return;
        }

        int slot = 10;
        Set<String> times = timesSection.getKeys(false);
        for (String time : times) {
            ConfigurationSection timeSection = timesSection.getConfigurationSection(time);
            if (timeSection == null) continue;

            ItemStack item = ItemBuilder.simpleFromConfig(timeSection, time, type, time);

            String displayName = timeSection.getString("display");
            if (displayName != null) {
                displayName = ChatColor.translateAlternateColorCodes('&', displayName);
                item = ItemBuilder.setDisplayName(item, displayName);
            }

            inv.setItem(slot++, item);
        }

        ConfigurationSection backSection = ConfigManager.getConfig().getConfigurationSection("back");
        ItemStack backItem = ItemBuilder.fromSimpleSection(backSection, "back");
        inv.setItem(18, backItem);

        MenuManager.openedSubMenus.put(staff.getUniqueId(), new MenuManager.SubMenuData(target, reason, type));
        staff.openInventory(inv);
    }
}
