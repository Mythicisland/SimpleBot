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
package com.mythicisland.mc.bot.connection;

import com.mythicisland.mc.bot.Main;
import com.mythicisland.mc.bot.core.BotState;
import com.mythicisland.mc.bot.core.MinecraftBot;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class ReconnectHandler {

    private final Main plugin;
    private BukkitTask reconnectTask;

    public void scheduleReconnect(MinecraftBot bot) {
        if (reconnectTask != null && !reconnectTask.isCancelled()) {
            reconnectTask.cancel();
        }

        int delay = plugin.getConfigManager().getReconnectDelaySeconds();
        int maxAttempts = plugin.getConfigManager().getMaxReconnectAttempts();
        int currentAttempts = bot.getReconnectAttempts().get();

        if (currentAttempts >= maxAttempts) {
            BotLogger.error("Maximum number of reconnect attempts reached");
            return;
        }

        BotLogger.info("Scheduling reconnect in " + delay + " seconds");
        bot.setState(BotState.RECONNECTING);

        reconnectTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (bot.getState() == BotState.RECONNECTING) {
                BotLogger.info("Executing automatic reconnect...");
                bot.connect();
            }
        }, delay * 20L);
    }

    public void cancelAllReconnects() {
        if (reconnectTask != null && !reconnectTask.isCancelled()) {
            reconnectTask.cancel();
            BotLogger.debug("Reconnect task cancelled");
        }
    }
}
