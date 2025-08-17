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

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.InvalidCredentialsException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MojangAuthenticationService;
import com.mythicisland.mc.bot.constants.BotConstants;
import com.mythicisland.mc.bot.exceptions.AuthenticationException;
import com.mythicisland.mc.bot.utils.BotLogger;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthManager {

    public static AuthenticationService authenticateMicrosoft(String email, String password) {
        try {
            BotLogger.debug("Starting Microsoft authentication for: " + email);
            BotLogger.warning("Microsoft authentication not fully implemented yet");
            BotLogger.warning("Using legacy authentication as fallback");
            return authenticateLegacy(email, password);

        } catch (Exception e) {
            BotLogger.error("Microsoft authentication failed: " + e.getMessage());
            throw new AuthenticationException("Microsoft auth failed: " + e.getMessage(), e);
        }
    }

    public static AuthenticationService authenticateLegacy(String username, String password) {
        try {
            BotLogger.debug("Starting legacy authentication for: " + username);

            AuthenticationService authService = new MojangAuthenticationService();
            authService.setUsername(username);
            authService.setPassword(password);
            authService.login();

            BotLogger.debug("Legacy authentication successful");
            return authService;

        } catch (InvalidCredentialsException e) {
            BotLogger.error("Invalid credentials for: " + username);
            throw new AuthenticationException("Invalid credentials", e);
        } catch (Exception e) {
            BotLogger.error("Legacy authentication failed: " + e.getMessage());
            throw new AuthenticationException("Legacy auth failed: " + e.getMessage(), e);
        }
    }

    public static AuthenticationService authenticate(String accountType, String emailOrUsername, String password) {
        if (BotConstants.ACCOUNT_TYPE_MICROSOFT.equalsIgnoreCase(accountType)) {
            return authenticateMicrosoft(emailOrUsername, password);
        } else if (BotConstants.ACCOUNT_TYPE_LEGACY.equalsIgnoreCase(accountType)) {
            return authenticateLegacy(emailOrUsername, password);
        } else {
            throw new AuthenticationException("Unknown account type: " + accountType);
        }
    }

    public static boolean validateCredentials(String accountType, String emailOrUsername, String password) {
        try {
            AuthenticationService authService = authenticate(accountType, emailOrUsername, password);
            return authService.getSelectedProfile() != null;
        } catch (Exception e) {
            BotLogger.debug("Credential validation failed: " + e.getMessage());
            return false;
        }
    }

    public static GameProfile getGameProfile(AuthenticationService authService) {
        return authService.getSelectedProfile();
    }
}