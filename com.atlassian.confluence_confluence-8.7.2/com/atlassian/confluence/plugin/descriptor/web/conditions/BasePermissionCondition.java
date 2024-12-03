/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.user.User;
import java.util.Map;

public abstract class BasePermissionCondition
extends BaseConfluenceCondition {
    protected PermissionManager permissionManager;
    protected Permission permission = null;

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
        try {
            this.permission = Permission.forName(params.get("permission"));
        }
        catch (Exception e) {
            throw new PluginParseException("Could not determine permission for condition. " + e.getMessage(), (Throwable)e);
        }
        super.init(params);
    }

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        if (this.permission == null) {
            return false;
        }
        Object target = this.getPermissionTarget(context);
        if (target == null) {
            return false;
        }
        return this.permissionManager.hasPermission((User)context.getCurrentUser(), this.permission, target);
    }

    protected abstract Object getPermissionTarget(WebInterfaceContext var1);

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

