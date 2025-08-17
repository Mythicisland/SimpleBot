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
package com.mythicisland.mc.bot.utils;

import com.mythicisland.mc.bot.Main;
import com.mythicisland.mc.bot.constants.BotConstants;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class BotLogger {

    private static Logger logger;
    private static boolean debugMode = false;

    public static void initialize(Main plugin) {
        logger = plugin.getLogger();
        debugMode = plugin.getConfigManager().isDebugMode();
    }

    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    public static void info(String message) {
        if (logger != null) {
            logger.info(message);
        } else {
            System.out.println("[INFO] " + message);
        }
    }

    public static void warning(String message) {
        if (logger != null) {
            logger.warning(message);
        } else {
            System.out.println("[WARNING] " + message);
        }
    }

    public static void error(String message) {
        if (logger != null) {
            logger.severe(message);
        } else {
            System.err.println("[ERROR] " + message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if (logger != null) {
            logger.log(Level.SEVERE, message, throwable);
        } else {
            System.err.println("[ERROR] " + message);
            throwable.printStackTrace();
        }
    }

    public static void debug(String message) {
        if (debugMode) {
            if (logger != null) {
                logger.info("[DEBUG] " + message);
            } else {
                System.out.println("[DEBUG] " + message);
            }
        }
    }

    public static void packet(String message) {
        if (debugMode && Main.getInstance() != null &&
                Main.getInstance().getConfigManager().shouldLogPackets()) {
            if (logger != null) {
                logger.info("[PACKET] " + message);
            } else {
                System.out.println("[PACKET] " + message);
            }
        }
    }

    public static void broadcast(String message) {
        String formatted = BotConstants.PLUGIN_PREFIX + message;
        if (Bukkit.getServer() != null) {
            Bukkit.getServer().getConsoleSender().sendMessage(formatted);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission(BotConstants.PERMISSION_ADMIN)) {
                    player.sendMessage(formatted);
                }
            });
        }
    }
}