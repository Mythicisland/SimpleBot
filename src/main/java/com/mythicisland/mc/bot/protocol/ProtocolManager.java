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

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.mythicisland.mc.bot.core.MinecraftBot;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProtocolManager {

    private final MinecraftBot bot;
    private final PacketHandler packetHandler;

    public ProtocolManager(MinecraftBot bot) {
        this.bot = bot;
        this.packetHandler = new PacketHandler(bot);
    }

    public void setupSessionListeners(Session session) {
        session.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(Session session, Packet packet) {
                try {
                    packetHandler.handleIncomingPacket(packet);
                    bot.getBotSession().incrementPacketsReceived();
                } catch (Exception e) {
                    BotLogger.error("Error while processing incoming packets: " + e.getMessage());
                }
            }

            public void disconnected(Session session, String reason) {
                String disconnectReason = reason != null ? reason : "Unknown reason";
                BotLogger.warning("Session disconnected: " + disconnectReason);
                bot.onSessionDisconnected(disconnectReason);
            }
        });

        BotLogger.debug("Session listener registered");
    }
}
