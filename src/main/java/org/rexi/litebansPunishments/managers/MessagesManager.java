package org.rexi.litebansPunishments.managers;

import org.bukkit.ChatColor;
import org.rexi.litebansPunishments.LitebansPunishments;

public class MessagesManager {
    public static String get(String path) {
        String prefix = LitebansPunishments.getInstance().getMessages().getString("prefix", "");
        String msg = LitebansPunishments.getInstance().getMessages().getString(path, "&cMessage not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public static String getRaw(String path) {
        String msg = LitebansPunishments.getInstance().getMessages().getString(path, "&cMessage not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String get(String path, String placeholder, String replacement) {
        return get(path).replace(placeholder, replacement);
    }
    public static String getRaw(String path, String placeholder, String replacement) {
        return get(path).replace(placeholder, replacement);
    }
}
