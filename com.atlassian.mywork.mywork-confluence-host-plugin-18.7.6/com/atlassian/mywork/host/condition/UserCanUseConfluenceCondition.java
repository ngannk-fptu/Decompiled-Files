/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.mywork.host.condition;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.beans.factory.annotation.Autowired;

public class UserCanUseConfluenceCondition
extends BaseConfluenceCondition {
    private PermissionManager permissionManager;

    @Autowired
    public UserCanUseConfluenceCondition(@ComponentImport PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.permissionManager.hasPermission((User)context.getCurrentUser(), Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }
}

