/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.sal.api.user.UserManager;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class AccessTokensServletValidation {
    private final UserManager userManager;

    public AccessTokensServletValidation(UserManager userManager) {
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    public Optional<String> validate(HttpServletRequest request) {
        String username = this.userManager.getRemoteUsername(request);
        if (username != null) {
            return Optional.of(username);
        }
        return Optional.empty();
    }
}

