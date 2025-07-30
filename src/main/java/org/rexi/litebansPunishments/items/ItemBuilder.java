package org.rexi.litebansPunishments.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.rexi.litebansPunishments.LitebansPunishments;
import org.rexi.litebansPunishments.managers.MessagesManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {
    public static ItemStack fromConfig(org.bukkit.configuration.file.FileConfiguration config, String path, String name) {
        ConfigurationSection section = config.getConfigurationSection("punishments." + name);
        if (section == null) {
            return new ItemStack(Material.STONE); // Default item if section is not found
        }
        String itemString = section.getString("item", "STONE");
        ItemStack item;
        if (itemString.startsWith("basehead-")) {
            String base64 = itemString.substring("basehead-".length());
            item = getCustomHead(base64);
        } else {
            Material mat;
            try {
                mat = Material.valueOf(itemString.toUpperCase());
            } catch (IllegalArgumentException e) {
                mat = Material.STONE;
            }
            item = new ItemStack(mat);
        }

        String display = section.getString("name", name);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display.replace("&", "§"));

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_DYE
        );

        meta.lore(
                List.of(
                        Component.text(" "),
                        Component.text(MessagesManager.getRaw("menu.clickToSelect"))
                )
        );
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack simpleFromConfig(ConfigurationSection section, String name, String punishKey, String punishTime) {
        if (section == null) return new ItemStack(Material.STONE);

        String itemString = section.getString("item", "STONE");
        ItemStack item;
        if (itemString.startsWith("basehead-")) {
            String base64 = itemString.substring("basehead-".length());
            item = getCustomHead(base64);
        } else {
            Material mat;
            try {
                mat = Material.valueOf(itemString.toUpperCase());
            } catch (IllegalArgumentException e) {
                mat = Material.STONE;
            }
            item = new ItemStack(mat);
        }

        String display = section.getString("display", name);
        display = ChatColor.translateAlternateColorCodes('&', display);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_DYE
        );


        String reason = section.getString("reason");
        if (reason != null) {
            meta.lore(
                    List.of(
                            Component.text(" "),
                            Component.text(MessagesManager.getRaw("menu.reason", "%reason%", reason)),
                            Component.text(" "),
                            Component.text(MessagesManager.getRaw("menu.clickToSelect"))
                    )
            );
        } else {
            meta.lore(
                    List.of(
                            Component.text(" "),
                            Component.text(MessagesManager.getRaw("menu.clickToSelect"))
                    )
            );
        }

        NamespacedKey key = new NamespacedKey(LitebansPunishments.getInstance(), "punish_key");
        NamespacedKey timeKey = new NamespacedKey(LitebansPunishments.getInstance(), "punish_time");

        if (punishKey != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, punishKey);
        }
        if (punishTime != null) {
            meta.getPersistentDataContainer().set(timeKey, PersistentDataType.STRING, punishTime);
        }

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack setDisplayName(ItemStack item, String displayName) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack fromSimpleSection(ConfigurationSection section, String keyName) {
        if (section == null) return new ItemStack(Material.STONE);

        String itemString = section.getString("item", "STONE");
        ItemStack item;
        if (itemString.startsWith("basehead-")) {
            String base64 = itemString.substring("basehead-".length());
            item = getCustomHead(base64);
        } else {
            Material mat;
            try {
                mat = Material.valueOf(itemString.toUpperCase());
            } catch (IllegalArgumentException e) {
                mat = Material.STONE;
            }
            item = new ItemStack(mat);
        }

        String name = ChatColor.translateAlternateColorCodes('&', section.getString("name", keyName));

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        meta.lore(
                List.of(
                        Component.text(" "),
                        Component.text(MessagesManager.getRaw("menu.clickToSelect"))
                )
        );

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_DYE
        );

        meta.getPersistentDataContainer().set(
                new NamespacedKey(LitebansPunishments.getInstance(), "punish_key"),
                PersistentDataType.STRING,
                keyName
        );

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getCustomHead(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), "CustomHead");
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        head.setItemMeta(skullMeta);
        return head;
    }

}
