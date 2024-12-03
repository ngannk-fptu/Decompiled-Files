/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.security.JiraAuthenticationContext
 *  com.atlassian.jira.security.PermissionManager
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.core.permission;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.plugins.whitelist.core.permission.AbstractPermissionChecker;
import com.atlassian.plugins.whitelist.core.permission.PermissionChecker;
import com.google.common.base.Preconditions;

public class JiraPermissionChecker
extends AbstractPermissionChecker
implements PermissionChecker {
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final PermissionManager permissionManager;

    public JiraPermissionChecker(JiraAuthenticationContext jiraAuthenticationContext, PermissionManager permissionManager) {
        this.jiraAuthenticationContext = (JiraAuthenticationContext)Preconditions.checkNotNull((Object)jiraAuthenticationContext, (Object)"jiraAuthenticationContext");
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager, (Object)"permissionManager");
    }

    @Override
    public boolean canCurrentUserManageWhitelist() {
        return this.permissionManager.hasPermission(44, this.jiraAuthenticationContext.getUser());
    }
}

