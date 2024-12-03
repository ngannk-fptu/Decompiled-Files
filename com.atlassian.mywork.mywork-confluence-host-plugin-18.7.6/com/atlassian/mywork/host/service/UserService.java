/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    private final UserManager userManager;

    public UserService(UserManager userManager) {
        this.userManager = userManager;
    }

    @Deprecated
    public String getRemoteUsername(HttpServletRequest request) {
        return this.userManager.getRemoteUsername(request);
    }

    public UserKey getRemoteUserKey(HttpServletRequest request) {
        return this.userManager.getRemoteUserKey(request);
    }

    @Deprecated
    public String getBypassUsername(HttpServletRequest request, String bypass) {
        String username = this.userManager.getRemoteUsername(request);
        if (bypass != null) {
            if (!this.userManager.isSystemAdmin(username)) {
                throw new AuthorisationException();
            }
            return bypass;
        }
        return username;
    }
}

