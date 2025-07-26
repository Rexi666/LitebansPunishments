package org.rexi.litebansPunishments.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.rexi.litebansPunishments.managers.MessagesManager;

public class PunishSubMenu {
    public static void open(Player staff, String target, String reason) {
        Inventory inv = Bukkit.createInventory(null, 27, MessagesManager.getRaw("menu.submenu-title", "%target%", target, "%reason%", reason));

        // Aquí deberías leer de la config los tipos (ban, mute, warn, kick)
        // y añadir ítems representativos para abrir tiempos o aplicar sanción

        staff.openInventory(inv);
    }
}