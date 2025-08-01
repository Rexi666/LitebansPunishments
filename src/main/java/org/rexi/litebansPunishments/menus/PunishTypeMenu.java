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

public class PunishTypeMenu {

    public static void open(Player staff, String target, String reason) {

        ConfigurationSection actionsSection = ConfigManager.getConfig()
                .getConfigurationSection("punishments." + reason + ".actions");

        if (actionsSection == null) {
            staff.sendMessage(MessagesManager.get("errors.nosubmenusection"));
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27,
                MessagesManager.getRaw("menu.type-title",
                        "%target%", target,
                        "%reason%", reason));

        Set<String> types = actionsSection.getKeys(false);

        int slot = 11;
        boolean addedAnyItem = false;

        for (String type : types) {
            ConfigurationSection typeSection = actionsSection.getConfigurationSection(type);
            if (typeSection == null) continue;

            // Añadimos todos los tipos, con o sin tiempos
            ItemStack item = ItemBuilder.simpleFromConfig(typeSection, type, type, type);

            String displayName = typeSection.getString("display");
            if (displayName != null) {
                displayName = ChatColor.translateAlternateColorCodes('&', displayName);
                item = ItemBuilder.setDisplayName(item, displayName);
            }

            inv.setItem(slot++, item);
            addedAnyItem = true;
        }

        if (!addedAnyItem) {
            staff.sendMessage(MessagesManager.get("errors.notypesavailable"));
            return;
        }

        ConfigurationSection backSection = ConfigManager.getConfig().getConfigurationSection("back");
        ItemStack backItem = ItemBuilder.fromSimpleSection(backSection, "back");
        inv.setItem(18, backItem);

        MenuManager.openedTypeMenus.put(staff.getUniqueId(), new MenuManager.TypeMenuData(target, reason));
        staff.openInventory(inv);
    }
}
