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
public class MessageKeys {

    public static final String BOT_CONNECTING = "bot.connecting";
    public static final String BOT_CONNECTED = "bot.connected";
    public static final String BOT_DISCONNECTED = "bot.disconnected";
    public static final String BOT_ALREADY_CONNECTED = "bot.already_connected";
    public static final String BOT_NOT_CONNECTED = "bot.not_connected";
    public static final String BOT_RECONNECTING = "bot.reconnecting";

    public static final String BOT_STATUS_ONLINE = "bot.status.online";
    public static final String BOT_STATUS_OFFLINE = "bot.status.offline";
    public static final String BOT_STATUS_CONNECTING = "bot.status.connecting";
    public static final String BOT_STATUS_ERROR = "bot.status.error";

    public static final String BOT_ERROR_AUTH_FAILED = "bot.errors.auth_failed";
    public static final String BOT_ERROR_CONNECTION_FAILED = "bot.errors.connection_failed";
    public static final String BOT_ERROR_CONFIG_INVALID = "bot.errors.config_invalid";
    public static final String BOT_ERROR_SERVER_NOT_FOUND = "bot.errors.server_not_found";

    public static final String COMMAND_BOT_DESCRIPTION = "commands.bot.description";
    public static final String COMMAND_BOT_USAGE = "commands.bot.usage";
    public static final String COMMAND_BOT_UNKNOWN_SUBCOMMAND = "commands.bot.unknown_subcommand";
    public static final String COMMAND_BOT_NO_PERMISSION = "commands.bot.no_permission";

    public static final String CONFIG_RELOADED = "config.reloaded";
    public static final String CONFIG_RELOAD_FAILED = "config.reload_failed";

    public static final String DISPLAY_STATUS_HEADER = "display.status_header";
    public static final String DISPLAY_STATUS_LINE = "display.status_line";
    public static final String DISPLAY_SERVER_LINE = "display.server_line";
    public static final String DISPLAY_ACCOUNT_LINE = "display.account_line";
    public static final String DISPLAY_UPTIME_LINE = "display.uptime_line";
    public static final String DISPLAY_LAST_ERROR_LINE = "display.last_error_line";
}