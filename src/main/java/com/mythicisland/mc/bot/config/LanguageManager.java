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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mythicisland.mc.bot.Main;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final Main plugin;
    private final File langDir;
    private final Gson gson;

    @Getter
    private String currentLanguage;
    private JsonObject messages;
    private final Map<String, JsonObject> loadedLanguages;

    public LanguageManager(Main plugin) {
        this.plugin = plugin;
        this.langDir = new File(plugin.getDataFolder(), "lang");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.loadedLanguages = new HashMap<>();

        initializeLanguageSystem();
    }

    private void initializeLanguageSystem() {
        try {
            if (!langDir.exists()) {
                langDir.mkdirs();
            }

            createDefaultLanguageFiles();
            String configLanguage = plugin.getConfigManager().getLanguage();
            loadLanguage(configLanguage);

            BotLogger.info("Language system initialized with: " + currentLanguage);

        } catch (Exception e) {
            BotLogger.error("Error initializing language system: " + e.getMessage());
            createFallbackMessages();
        }
    }

    private void createDefaultLanguageFiles() {
        createLanguageFile("en-us", createEnglishMessages());
        createLanguageFile("de-de", createGermanMessages());
    }

    private void createLanguageFile(String language, JsonObject messages) {
        File langFile = new File(langDir, language + ".json");

        if (!langFile.exists()) {
            try {
                String json = gson.toJson(messages);
                Files.write(langFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
                BotLogger.info("Language file created: " + langFile.getName());
            } catch (IOException e) {
                BotLogger.error("Error creating language file " + language + ": " + e.getMessage());
            }
        }
    }

    public void loadLanguage(String language) {
        try {
            if (loadedLanguages.containsKey(language)) {
                this.messages = loadedLanguages.get(language);
                this.currentLanguage = language;
                return;
            }

            File langFile = new File(langDir, language + ".json");
            if (!langFile.exists()) {
                BotLogger.warning("Language file not found: " + language + ".json, using en-us");
                language = "en-us";
                langFile = new File(langDir, language + ".json");

                if (!langFile.exists()) {
                    BotLogger.error("en-us.json also not found! Creating fallback...");
                    createFallbackMessages();
                    return;
                }
            }

            String json = Files.readString(langFile.toPath(), StandardCharsets.UTF_8);
            JsonObject loadedMessages = JsonParser.parseString(json).getAsJsonObject();

            loadedLanguages.put(language, loadedMessages);
            this.messages = loadedMessages;
            this.currentLanguage = language;

            BotLogger.info("Language loaded: " + language);

        } catch (Exception e) {
            BotLogger.error("Error loading language " + language + ": " + e.getMessage());
            createFallbackMessages();
        }
    }

    private void createFallbackMessages() {
        this.messages = createEnglishMessages();
        this.currentLanguage = "en-us";
        BotLogger.warning("Using fallback messages (English)");
    }

    public String getMessage(String key) {
        return getMessage(key, new Object[0]);
    }

    public String getMessage(String key, Object... replacements) {
        try {
            String message = getNestedValue(messages, key);

            if (message == null) {
                BotLogger.warning("Message not found: " + key);
                return "§c[Missing: " + key + "]";
            }

            return formatMessage(message, replacements);

        } catch (Exception e) {
            BotLogger.error("Error retrieving message " + key + ": " + e.getMessage());
            return "§c[Error: " + key + "]";
        }
    }

    private String getNestedValue(JsonObject json, String key) {
        String[] parts = key.split("\\.");
        JsonObject current = json;

        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(parts[i]);
        }

        String lastKey = parts[parts.length - 1];
        if (!current.has(lastKey) || !current.get(lastKey).isJsonPrimitive()) {
            return null;
        }

        return current.get(lastKey).getAsString();
    }

    private String formatMessage(String message, Object... replacements) {
        String formatted = message;
        for (int i = 0; i < replacements.length; i++) {
            formatted = formatted.replace("{" + i + "}", String.valueOf(replacements[i]));
        }

        formatted = formatted.replace("&", "§");

        return formatted;
    }

    public void reloadLanguages() {
        loadedLanguages.clear();
        String configLanguage = plugin.getConfigManager().getLanguage();
        loadLanguage(configLanguage);
        BotLogger.info("Alle Sprachen neu geladen");
    }

    private JsonObject createEnglishMessages() {
        JsonObject root = new JsonObject();

        JsonObject bot = new JsonObject();
        bot.addProperty("connecting", "&aConnecting bot to server...");
        bot.addProperty("connected", "&aBot successfully connected!");
        bot.addProperty("disconnected", "&cBot disconnected: &7{0}");
        bot.addProperty("already_connected", "&cBot is already connected!");
        bot.addProperty("not_connected", "&cBot is not connected!");
        bot.addProperty("reconnecting", "&eReconnecting bot...");

        JsonObject status = new JsonObject();
        status.addProperty("online", "&aOnline");
        status.addProperty("offline", "&7Offline");
        status.addProperty("connecting", "&eConnecting");
        status.addProperty("error", "&cError");
        bot.add("status", status);

        JsonObject errors = new JsonObject();
        errors.addProperty("auth_failed", "&cAuthentication failed: &7{0}");
        errors.addProperty("connection_failed", "&cConnection failed: &7{0}");
        errors.addProperty("config_invalid", "&cInvalid configuration: &7{0}");
        errors.addProperty("server_not_found", "&cCould not determine server address");
        bot.add("errors", errors);

        root.add("bot", bot);

        JsonObject commands = new JsonObject();
        JsonObject botCmd = new JsonObject();
        botCmd.addProperty("description", "Bot management commands");
        botCmd.addProperty("usage", "&cUsage: &7/bot <connect|disconnect|status|reconnect|reload>");
        botCmd.addProperty("unknown_subcommand", "&cUnknown subcommand: &7{0}");
        botCmd.addProperty("no_permission", "&cYou don't have permission to use this command!");
        commands.add("bot", botCmd);

        root.add("commands", commands);

        JsonObject config = new JsonObject();
        config.addProperty("reloaded", "&aConfiguration reloaded successfully!");
        config.addProperty("reload_failed", "&cFailed to reload configuration: &7{0}");
        root.add("config", config);

        JsonObject display = new JsonObject();
        display.addProperty("status_header", "&7=== &bBot Status &7===");
        display.addProperty("status_line", "&7Status: {0}");
        display.addProperty("server_line", "&7Server: &b{0}");
        display.addProperty("account_line", "&7Account: &a{0}");
        display.addProperty("uptime_line", "&7Uptime: &e{0}");
        display.addProperty("last_error_line", "&7Last Error: &c{0}");
        root.add("display", display);

        return root;
    }

    private JsonObject createGermanMessages() {
        JsonObject root = new JsonObject();
        JsonObject bot = new JsonObject();
        bot.addProperty("connecting", "&aVerbinde Bot mit Server...");
        bot.addProperty("connected", "&aBot erfolgreich verbunden!");
        bot.addProperty("disconnected", "&cBot getrennt: &7{0}");
        bot.addProperty("already_connected", "&cBot ist bereits verbunden!");
        bot.addProperty("not_connected", "&cBot ist nicht verbunden!");
        bot.addProperty("reconnecting", "&eBot wird neu verbunden...");

        JsonObject status = new JsonObject();
        status.addProperty("online", "&aOnline");
        status.addProperty("offline", "&7Offline");
        status.addProperty("connecting", "&eVerbindet");
        status.addProperty("error", "&cFehler");
        bot.add("status", status);

        JsonObject errors = new JsonObject();
        errors.addProperty("auth_failed", "&cAuthentifizierung fehlgeschlagen: &7{0}");
        errors.addProperty("connection_failed", "&cVerbindung fehlgeschlagen: &7{0}");
        errors.addProperty("config_invalid", "&cUngültige Konfiguration: &7{0}");
        errors.addProperty("server_not_found", "&cServer-Adresse konnte nicht ermittelt werden");
        bot.add("errors", errors);

        root.add("bot", bot);

        JsonObject commands = new JsonObject();
        JsonObject botCmd = new JsonObject();
        botCmd.addProperty("description", "Bot-Verwaltungskommandos");
        botCmd.addProperty("usage", "&cVerwendung: &7/bot <connect|disconnect|status|reconnect|reload>");
        botCmd.addProperty("unknown_subcommand", "&cUnbekannter Befehl: &7{0}");
        botCmd.addProperty("no_permission", "&cDu hast keine Berechtigung für diesen Befehl!");
        commands.add("bot", botCmd);

        root.add("commands", commands);

        JsonObject config = new JsonObject();
        config.addProperty("reloaded", "&aKonfiguration erfolgreich neu geladen!");
        config.addProperty("reload_failed", "&cKonfiguration konnte nicht neu geladen werden: &7{0}");
        root.add("config", config);

        JsonObject display = new JsonObject();
        display.addProperty("status_header", "&7=== &bBot Status &7===");
        display.addProperty("status_line", "&7Status: {0}");
        display.addProperty("server_line", "&7Server: &b{0}");
        display.addProperty("account_line", "&7Account: &a{0}");
        display.addProperty("uptime_line", "&7Laufzeit: &e{0}");
        display.addProperty("last_error_line", "&7Letzter Fehler: &c{0}");
        root.add("display", display);

        return root;
    }
}