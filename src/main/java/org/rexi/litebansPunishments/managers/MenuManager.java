package org.rexi.litebansPunishments.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.rexi.litebansPunishments.menus.PunishSubMenu;

import java.util.HashMap;
import java.util.UUID;

public class MenuManager {
    public static HashMap<UUID, String> openedMenus = new HashMap<>();

    public static void handleClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!openedMenus.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;
        if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String clickedName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        String target = openedMenus.get(player.getUniqueId());

        String historyName = ConfigManager.getConfig().getString("history.name", "&bHistory");
        String historyNameStripped = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', historyName));

        if (clickedName.equalsIgnoreCase(historyNameStripped) || clickedName.contains(historyNameStripped)) {
            player.closeInventory();
            Bukkit.dispatchCommand(player, "history " + target);
            return;
        }

        // Abre submenú de sanción
        PunishSubMenu.open(player, target, clickedName);
    }
}
