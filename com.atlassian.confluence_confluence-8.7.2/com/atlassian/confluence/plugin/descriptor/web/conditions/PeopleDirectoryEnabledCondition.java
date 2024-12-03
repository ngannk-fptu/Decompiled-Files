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
import com.atlassian.user.User;

public class PeopleDirectoryEnabledCondition
extends BaseConfluenceCondition {
    private static final String CONFLUENCE_DISABLE_PEOPLEDIRECTORY_ANONYMOUS = "confluence.disable.peopledirectory.anonymous";
    private static final String CONFLUENCE_DISABLE_PEOPLEDIRECTORY_ALL = "confluence.disable.peopledirectory.all";
    private PermissionManager permissionManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return !this.isPeopleDirectoryDisabled(context.getCurrentUser());
    }

    public boolean isPeopleDirectoryDisabled(User user) {
        boolean disabled = false;
        if (user == null && !(disabled = "true".equals(System.getProperty(CONFLUENCE_DISABLE_PEOPLEDIRECTORY_ANONYMOUS)))) {
            disabled = !this.permissionManager.hasPermission(null, Permission.VIEW, PermissionManager.TARGET_PEOPLE_DIRECTORY);
        }
        return "true".equals(System.getProperty(CONFLUENCE_DISABLE_PEOPLEDIRECTORY_ALL)) || disabled;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

