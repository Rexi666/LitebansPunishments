package org.rexi.litebansPunishments.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.rexi.litebansPunishments.managers.MenuManager;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();

        if (MenuManager.openedMenus.containsKey(player.getUniqueId())) {
            MenuManager.handleClick(e);
        } else if (MenuManager.openedSubMenus.containsKey(player.getUniqueId())) {
            MenuManager.handleSubMenuClick(e);
        }
    }
}
