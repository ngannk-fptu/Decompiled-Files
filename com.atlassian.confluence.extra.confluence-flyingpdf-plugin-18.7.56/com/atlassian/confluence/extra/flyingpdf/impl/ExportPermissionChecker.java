/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.stereotype.Component;

@Component
@Internal
public class ExportPermissionChecker {
    private final PermissionManager permissionManager;

    ExportPermissionChecker(@ComponentImport PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void checkAuthorization(User user, Object target) {
        boolean permitted = false;
        if (target instanceof Space) {
            permitted = this.isPermitted(user, (Space)target);
        } else if (target instanceof AbstractPage) {
            permitted = this.isPermitted(user, (AbstractPage)target);
        }
        if (!permitted) {
            String username = user == null ? "anonymous" : user.getName();
            throw new NotAuthorizedException("The user " + username + " is not permitted to perform this export");
        }
    }

    public boolean isPermitted(User user, AbstractPage page) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, (Object)page);
    }

    public boolean isPermitted(User user, Space space) {
        return this.permissionManager.isConfluenceAdministrator(user) || this.permissionManager.hasPermission(user, Permission.EXPORT, (Object)space);
    }
}

