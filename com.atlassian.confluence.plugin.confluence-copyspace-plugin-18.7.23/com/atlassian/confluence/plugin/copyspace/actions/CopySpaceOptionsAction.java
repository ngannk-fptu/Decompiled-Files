/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.copyspace.actions;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.user.User;

public class CopySpaceOptionsAction
extends AbstractSpaceAction
implements SpaceAware {
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, (Object)this.getSpace());
    }

    public boolean isSpaceRequired() {
        return true;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }
}

