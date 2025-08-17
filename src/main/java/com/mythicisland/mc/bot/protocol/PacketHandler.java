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
package com.mythicisland.mc.bot.protocol;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.login.clientbound.ClientboundGameProfilePacket;
import com.github.steveice10.packetlib.packet.Packet;
import com.mythicisland.mc.bot.core.MinecraftBot;
import com.mythicisland.mc.bot.protocol.handlers.ChatHandler;
import com.mythicisland.mc.bot.protocol.handlers.KeepAliveHandler;
import com.mythicisland.mc.bot.protocol.handlers.LoginHandler;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketHandler {

    private final MinecraftBot bot;
    private final LoginHandler loginHandler;
    private final KeepAliveHandler keepAliveHandler;
    private final ChatHandler chatHandler;

    public PacketHandler(MinecraftBot bot) {
        this.bot = bot;
        this.loginHandler = new LoginHandler(bot);
        this.keepAliveHandler = new KeepAliveHandler(bot);
        this.chatHandler = new ChatHandler(bot);
    }

    public void handleIncomingPacket(Packet packet) {
        BotLogger.packet("Incoming: " + packet.getClass().getSimpleName());

        if (packet instanceof ClientboundGameProfilePacket) {
            loginHandler.handleLoginSuccess((ClientboundGameProfilePacket) packet);

        } else if (packet instanceof ClientboundKeepAlivePacket) {
            keepAliveHandler.handleKeepAlive((ClientboundKeepAlivePacket) packet);

        } else if (packet instanceof ClientboundSystemChatPacket) {
            chatHandler.handleSystemChat((ClientboundSystemChatPacket) packet);

        } else if (packet instanceof ClientboundPlayerPositionPacket) {
            handlePlayerPosition((ClientboundPlayerPositionPacket) packet);

        } else {
            BotLogger.debug("Unhandled packet: " + packet.getClass().getSimpleName());
        }
    }

    private void handlePlayerPosition(ClientboundPlayerPositionPacket packet) {
        bot.getBotSession().updatePosition(
                packet.getX(),
                packet.getY(),
                packet.getZ(),
                packet.getYaw(),
                packet.getPitch(),
                false
        );

        BotLogger.debug("Position updated: " +
                String.format("%.2f, %.2f, %.2f", packet.getX(), packet.getY(), packet.getZ()));
    }
}
