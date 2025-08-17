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
import com.mythicisland.mc.bot.core.MinecraftBot;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionManager {

    private final Main plugin;
    private final ReconnectHandler reconnectHandler;

    public ConnectionManager(Main plugin) {
        this.plugin = plugin;
        this.reconnectHandler = new ReconnectHandler(plugin);
    }

    public void initialize() {
        BotLogger.info("ConnectionManager initialized");
    }

    public void handleConnectionLost(MinecraftBot bot, String reason) {
        BotLogger.warning("Connection lost: " + reason);

        if (plugin.getConfigManager().shouldAutoReconnect()) {
            reconnectHandler.scheduleReconnect(bot);
        }
    }

    public void shutdown() {
        reconnectHandler.cancelAllReconnects();
        BotLogger.info("ConnectionManager shut down");
    }
}
