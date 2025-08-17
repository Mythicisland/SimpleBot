/*
 * This file is part of Simple Minecraft Bot, licensed under the MIT License.
 *
 * Copyright (c) 2025 Mythic Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.mythicisland.mc.bot.commands;

import com.mythicisland.mc.bot.Main;
import com.mythicisland.mc.bot.constants.BotConstants;
import com.mythicisland.mc.bot.constants.MessageKeys;
import com.mythicisland.mc.bot.core.BotState;
import com.mythicisland.mc.bot.core.MinecraftBot;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BotCommand implements CommandExecutor {

    private final Main plugin;

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!sender.hasPermission(BotConstants.PERMISSION_USE)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.COMMAND_BOT_NO_PERMISSION));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.COMMAND_BOT_USAGE));
            return true;
        }

        MinecraftBot bot = plugin.getBot();
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "connect":
                handleConnect(sender, bot);
                break;

            case "disconnect":
                handleDisconnect(sender, bot);
                break;

            case "status":
                handleStatus(sender, bot);
                break;

            case "reconnect":
                handleReconnect(sender, bot);
                break;

            case "reload":
                handleReload(sender);
                break;

            default:
                sender.sendMessage(plugin.getLanguageManager().getMessage(
                        MessageKeys.COMMAND_BOT_UNKNOWN_SUBCOMMAND, subCommand));
                return false;
        }

        return true;
    }

    private void handleConnect(CommandSender sender, MinecraftBot bot) {
        if (bot.isConnected()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.BOT_ALREADY_CONNECTED));
        } else {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.BOT_CONNECTING));
            bot.connect().thenRun(() -> {
                if (bot.isConnected()) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.BOT_CONNECTED));
                }
            });
        }
    }

    private void handleDisconnect(CommandSender sender, MinecraftBot bot) {
        if (!bot.isConnected()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.BOT_NOT_CONNECTED));
        } else {
            bot.disconnect("Manuell getrennt");
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.BOT_DISCONNECTED, "Manuell getrennt"));
        }
    }

    private void handleStatus(CommandSender sender, MinecraftBot bot) {
        BotState state = bot.getState();
        String serverInfo = bot.getServerInfo();
        String accountInfo = bot.getAccountInfo();
        String uptime = bot.getUptimeString();

        String statusColor = getStatusColor(state);
        String statusText = plugin.getLanguageManager().getMessage("bot.status." + state.name().toLowerCase());

        sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.DISPLAY_STATUS_HEADER));
        sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.DISPLAY_STATUS_LINE, statusColor + statusText));
        sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.DISPLAY_SERVER_LINE, serverInfo));
        sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.DISPLAY_ACCOUNT_LINE, accountInfo));

        if (bot.isConnected()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.DISPLAY_UPTIME_LINE, uptime));
        }

        if (bot.getLastDisconnectReason() != null) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(
                    MessageKeys.DISPLAY_LAST_ERROR_LINE, bot.getLastDisconnectReason()));
        }
    }

    private void handleReconnect(CommandSender sender, MinecraftBot bot) {
        sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.BOT_RECONNECTING));

        if (bot.isConnected()) {
            bot.disconnect("");
        }

        bot.connect().thenRun(() -> {
            if (bot.isConnected()) {
                sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.BOT_CONNECTED));
            }
        });
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission(BotConstants.PERMISSION_RELOAD)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.COMMAND_BOT_NO_PERMISSION));
            return;
        }

        try {
            plugin.getConfigManager().reload();
            plugin.getLanguageManager().reloadLanguages();
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.CONFIG_RELOADED));
        } catch (Exception e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage(MessageKeys.CONFIG_RELOAD_FAILED, e.getMessage()));
        }
    }

    private String getStatusColor(BotState state) {
        switch (state) {
            case ONLINE: return "§a";
            case CONNECTING:
            case RECONNECTING: return "§e";
            case OFFLINE: return "§7";
            case ERROR: return "§c";
            default: return "§f";
        }
    }
}