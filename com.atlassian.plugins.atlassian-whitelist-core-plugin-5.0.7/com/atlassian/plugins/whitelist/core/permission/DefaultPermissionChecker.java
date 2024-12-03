/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.core.permission;

import com.atlassian.plugins.whitelist.core.permission.AbstractPermissionChecker;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Preconditions;

public class DefaultPermissionChecker
extends AbstractPermissionChecker {
    private final UserManager userManager;

    public DefaultPermissionChecker(UserManager userManager) {
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    @Override
    public boolean canCurrentUserManageWhitelist() {
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        return this.userManager.isSystemAdmin(remoteUserKey);
    }
}

