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
package com.mythicisland.mc.bot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BotConstants {

    public static final String PLUGIN_NAME = "MinecraftBot";
    public static final String PLUGIN_PREFIX = "§8[§bBot§8] §7";

    public static final int DEFAULT_MINECRAFT_PORT = 25565;
    public static final String DEFAULT_SERVER_HOST = "localhost";

    public static final int MIN_RECONNECT_DELAY = 5;
    public static final int MAX_RECONNECT_DELAY = 300;
    public static final int MIN_KEEP_ALIVE_INTERVAL = 10;
    public static final int MAX_KEEP_ALIVE_INTERVAL = 60;

    public static final int CONNECTION_TIMEOUT_MS = 10000;
    public static final int READ_TIMEOUT_MS = 30000;

    public static final String ACCOUNT_TYPE_MICROSOFT = "microsoft";
    public static final String ACCOUNT_TYPE_LEGACY = "legacy";

    public static final String DEFAULT_LANGUAGE = "en-us";
    public static final String FALLBACK_LANGUAGE = "en-us";

    public static final String CONFIG_FILE = "config.yml";
    public static final String LANG_DIRECTORY = "lang";

    public static final String PERMISSION_USE = "minecraftbot.use";
    public static final String PERMISSION_ADMIN = "minecraftbot.admin";
    public static final String PERMISSION_RELOAD = "minecraftbot.reload";
}