/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.integration.jira.applinks.UserAccessResolver
 */
package com.atlassian.confluence.impl.integration.jira.applinks;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.integration.jira.applinks.UserAccessResolver;

public class ConfluenceUserAccessResolver
implements UserAccessResolver {
    private final PermissionManager permissionManager;

    public ConfluenceUserAccessResolver(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public boolean isAnonymousAccessAllowed() {
        return this.permissionManager.hasPermission(null, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }
}

