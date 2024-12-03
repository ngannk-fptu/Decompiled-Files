/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;

public class DraftPermissionsDelegate
extends AbstractPermissionsDelegate<Draft> {
    private SpaceManager spaceManager;

    @Override
    public boolean canView(User user, Draft target) {
        return this.wasCreatedBy(target, user);
    }

    @Override
    public boolean canEdit(User user, Draft target) {
        return this.wasCreatedBy(target, user);
    }

    @Override
    public boolean canSetPermissions(User user, Draft target) {
        if (!this.wasCreatedBy(target, user)) {
            return false;
        }
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target) || this.hasSpaceLevelPermission("SETPAGEPERMISSIONS", user, target);
    }

    @Override
    public boolean canRemove(User user, Draft target) {
        return this.wasCreatedBy(target, user);
    }

    @Override
    public boolean canExport(User user, Draft target) {
        throw new IllegalStateException("Drafts should not be exported.");
    }

    @Override
    public boolean canAdminister(User user, Draft target) {
        throw new IllegalStateException("Administration privileges do not apply to drafts");
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.spacePermissionManager.hasPermissionNoExemptions("EDITSPACE", (Space)container, user);
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        return this.getSpaceManager().getSpace(((Draft)target).getDraftSpaceKey());
    }

    private boolean wasCreatedBy(Object target, User user) {
        return ((Draft)target).wasCreatedBy(user);
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public SpaceManager getSpaceManager() {
        if (this.spaceManager == null) {
            this.spaceManager = (SpaceManager)ContainerManager.getComponent((String)"spaceManager");
        }
        return this.spaceManager;
    }
}

