/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;

public class CanEditSpaceStylesCondition
extends BaseConfluenceCondition {
    private PermissionManager permissionManager;
    private SettingsManager settingsManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser user = context.getCurrentUser();
        if (this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)) {
            return true;
        }
        return this.settingsManager.getGlobalSettings().isEnableSpaceStyles();
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}

