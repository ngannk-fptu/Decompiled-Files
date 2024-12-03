/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

public class AbstractSidebarService {
    protected PermissionManager permissionManager;
    protected SpaceManager spaceManager;

    public AbstractSidebarService(PermissionManager permissionManager, SpaceManager spaceManager) {
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
    }

    protected void checkPermissions(Space space) {
        if (space == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space)) {
            throw new NotFoundException("The specified space was not found.");
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, (Object)space)) {
            throw new PermissionException("You do not have permission to change details for space with key: " + space.getKey());
        }
    }

    @Deprecated
    protected void checkPermissions(String spaceKey) {
        this.checkEditPermissions(spaceKey);
    }

    protected void checkEditPermissions(String spaceKey) {
        this.checkPermissions(this.spaceManager.getSpace(spaceKey));
    }

    protected void checkViewPermissions(String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space)) {
            throw new NotFoundException("The specified space was not found.");
        }
    }
}

