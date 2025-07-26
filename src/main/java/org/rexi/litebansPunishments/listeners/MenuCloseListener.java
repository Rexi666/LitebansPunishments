package org.rexi.litebansPunishments.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.rexi.litebansPunishments.managers.MenuManager;

import java.util.UUID;

public class MenuCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Quitar de todos los mapas si está en alguno
        MenuManager.openedMenus.remove(uuid);
        MenuManager.openedTypeMenus.remove(uuid);
        MenuManager.openedSubMenus.remove(uuid);
    }
}
