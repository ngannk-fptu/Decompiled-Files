/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.PermissionManager;

public class SystemAdministratorCondition
extends BaseConfluenceCondition {
    private PermissionManager permissionManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return this.permissionManager.isSystemAdministrator(context.getCurrentUser());
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

