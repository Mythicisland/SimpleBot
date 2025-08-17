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
package com.mythicisland.mc.bot;

import com.mythicisland.mc.bot.commands.BotCommand;
import com.mythicisland.mc.bot.commands.BotTabCompleter;
import com.mythicisland.mc.bot.config.ConfigManager;
import com.mythicisland.mc.bot.config.LanguageManager;
import com.mythicisland.mc.bot.connection.ConnectionManager;
import com.mythicisland.mc.bot.core.MinecraftBot;
import com.mythicisland.mc.bot.tasks.KeepAliveTask;
import com.mythicisland.mc.bot.tasks.ReconnectTask;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    private MinecraftBot bot;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private ConnectionManager connectionManager;
    private BukkitTask keepAliveTask;
    private BukkitTask reconnectTask;

    @Override
    public void onEnable() {
        instance = this;

        try {
            initializePlugin();
            startTasks();
            autoConnectIfEnabled();

            getLogger().info("Minecraft Bot Plugin successfully enabled!");

        } catch (Exception e) {
            getLogger().severe("Error while enabling the plugin: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            shutdownPlugin();
            getLogger().info("Minecraft Bot Plugin disabled!");

        } catch (Exception e) {
            getLogger().severe("Error while disabling the plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializePlugin() {
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        BotLogger.initialize(this);

        bot = new MinecraftBot(this);
        connectionManager = new ConnectionManager(this);
        connectionManager.initialize();

        registerCommands();

        BotLogger.info("Plugin components initialized");
    }

    private void registerCommands() {
        BotCommand botCommand = new BotCommand(this);
        BotTabCompleter tabCompleter = new BotTabCompleter();

        getCommand("bot").setExecutor(botCommand);
        getCommand("bot").setTabCompleter(tabCompleter);

        BotLogger.debug("Commands registered");
    }

    private void startTasks() {
        int keepAliveInterval = configManager.getKeepAliveInterval();
        keepAliveTask = new KeepAliveTask(this).runTaskTimer(this, 0L, keepAliveInterval * 20L);

        int reconnectCheckInterval = 60;
        reconnectTask = new ReconnectTask(this).runTaskTimer(this, 0L, reconnectCheckInterval * 20L);

        BotLogger.debug("Tasks started (KeepAlive: " + keepAliveInterval + "s, Reconnect-Check: " + reconnectCheckInterval + "s)");
    }

    private void autoConnectIfEnabled() {
        if (configManager.shouldAutoConnectOnStartup()) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                BotLogger.info("Starting automatic connection...");
                bot.connect();
            }, 60L);
        }
    }

    private void shutdownPlugin() {
        if (keepAliveTask != null && !keepAliveTask.isCancelled()) {
            keepAliveTask.cancel();
        }

        if (reconnectTask != null && !reconnectTask.isCancelled()) {
            reconnectTask.cancel();
        }

        if (connectionManager != null) {
            connectionManager.shutdown();
        }

        if (bot != null && bot.isConnected()) {
            bot.disconnect("Plugin is being disabled");
        }
    }
}