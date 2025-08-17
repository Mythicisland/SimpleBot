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
package com.mythicisland.mc.bot.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotState {

    OFFLINE("Offline", false),
    CONNECTING("Connecting", false),
    ONLINE("Online", true),
    ERROR("Error", false),
    RECONNECTING("Reconnecting", false);

    private final String displayName;
    private final boolean connected;

    public boolean canConnect() {
        return this == OFFLINE || this == ERROR;
    }

    public boolean canDisconnect() {
        return this == ONLINE || this == CONNECTING;
    }

    public boolean isActive() {
        return this == ONLINE || this == CONNECTING || this == RECONNECTING;
    }
}