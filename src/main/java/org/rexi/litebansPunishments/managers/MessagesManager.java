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

    public static String get(String path, String... replacements) {
        String prefix = LitebansPunishments.getInstance().getMessages().getString("prefix", "");
        String msg = LitebansPunishments.getInstance().getMessages().getString(path, "&cMessage not found: " + path);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public static String getRaw(String path, String... replacements) {
        String msg = LitebansPunishments.getInstance().getMessages().getString(path, "&cMessage not found: " + path);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
