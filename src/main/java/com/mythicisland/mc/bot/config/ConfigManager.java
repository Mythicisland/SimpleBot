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
package com.mythicisland.mc.bot.config;

import com.mythicisland.mc.bot.Main;
import com.mythicisland.mc.bot.exceptions.ConfigurationException;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class ConfigManager {

    private final Main plugin;
    @Getter
    private FileConfiguration config;
    private final File configFile;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        loadConfig();
    }

    public void loadConfig() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            if (!configFile.exists()) {
                createDefaultConfig();
            }

            config = YamlConfiguration.loadConfiguration(configFile);
            validateConfig();

            BotLogger.info("Configuration loaded: " + configFile.getName());

        } catch (Exception e) {
            throw new ConfigurationException("Error loading configuration: " + e.getMessage(), e);
        }
    }

    private void createDefaultConfig() throws IOException {
        try (InputStream defaultConfig = plugin.getResource("config.yml")) {
            if (defaultConfig != null) {
                Files.copy(defaultConfig, configFile.toPath());
                BotLogger.info("Default configuration created: " + configFile.getName());
                return;
            }
        } catch (Exception e) {
            BotLogger.warning("Could not copy default config from resources: " + e.getMessage());
        }

        FileConfiguration defaultConfig = new YamlConfiguration();
        createDefaultConfigValues(defaultConfig);
        defaultConfig.save(configFile);
        BotLogger.info("Default configuration created programmatically");
    }

    private void createDefaultConfigValues(FileConfiguration config) {
        config.set("bot.account.type", "microsoft");
        config.set("bot.account.email", "your-email@example.com");
        config.set("bot.account.password", "your-password");
        config.set("bot.account.username", "MinecraftUsername"); // For legacy accounts

        config.set("bot.behavior.auto-connect-on-startup", true);
        config.set("bot.behavior.auto-reconnect", true);
        config.set("bot.behavior.reconnect-delay-seconds", 30);
        config.set("bot.behavior.max-reconnect-attempts", 10);
        config.set("bot.behavior.idle-timeout-minutes", 60);

        config.set("bot.advanced.keep-alive-interval", 20);
        config.set("bot.advanced.connection-timeout", 10);
        config.set("bot.advanced.packet-delay-ms", 50);

        config.set("plugin.language", "en-us");
        config.set("plugin.debug-mode", false);
        config.set("plugin.log-packets", false);

        config.setComments("bot.account", List.of(
                "Minecraft account configuration",
                "type: 'microsoft' for modern accounts or 'legacy' for old Mojang accounts",
                "For Microsoft: email + password",
                "For Legacy: username + password"
        ));

        config.setComments("bot.behavior", List.of(
                "Bot behavior configuration"
        ));

        config.setComments("plugin", List.of(
                "Plugin-specific settings"
        ));
    }

    private void validateConfig() throws ConfigurationException {
        String accountType = getString("bot.account.type");
        if (!accountType.equals("microsoft") && !accountType.equals("legacy")) {
            throw new ConfigurationException("Invalid account type: " + accountType + " (allowed: microsoft, legacy)");
        }

        if ("microsoft".equals(accountType)) {
            String email = getString("bot.account.email");
            String password = getString("bot.account.password");

            if (email.equals("your-email@example.com") || password.equals("your-password")) {
                throw new ConfigurationException("Please configure your real Microsoft account credentials in config.yml!");
            }

            if (!email.contains("@")) {
                throw new ConfigurationException("Invalid email address: " + email);
            }

        } else if ("legacy".equals(accountType)) {
            String username = getString("bot.account.username");
            String password = getString("bot.account.password");

            if (username.equals("MinecraftUsername") || password.equals("your-password")) {
                throw new ConfigurationException("Please configure your real Legacy account credentials in config.yml!");
            }
        }

        validatePositiveInt("bot.behavior.reconnect-delay-seconds", 1, 3600);
        validatePositiveInt("bot.behavior.max-reconnect-attempts", 1, 100);
        validatePositiveInt("bot.advanced.keep-alive-interval", 5, 300);
        validatePositiveInt("bot.advanced.connection-timeout", 1, 60);
        String language = getString("plugin.language");
        if (!language.matches("^[a-z]{2}-[a-z]{2}$")) {
            BotLogger.warning("Invalid language format: " + language + " (expected: e.g. 'en-us')");
        }
    }


    private void validatePositiveInt(String path, int min, int max) throws ConfigurationException {
        int value = getInt(path);
        if (value < min || value > max) {
            throw new ConfigurationException(String.format(
                    "Value for '%s' must be between %d and %d, but is: %d",
                    path, min, max, value
            ));
        }
    }

    public void reload() {
        try {
            loadConfig();
            BotLogger.info("Configuration reloaded");
        } catch (Exception e) {
            BotLogger.error("Error reloading configuration: " + e.getMessage());
            throw new ConfigurationException("Reload failed", e);
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new ConfigurationException("Error saving configuration", e);
        }
    }


    public String getString(String path) {
        return config.getString(path, "");
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public String getAccountType() {
        return getString("bot.account.type");
    }

    public String getAccountEmail() {
        return getString("bot.account.email");
    }

    public String getAccountPassword() {
        return getString("bot.account.password");
    }

    public String getAccountUsername() {
        return getString("bot.account.username");
    }

    public boolean shouldAutoConnectOnStartup() {
        return getBoolean("bot.behavior.auto-connect-on-startup");
    }

    public boolean shouldAutoReconnect() {
        return getBoolean("bot.behavior.auto-reconnect");
    }

    public int getReconnectDelaySeconds() {
        return getInt("bot.behavior.reconnect-delay-seconds");
    }

    public int getMaxReconnectAttempts() {
        return getInt("bot.behavior.max-reconnect-attempts");
    }

    public int getIdleTimeoutMinutes() {
        return getInt("bot.behavior.idle-timeout-minutes");
    }

    public int getKeepAliveInterval() {
        return getInt("bot.advanced.keep-alive-interval");
    }

    public int getConnectionTimeout() {
        return getInt("bot.advanced.connection-timeout");
    }

    public int getPacketDelayMs() {
        return getInt("bot.advanced.packet-delay-ms");
    }

    public String getLanguage() {
        return getString("plugin.language");
    }

    public boolean isDebugMode() {
        return getBoolean("plugin.debug-mode");
    }

    public boolean shouldLogPackets() {
        return getBoolean("plugin.log-packets");
    }
}