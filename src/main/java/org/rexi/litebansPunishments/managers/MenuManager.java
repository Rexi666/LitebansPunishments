package org.rexi.litebansPunishments.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.rexi.litebansPunishments.LitebansPunishments;
import org.rexi.litebansPunishments.menus.PunishMenu;
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
            String clickedName = ChatColor.stripColor(meta.getDisplayName());
            String backName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
                    ConfigManager.getConfig().getString("back.name", "Back")));

            if (clickedName.equalsIgnoreCase(backName)) {
                // Volver al menú anterior (razones)
                TypeMenuData data = openedTypeMenus.remove(uuid);
                MenuManager.openedMenus.put(uuid, data.target);
                player.closeInventory();
                Bukkit.getScheduler().runTask(LitebansPunishments.getInstance(), () -> {
                    // Reabrir el menú principal después de un tick
                    // Asegúrate de tener tu método PunishMenu.open
                    PunishMenu.openMainMenu(player, data.target);
                });
                return;
            }

            if (key == null) {
                player.sendMessage(MessagesManager.get("errors.novalidpunishment"));
                player.closeInventory();
                openedTypeMenus.remove(uuid);
                return;
            }

            MenuManager.TypeMenuData data = openedTypeMenus.get(uuid);

            // Obtenemos la sección de tiempos del config para este tipo
            ConfigurationSection typeSection = ConfigManager.getConfig()
                    .getConfigurationSection("punishments." + data.reason + ".actions." + key);

            if (typeSection == null) {
                player.sendMessage(MessagesManager.get("errors.nopunishmentsection-player"));
                player.closeInventory();
                openedTypeMenus.remove(uuid);
                return;
            }

            ConfigurationSection timesSection = typeSection.getConfigurationSection("times");

            if (timesSection == null || timesSection.getKeys(false).isEmpty()) {
                // No hay tiempos, ejecutamos comando directo y cerramos menú
                String reason_punish = typeSection.getString("reason", key);
                reason_punish = ChatColor.translateAlternateColorCodes('&', reason_punish);

                boolean ip = typeSection.getBoolean("ip", false);

                String comando;
                switch (key.toLowerCase()) {
                    case "ban":
                        if (ip) {
                            comando = "ipban " + data.target + " " + reason_punish;
                        } else {
                            comando = "ban " + data.target + " " + reason_punish;
                        }
                        break;
                    case "mute":
                        if (ip) {
                            comando = "ipmute " + data.target + " " + reason_punish;
                        } else {
                            comando = "mute " + data.target + " " + reason_punish;
                        }
                        break;
                    case "warn":
                        comando = "warn " + data.target + " " + reason_punish;
                        break;
                    case "kick":
                        comando = "kick " + data.target + " " + reason_punish;
                        break;
                    default:
                        player.sendMessage(MessagesManager.get("errors.unknown-punishment", "%type%", key));
                        player.closeInventory();
                        openedTypeMenus.remove(uuid);
                        return;
                }

                player.closeInventory();
                openedTypeMenus.remove(uuid);
                Bukkit.dispatchCommand(player, comando);
                return;
            }

            // Si hay tiempos, abrimos submenú
            player.closeInventory();
            openedTypeMenus.remove(uuid);
            PunishSubMenu.open(player, data.target, data.reason, key);
            return;
        }

        if (openedMenus.containsKey(uuid)) {
            // Menú principal (razones)
            if (key == null) {
                // Puede ser el botón "history"
                String historyName = ConfigManager.getConfig().getString("history.display", "&bHistory");
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

        String clickedName = ChatColor.stripColor(meta.getDisplayName());
        String backName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
                ConfigManager.getConfig().getString("back.name", "Back")));

        if (clickedName.equalsIgnoreCase(backName)) {
            SubMenuData data = openedSubMenus.remove(player.getUniqueId());
            player.closeInventory();
            Bukkit.getScheduler().runTask(LitebansPunishments.getInstance(), () -> {
                PunishTypeMenu.open(player, data.target, data.reason);
            });
            return;
        }

        String key = meta.getPersistentDataContainer().get(
                new NamespacedKey(LitebansPunishments.getInstance(), "punish_key"),
                PersistentDataType.STRING);

        String time = meta.getPersistentDataContainer().get(
                new NamespacedKey(LitebansPunishments.getInstance(), "punish_time"),
                PersistentDataType.STRING);

        if (key == null) {
            player.sendMessage(MessagesManager.get("errors.novalidpunishment"));
            player.closeInventory();
            openedSubMenus.remove(player.getUniqueId());
            return;
        }

        if (time == null) {
            // Si no hay tiempo definido, puedes poner un valor por defecto o avisar
            time = "";
        }

        SubMenuData data = openedSubMenus.get(player.getUniqueId());

        ConfigurationSection config = ConfigManager.getConfig();

        String configReason = null;

        ConfigurationSection timeSection = config.getConfigurationSection(
                "punishments." + data.reason + ".actions." + key + ".times." + time);

        if (timeSection != null) {
            configReason = timeSection.getString("reason");
        }

        if (configReason == null) {
            // Intentamos la razón general sin tiempos
            configReason = config.getString("punishments." + data.reason + ".actions." + key + ".reason");
        }

        if (configReason == null) {
            // Por si acaso no está, ponemos la key (tipo) como razón fallback
            configReason = key;
        }

        boolean ip = timeSection != null && timeSection.getBoolean("ip", false);

        player.closeInventory();
        openedSubMenus.remove(player.getUniqueId());

        // Construimos el comando a ejecutar
        String comando;

        switch (key.toLowerCase()) {
            case "ban":
                if (ip) {
                    if (time.equalsIgnoreCase("permanent") || time.isEmpty()) {
                        comando = "ipban " + data.target + " " + configReason;
                    } else {
                        comando = "ipban " + data.target + " " + time + " " + configReason;
                    }
                } else {
                    if (time.equalsIgnoreCase("permanent") || time.isEmpty()) {
                        comando = "ban " + data.target + " " + configReason;
                    } else {
                        comando = "ban " + data.target + " " + time + " " + configReason;
                    }
                }
                break;
            case "mute":
                if (ip) {
                    if (time.equalsIgnoreCase("permanent") || time.isEmpty()) {
                        comando = "ipmute " + data.target + " " + configReason;
                    } else {
                        comando = "ipmute " + data.target + " " + time + " " + configReason;
                    }
                } else {
                    if (time.equalsIgnoreCase("permanent") || time.isEmpty()) {
                        comando = "mute " + data.target + " " + configReason;
                    } else {
                        comando = "mute " + data.target + " " + time + " " + configReason;
                    }
                }
                break;
            case "warn":
                comando = "warn " + data.target + " " + configReason;
                break;
            case "kick":
                comando = "kick " + data.target + " " + configReason;
                break;
            default:
                player.sendMessage(MessagesManager.get("errors.unknown-punishment", "%type%", key));
                return;
        }

        Bukkit.dispatchCommand(player, comando);
    }
}
