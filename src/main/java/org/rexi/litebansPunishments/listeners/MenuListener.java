package org.rexi.litebansPunishments.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.rexi.litebansPunishments.managers.MenuManager;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        // Aquí se manejarán los clics en los menús
        MenuManager.handleClick(e);
    }
}
