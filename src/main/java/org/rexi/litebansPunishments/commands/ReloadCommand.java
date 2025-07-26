package org.rexi.litebansPunishments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.rexi.litebansPunishments.LitebansPunishments;
import org.rexi.litebansPunishments.managers.MessagesManager;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("litebanspunishments.reload")) {
            sender.sendMessage(MessagesManager.getRaw("errors.no-permission"));
            return true;
        }

        LitebansPunishments.getInstance().reloadConfig();
        LitebansPunishments.getInstance().reloadMessages();

        sender.sendMessage(MessagesManager.getRaw("reload.success"));
        return true;
    }
}
