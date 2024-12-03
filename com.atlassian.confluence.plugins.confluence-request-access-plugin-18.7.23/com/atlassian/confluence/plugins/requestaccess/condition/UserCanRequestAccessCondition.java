/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.requestaccess.condition;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;

public class UserCanRequestAccessCondition
extends BaseConfluenceCondition {
    private final PermissionManager permissionManager;

    public UserCanRequestAccessCondition(@ComponentImport PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser user = context.getCurrentUser();
        return user != null && this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }
}

