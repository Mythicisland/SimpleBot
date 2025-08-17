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

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BotSession {

    private String serverHost;
    private int serverPort;
    private LocalDateTime connectTime;
    private LocalDateTime lastActivity;

    private GameProfile gameProfile;
    private UUID sessionId;
    private String accessToken;
    private String refreshToken;

    private int entityId;
    private GameMode gameMode;
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    private int health = 20;
    private int food = 20;
    private float experience = 0.0f;

    private long packetsReceived = 0;
    private long packetsSent = 0;
    private long lastKeepAlive = 0;

    private String lastDisconnectReason;
    private int reconnectAttempts = 0;

    public BotSession() {
        this.connectTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.sessionId = UUID.randomUUID();
    }

    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    public void incrementPacketsReceived() {
        this.packetsReceived++;
        updateActivity();
    }

    public void incrementPacketsSent() {
        this.packetsSent++;
        updateActivity();
    }

    public void updatePosition(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        updateActivity();
    }

    public void updateHealth(int health, int food, float experience) {
        this.health = health;
        this.food = food;
        this.experience = experience;
        updateActivity();
    }

    public void reset() {
        this.connectTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.entityId = 0;
        this.gameMode = null;
        this.x = this.y = this.z = 0;
        this.yaw = this.pitch = 0;
        this.onGround = false;
        this.health = 20;
        this.food = 20;
        this.experience = 0.0f;
        this.packetsReceived = 0;
        this.packetsSent = 0;
        this.lastKeepAlive = 0;
    }

    public String getUptimeString() {
        if (connectTime == null) {
            return "0s";
        }

        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(connectTime, now).getSeconds();

        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "h " + minutes + "m";
        }
    }

    public String getAccountDisplay() {
        if (gameProfile != null && gameProfile.getName() != null) {
            return gameProfile.getName();
        }
        return "Unknown";
    }
}