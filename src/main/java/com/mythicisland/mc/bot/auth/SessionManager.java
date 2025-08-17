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
package com.mythicisland.mc.bot.auth;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.mythicisland.mc.bot.exceptions.AuthenticationException;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class SessionManager {

    private AuthenticationService authService;
    private LocalDateTime lastRefresh;
    private int refreshAttempts = 0;
    private static final int MAX_REFRESH_ATTEMPTS = 3;
    private static final int REFRESH_THRESHOLD_HOURS = 22;

    public SessionManager(AuthenticationService authService) {
        this.authService = authService;
        this.lastRefresh = LocalDateTime.now();
    }

    public boolean needsRefresh() {
        if (lastRefresh == null) {
            return true;
        }

        long hoursSinceRefresh = ChronoUnit.HOURS.between(lastRefresh, LocalDateTime.now());
        return hoursSinceRefresh >= REFRESH_THRESHOLD_HOURS;
    }

    public boolean refreshSession() {
        if (!needsRefresh()) {
            BotLogger.debug("Session refresh not necessary");
            return true;
        }

        if (refreshAttempts >= MAX_REFRESH_ATTEMPTS) {
            BotLogger.error("Maximum number of refresh attempts reached");
            return false;
        }

        try {
            BotLogger.debug("Attempting session refresh...");
            if (authService.getUsername() != null && authService.getPassword() != null) {
                authService.login();
            } else {
                BotLogger.warning("No username/password available for session refresh");
                return false;
            }

            lastRefresh = LocalDateTime.now();
            refreshAttempts = 0;

            BotLogger.debug("Session successfully refreshed");
            return true;

        } catch (Exception e) {
            refreshAttempts++;
            BotLogger.error("Session refresh failed (attempt " + refreshAttempts + "): " + e.getMessage());

            if (refreshAttempts >= MAX_REFRESH_ATTEMPTS) {
                throw new AuthenticationException("Session refresh failed after " + MAX_REFRESH_ATTEMPTS + " attempts", e);
            }

            return false;
        }
    }

    public boolean isSessionValid() {
        try {
            return authService.getSelectedProfile() != null &&
                    authService.getAccessToken() != null &&
                    !authService.getAccessToken().isEmpty();
        } catch (Exception e) {
            BotLogger.debug("Session validation failed: " + e.getMessage());
            return false;
        }
    }

    public void invalidateSession() {
        BotLogger.debug("Invalidating session");
        lastRefresh = null;
        refreshAttempts = 0;
    }

    public String getSessionInfo() {
        if (authService.getSelectedProfile() != null) {
            return authService.getSelectedProfile().getName() + " (" +
                    (isSessionValid() ? "valid" : "invalid") + ")";
        }
        return "No session";
    }
}
