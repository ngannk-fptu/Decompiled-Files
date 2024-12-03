/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserPermissions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;
import com.atlassian.user.User;
import java.util.Collection;

public class SpaceEntityUserPermissionsExpander
extends AbstractRecursiveEntityExpander<SpaceEntityUserPermissions> {
    private SpacePermissionManager spacePermissionManager;
    private SpaceManager spaceManager;
    private UserAccessor userAccessor;

    public SpaceEntityUserPermissionsExpander(SpacePermissionManager spacePermissionManager, SpaceManager spaceManager, UserAccessor userAccessor) {
        this.spacePermissionManager = spacePermissionManager;
        this.spaceManager = spaceManager;
        this.userAccessor = userAccessor;
    }

    protected SpaceEntityUserPermissions expandInternal(SpaceEntityUserPermissions entity) {
        Collection permissions = SpacePermission.GENERIC_SPACE_PERMISSIONS;
        for (String permission : permissions) {
            ConfluenceUser user;
            Space space;
            if (!this.spacePermissionManager.hasPermission(permission, space = this.spaceManager.getSpace(entity.getSpaceKey()), (User)(user = this.userAccessor.getUserByName(entity.getEffectiveUser())))) continue;
            entity.addPermission(permission);
        }
        return entity;
    }
}

