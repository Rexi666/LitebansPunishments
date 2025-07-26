package org.rexi.litebansPunishments.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.rexi.litebansPunishments.LitebansPunishments;
import org.rexi.litebansPunishments.menus.PunishSubMenu;

import java.util.HashMap;
import java.util.UUID;

public class MenuManager {
    public static HashMap<UUID, String> openedMenus = new HashMap<>();
    public static HashMap<UUID, SubMenuData> openedSubMenus = new HashMap<>();

    public static void handleClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!openedMenus.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;
        if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) return;

        String target = openedMenus.get(player.getUniqueId());

        ItemMeta meta = clickedItem.getItemMeta();
        String key = meta.getPersistentDataContainer().get(
                new NamespacedKey(LitebansPunishments.getInstance(), "punish_key"),
                PersistentDataType.STRING);

        if (key == null) {
            // Si no tiene la key, puede ser el item "history"
            String historyName = ConfigManager.getConfig().getString("history.name", "&bHistory");
            String historyNameStripped = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', historyName));
            String clickedName = ChatColor.stripColor(meta.getDisplayName());

            if (clickedName.equalsIgnoreCase(historyNameStripped) || clickedName.contains(historyNameStripped)) {
                player.closeInventory();
                Bukkit.dispatchCommand(player, "history " + target);
            }

            return;
        }

        // Abre submenú con la key correcta
        PunishSubMenu.open(player, target, key);
    }

    public static class SubMenuData {
        public final String target;
        public final String reason;

        public SubMenuData(String target, String reason) {
            this.target = target;
            this.reason = reason;
        }
    }

    public static void handleSubMenuClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!openedSubMenus.containsKey(player.getUniqueId())) return;

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;
        if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) return;

        ItemMeta meta = clickedItem.getItemMeta();

        String key = meta.getPersistentDataContainer().get(
                new NamespacedKey(LitebansPunishments.getInstance(), "punish_key"),
                PersistentDataType.STRING);

        String time = meta.getPersistentDataContainer().get(
                new NamespacedKey(LitebansPunishments.getInstance(), "punish_time"),
                PersistentDataType.STRING);

        if (key == null) {
            player.sendMessage(ChatColor.RED + "Error: no se pudo determinar el tipo de castigo.");
            player.closeInventory();
            openedSubMenus.remove(player.getUniqueId());
            return;
        }

        if (time == null) {
            // Si no hay tiempo definido, puedes poner un valor por defecto o avisar
            time = "";
        }

        SubMenuData data = openedSubMenus.get(player.getUniqueId());

        player.closeInventory();
        openedSubMenus.remove(player.getUniqueId());

        // Construimos el comando a ejecutar
        String comando;

        switch (key.toLowerCase()) {
            case "ban":
                if (time.equalsIgnoreCase("permanent") || time.isEmpty()) {
                    comando = "ban " + data.target + " " + meta.getDisplayName();
                } else {
                    comando = "ban " + data.target + " " + time + " " + meta.getDisplayName();
                }
                break;
            case "mute":
                if (time.equalsIgnoreCase("permanent") || time.isEmpty()) {
                    comando = "mute " + data.target + " " + meta.getDisplayName();
                } else {
                    comando = "mute " + data.target + " " + time + " " + meta.getDisplayName();
                }
                break;
            case "warn":
                comando = "warn " + data.target + " " + meta.getDisplayName();
                break;
            case "kick":
                comando = "kick " + data.target + " " + meta.getDisplayName();
                break;
            default:
                player.sendMessage(ChatColor.RED + "Tipo de castigo desconocido: " + key);
                return;
        }

        Bukkit.dispatchCommand(player, comando);
    }
}
