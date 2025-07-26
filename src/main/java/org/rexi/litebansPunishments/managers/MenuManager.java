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
import org.rexi.litebansPunishments.menus.PunishTypeMenu;

import java.util.HashMap;
import java.util.UUID;

public class MenuManager {
    public static HashMap<UUID, String> openedMenus = new HashMap<>();
    public static HashMap<UUID, SubMenuData> openedSubMenus = new HashMap<>();
    public static HashMap<UUID, TypeMenuData> openedTypeMenus = new HashMap<>();

    public static class SubMenuData {
        public final String target;
        public final String reason;
        public final String type;

        public SubMenuData(String target, String reason, String type) {
            this.target = target;
            this.reason = reason;
            this.type = type;
        }
    }

    public static class TypeMenuData {
        public final String target;
        public final String reason;

        public TypeMenuData(String target, String reason) {
            this.target = target;
            this.reason = reason;
        }
    }

    public static void handleClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;
        if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) return;

        ItemMeta meta = clickedItem.getItemMeta();
        String key = meta.getPersistentDataContainer().get(
                new NamespacedKey(LitebansPunishments.getInstance(), "punish_key"),
                PersistentDataType.STRING);

        if (openedTypeMenus.containsKey(uuid)) {
            // Menú de tipos (ban, mute, warn, kick)
            if (key == null) {
                player.sendMessage(ChatColor.RED + "No se pudo determinar el tipo de castigo.");
                player.closeInventory();
                openedTypeMenus.remove(uuid);
                return;
            }

            MenuManager.TypeMenuData data = openedTypeMenus.get(uuid);

            // Cerramos menú actual y abrimos submenú de tiempos
            player.closeInventory();
            openedTypeMenus.remove(uuid);

            // Abrimos submenú pasando target, reason, y el tipo seleccionado (key)
            PunishSubMenu.open(player, data.target, data.reason, key);

            return;
        }

        if (openedMenus.containsKey(uuid)) {
            // Menú principal (razones)
            if (key == null) {
                // Puede ser el botón "history"
                String historyName = ConfigManager.getConfig().getString("history.name", "&bHistory");
                String historyNameStripped = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', historyName));
                String clickedName = ChatColor.stripColor(meta.getDisplayName());

                if (clickedName.equalsIgnoreCase(historyNameStripped) || clickedName.contains(historyNameStripped)) {
                    String target = openedMenus.get(uuid);
                    player.closeInventory();
                    Bukkit.dispatchCommand(player, "history " + target);
                }
                return;
            }

            // Abrimos menú de tipos con la razón seleccionada
            String target = openedMenus.get(uuid);
            String reason = key;  // En menú principal guardamos la "reason" en la key

            player.closeInventory();
            openedMenus.remove(uuid);

            PunishTypeMenu.open(player, target, reason);
            return;
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
