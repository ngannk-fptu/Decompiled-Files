/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.analytics.client;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import javax.servlet.http.HttpServletRequest;

public class UserPermissionsHelper {
    private final UserManager userManager;

    public UserPermissionsHelper(UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean isRequestUserSystemAdmin(HttpServletRequest request) {
        return this.isUserSystemAdmin(this.userManager.getRemoteUserKey(request));
    }

    public boolean isCurrentUserSystemAdmin() {
        return this.isUserSystemAdmin(this.userManager.getRemoteUserKey());
    }

    public boolean isUserSystemAdmin(UserKey userKey) {
        return this.userManager.isSystemAdmin(userKey);
    }
}

