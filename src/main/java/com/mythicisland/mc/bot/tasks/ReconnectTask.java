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
package com.mythicisland.mc.bot.tasks;

import com.mythicisland.mc.bot.Main;
import com.mythicisland.mc.bot.core.BotState;
import com.mythicisland.mc.bot.core.MinecraftBot;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class ReconnectTask extends BukkitRunnable {

    private final Main plugin;

    @Override
    public void run() {
        MinecraftBot bot = plugin.getBot();

        if (!plugin.getConfigManager().shouldAutoReconnect()) {
            return;
        }

        if (bot.getState() == BotState.OFFLINE || bot.getState() == BotState.ERROR) {
            BotLogger.debug("Checking for automatic reconnect...");

            if (bot.getReconnectAttempts().get() < plugin.getConfigManager().getMaxReconnectAttempts()) {
                BotLogger.info("Starting automatic reconnect...");
                bot.connect();
            }
        }
    }
}
