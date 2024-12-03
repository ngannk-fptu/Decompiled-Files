/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.createcontent.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;

public class UserCanUseConfluenceCondition
extends BaseConfluenceCondition {
    private final PermissionManager permissionManager;

    public UserCanUseConfluenceCondition(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.permissionManager.hasPermission((User)context.getCurrentUser(), Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }
}

