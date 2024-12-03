/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.user.User;

public class TargetUserHasPersonalSpaceCondition
extends BaseConfluenceCondition {
    private SpaceManager spaceManager;
    private PermissionManager permissionManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        Space space = this.spaceManager.getPersonalSpace(context.getTargetedUser());
        if (space == null) {
            return false;
        }
        return this.permissionManager.hasPermission((User)context.getCurrentUser(), Permission.VIEW, space);
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

