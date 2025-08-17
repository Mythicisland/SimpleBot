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

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.mythicisland.mc.bot.Main;
import com.mythicisland.mc.bot.auth.AuthManager;
import com.mythicisland.mc.bot.auth.SessionManager;
import com.mythicisland.mc.bot.constants.BotConstants;
import com.mythicisland.mc.bot.exceptions.AuthenticationException;
import com.mythicisland.mc.bot.exceptions.ConnectionException;
import com.mythicisland.mc.bot.protocol.ProtocolManager;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class MinecraftBot {

    private final Main plugin;

    @Setter
    private BotState state = BotState.OFFLINE;
    private Session session;
    private BotSession botSession;
    private SessionManager sessionManager;
    private ProtocolManager protocolManager;

    private String serverHost;
    private int serverPort;
    private String lastDisconnectReason;
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);

    public MinecraftBot(Main plugin) {
        this.plugin = plugin;
        this.botSession = new BotSession();
        this.protocolManager = new ProtocolManager(this);
        determineServerInfo();
    }

    public CompletableFuture<Void> connect() {
        if (state == BotState.CONNECTING || state == BotState.ONLINE) {
            BotLogger.warning("Bot is already connected or connecting!");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                setState(BotState.CONNECTING);
                BotLogger.info("Connecting bot to " + serverHost + ":" + serverPort);

                performAuthentication();
                establishConnection();

                setState(BotState.ONLINE);
                reconnectAttempts.set(0);
                BotLogger.info("Bot successfully connected!");

            } catch (Exception e) {
                setState(BotState.ERROR);
                lastDisconnectReason = e.getMessage();
                BotLogger.error("Connection failed: " + e.getMessage());

                scheduleReconnectIfEnabled();
            }
        });
    }

    public void disconnect(String reason) {
        if (session != null && session.isConnected()) {
            session.disconnect(reason);
        }
        setState(BotState.OFFLINE);
        lastDisconnectReason = reason;
        BotLogger.info("Bot disconnected: " + reason);
    }

    private void performAuthentication() throws AuthenticationException {
        try {
            String accountType = plugin.getConfigManager().getAccountType();
            String emailOrUsername = accountType.equals(BotConstants.ACCOUNT_TYPE_MICROSOFT)
                    ? plugin.getConfigManager().getAccountEmail()
                    : plugin.getConfigManager().getAccountUsername();
            String password = plugin.getConfigManager().getAccountPassword();

            AuthenticationService authService = AuthManager.authenticate(accountType, emailOrUsername, password);
            sessionManager = new SessionManager(authService);

            if (!sessionManager.isSessionValid()) {
                throw new AuthenticationException("Session is invalid after authentication");
            }

            botSession.setGameProfile(authService.getSelectedProfile());
            botSession.setAccessToken(authService.getAccessToken());

        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed", e);
        }
    }

    private void establishConnection() throws ConnectionException {
        try {
            MinecraftProtocol protocol;
            if (sessionManager.getAuthService().getSelectedProfile() != null) {
                protocol = new MinecraftProtocol(sessionManager.getAuthService().getSelectedProfile(),
                        sessionManager.getAuthService().getAccessToken());
            } else {
                protocol = new MinecraftProtocol(sessionManager.getAuthService().getUsername());
            }

            session = new TcpClientSession(serverHost, serverPort, protocol);

            protocolManager.setupSessionListeners(session);

            session.connect();

            botSession.setServerHost(serverHost);
            botSession.setServerPort(serverPort);
            botSession.setConnectTime(LocalDateTime.now());

        } catch (Exception e) {
            throw new ConnectionException("Failed to connect to server", e);
        }
    }

    private void determineServerInfo() {
        try {
            this.serverHost = getServerHost();
            this.serverPort = getServerPort();
            BotLogger.debug("Server info determined: " + serverHost + ":" + serverPort);
        } catch (Exception e) {
            BotLogger.error("Error determining server info: " + e.getMessage());
            this.serverHost = BotConstants.DEFAULT_SERVER_HOST;
            this.serverPort = BotConstants.DEFAULT_MINECRAFT_PORT;
        }
    }

    private String getServerHost() {
        try {
            String serverIp = Bukkit.getServer().getIp();
            if (serverIp != null && !serverIp.isEmpty() && !serverIp.equals("0.0.0.0")) {
                BotLogger.debug("Server IP from Bukkit: " + serverIp);
                return serverIp;
            }
        } catch (Exception e) {
            BotLogger.debug("Bukkit server IP not available: " + e.getMessage());
        }

        try {
            File serverProps = new File("server.properties");
            if (serverProps.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(serverProps)) {
                    props.load(fis);
                    String serverIp = props.getProperty("server-ip", "").trim();
                    if (!serverIp.isEmpty()) {
                        BotLogger.debug("Server IP from server.properties: " + serverIp);
                        return serverIp;
                    }
                }
            }
        } catch (Exception e) {
            BotLogger.debug("Could not read server.properties for IP: " + e.getMessage());
        }

        BotLogger.debug("Using localhost as fallback");
        return BotConstants.DEFAULT_SERVER_HOST;
    }

    private int getServerPort() {
        try {
            int port = Bukkit.getServer().getPort();
            if (port > 0) {
                BotLogger.debug("Server port from Bukkit: " + port);
                return port;
            }
        } catch (Exception e) {
            BotLogger.debug("Bukkit server port not available: " + e.getMessage());
        }

        try {
            File serverProps = new File("server.properties");
            if (serverProps.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(serverProps)) {
                    props.load(fis);
                    String portStr = props.getProperty("server-port", "25565").trim();
                    int port = Integer.parseInt(portStr);
                    BotLogger.debug("Server port from server.properties: " + port);
                    return port;
                }
            }
        } catch (Exception e) {
            BotLogger.debug("Could not read server.properties for port: " + e.getMessage());
        }

        BotLogger.debug("Using default port 25565");
        return BotConstants.DEFAULT_MINECRAFT_PORT;
    }

    private void scheduleReconnectIfEnabled() {
        if (!plugin.getConfigManager().shouldAutoReconnect()) {
            return;
        }

        int maxAttempts = plugin.getConfigManager().getMaxReconnectAttempts();
        int currentAttempts = reconnectAttempts.incrementAndGet();

        if (currentAttempts > maxAttempts) {
            BotLogger.error("Maximum number of reconnect attempts reached (" + maxAttempts + ")");
            return;
        }

        int delay = plugin.getConfigManager().getReconnectDelaySeconds();
        BotLogger.info("Scheduling reconnect in " + delay + " seconds (attempt " + currentAttempts + "/" + maxAttempts + ")");

        setState(BotState.RECONNECTING);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (state == BotState.RECONNECTING) {
                BotLogger.info("Trying automatic reconnect...");
                connect();
            }
        }, delay * 20L);
    }

    public boolean isConnected() {
        return state == BotState.ONLINE && session != null && session.isConnected();
    }

    public String getServerInfo() {
        return serverHost + ":" + serverPort;
    }

    public String getAccountInfo() {
        return botSession.getAccountDisplay();
    }

    public String getUptimeString() {
        return botSession.getUptimeString();
    }

    public void onSessionDisconnected(String reason) {
        setState(BotState.OFFLINE);
        lastDisconnectReason = reason;
        BotLogger.warning("Bot disconnected: " + reason);

        scheduleReconnectIfEnabled();
    }

    public void refreshSessionIfNeeded() {
        if (sessionManager != null && sessionManager.needsRefresh()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    if (sessionManager.refreshSession()) {
                        BotLogger.debug("Session successfully refreshed");
                    }
                } catch (Exception e) {
                    BotLogger.error("Session refresh failed: " + e.getMessage());
                    if (isConnected()) {
                        disconnect("Session expired");
                    }
                }
            });
        }
    }
}
