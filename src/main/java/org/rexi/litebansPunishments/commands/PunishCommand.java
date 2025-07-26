package org.rexi.litebansPunishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rexi.litebansPunishments.managers.MessagesManager;
import org.rexi.litebansPunishments.menus.PunishMenu;

public class PunishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesManager.get("errors.only-players"));
            return true;
        }

        if (!sender.hasPermission("litebanspunishments.punish")) {
            sender.sendMessage(MessagesManager.get("errors.no-permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(MessagesManager.get("errors.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessagesManager.get("errors.player-not-found"));
            return true;
        }

        PunishMenu.openMainMenu((Player) sender, target.getName());
        return true;
    }
}
