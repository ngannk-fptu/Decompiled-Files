/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.service;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.requestaccess.service.PagePermissionChecker;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PagePermissionCheckerImpl
implements PagePermissionChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PagePermissionCheckerImpl.class);
    private final PermissionManager permissionManager;
    private final UserManager userManager;

    @Autowired
    public PagePermissionCheckerImpl(@ComponentImport PermissionManager permissionManager, @ComponentImport UserManager userManager) {
        this.permissionManager = permissionManager;
        this.userManager = userManager;
    }

    @Override
    public boolean canAuthenticatedUserGrantAccessToPage(AbstractPage page) {
        Preconditions.checkNotNull((Object)page);
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, (Object)page);
    }

    @Override
    public boolean isUserPermittedToViewPage(String username, AbstractPage page) {
        Preconditions.checkNotNull((Object)page);
        try {
            User user = this.userManager.getUser(username);
            return !page.hasPermissions("View") || page.getContentPermissionSet("View").isPermitted(user);
        }
        catch (EntityException e) {
            LOGGER.info("Could not retrieve User entity by username [{}]", (Object)username);
            LOGGER.debug("Exception thrown while retrieve User entity.", (Throwable)e);
            return false;
        }
    }
}

