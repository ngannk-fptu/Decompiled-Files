/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BasePermissionCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;

public class PagePermissionCondition
extends BasePermissionCondition {
    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        if (this.permission == null) {
            return false;
        }
        Object target = this.getPermissionTarget(context);
        if (target == null) {
            return false;
        }
        if (Permission.EDIT.equals(this.permission)) {
            return this.permissionManager.hasPermissionNoExemptions(context.getCurrentUser(), this.permission, target);
        }
        return this.permissionManager.hasPermission((User)context.getCurrentUser(), this.permission, target);
    }

    @Override
    protected Object getPermissionTarget(WebInterfaceContext context) {
        return context.getPage();
    }
}

