/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.user.User;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionService {
    private final PermissionManager permissionManager;
    private final PageManager pageManager;

    @Autowired
    public PermissionService(@ComponentImport PermissionManager permissionManager, @ComponentImport PageManager pageManager) {
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
    }

    public boolean isSysAdmin(User user) {
        return user != null && (this.permissionManager.isSystemAdministrator(Objects.requireNonNull(user)) || this.permissionManager.isConfluenceAdministrator(user));
    }

    public void enforceSysAdmin(User user) {
        if (!this.isSysAdmin(user)) {
            throw new AuthorisationException();
        }
    }

    public boolean canEdit(User user, long contentId) {
        return user != null && this.permissionManager.hasPermission(user, Permission.EDIT, (Object)this.pageManager.getAbstractPage(contentId));
    }
}

