package org.rexi.litebansPunishments;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.litebansPunishments.commands.PunishCommand;
import org.rexi.litebansPunishments.commands.ReloadCommand;
import org.rexi.litebansPunishments.listeners.MenuCloseListener;
import org.rexi.litebansPunishments.listeners.MenuListener;

import java.io.File;

public final class LitebansPunishments extends JavaPlugin {
    private static LitebansPunishments instance;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadMessages();
        getCommand("punish").setExecutor(new PunishCommand());
        getCommand("punishreload").setExecutor(new ReloadCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new MenuCloseListener(), this);
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&aLitebansPunishments has been enabled!"));
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bThank you for using Rexi666 plugins!"));
    }


    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cLitebansPunishments has been disabled!"));
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bThank you for using Rexi666 plugins!"));
    }

    public static LitebansPunishments getInstance() {
        return instance;
    }

    private void loadMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessages() {
        return messagesConfig;
    }

    public void reloadMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
}
