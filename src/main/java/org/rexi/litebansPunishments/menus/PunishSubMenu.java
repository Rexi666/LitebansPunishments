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

    public static void open(Player staff, String target, String reason) {
        Inventory inv = Bukkit.createInventory(null, 27,
                MessagesManager.getRaw("menu.submenu-title",
                        "%target%", target,
                        "%reason%", reason));

        ConfigurationSection section = ConfigManager.getConfig()
                .getConfigurationSection("punishments." + reason + ".actions");

        if (section == null) {
            staff.sendMessage(MessagesManager.get("errors.nosubmenusection"));
            return;
        }

        Set<String> types = section.getKeys(false); // ban, mute, warn, kick...
        int slot = 11;
        for (String type : types) {
            ConfigurationSection typeSection = section.getConfigurationSection(type);
            if (typeSection == null) continue;

            ConfigurationSection timesSection = typeSection.getConfigurationSection("times");

            if (timesSection == null) {
                // No hay sub-tiempos, mostramos un único item para este tipo
                ItemStack item = ItemBuilder.simpleFromConfig(typeSection, type, type, null);

                String displayName = typeSection.getString("display");
                if (displayName != null) {
                    displayName = ChatColor.translateAlternateColorCodes('&', displayName);
                    item = ItemBuilder.setDisplayName(item, displayName);
                }

                inv.setItem(slot++, item);
            } else {
                Set<String> times = timesSection.getKeys(false); // Correcto: keys dentro de "times"
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
            }
        }

        MenuManager.openedSubMenus.put(staff.getUniqueId(), new MenuManager.SubMenuData(target, reason));
        staff.openInventory(inv);
    }
}