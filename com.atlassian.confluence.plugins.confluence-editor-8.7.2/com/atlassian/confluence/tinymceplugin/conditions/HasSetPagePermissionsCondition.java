/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.tinymceplugin.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.user.User;

public class HasSetPagePermissionsCondition
extends BaseConfluenceCondition {
    private SpacePermissionManager spacePermissionManager;
    private PermissionManager permissionManager;

    public boolean shouldDisplay(WebInterfaceContext context) {
        return this.isSpaceAdmin(context) || this.hasSetPagePermission(context);
    }

    private boolean hasSetPagePermission(WebInterfaceContext context) {
        Object obj = this.getPermissionTarget(context);
        if (obj == null) {
            return false;
        }
        return this.permissionManager.hasPermission((User)context.getCurrentUser(), Permission.SET_PERMISSIONS, obj);
    }

    private boolean isSpaceAdmin(WebInterfaceContext context) {
        return this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", context.getSpace(), (User)context.getCurrentUser());
    }

    private Object getPermissionTarget(WebInterfaceContext context) {
        return context.getPage() != null ? context.getPage() : context.getDraft();
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

