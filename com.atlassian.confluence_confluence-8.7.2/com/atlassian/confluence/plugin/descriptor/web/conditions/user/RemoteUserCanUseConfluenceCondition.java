/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;

public class RemoteUserCanUseConfluenceCondition
extends BaseConfluenceCondition {
    private PermissionManager permissionManager;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.permissionManager.hasPermission((User)context.getCurrentUser(), Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

