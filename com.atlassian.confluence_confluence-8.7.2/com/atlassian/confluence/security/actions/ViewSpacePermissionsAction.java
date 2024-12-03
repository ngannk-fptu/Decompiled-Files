/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.space.SpacePermissionsViewEvent;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.actions.AbstractPermissionsAction;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.user.User;

public class ViewSpacePermissionsAction
extends AbstractPermissionsAction
implements SpaceAware,
SpaceAdministrative,
Spaced,
Evented<SpacePermissionsViewEvent> {
    private PermissionsAdministrator spacePermissionsAdministrator;
    private SpaceManager spaceManager;
    private Space space;
    private String key;
    protected ThemeManager themeManager;

    @Override
    public SpacePermissionsViewEvent getEventToPublish(String result) {
        return new SpacePermissionsViewEvent(this, this.getSpace());
    }

    @Override
    public PermissionsAdministrator getPermissionsAdministrator() {
        return this.spacePermissionsAdministrator;
    }

    @Override
    public void populateAdministrator() {
        this.spacePermissionsAdministrator = this.permissionsAdministratorBuilder.buildSpacePermissionAdministrator(this.getSpace());
    }

    public boolean isAnonymousSpacePermissionWithoutGlobalPermission() {
        return this.getSpace() != null && this.anonymousIsAssignedToViewSpaceIgnoreUse() && !this.spacePermissionManager.hasPermission("USECONFLUENCE", null, null);
    }

    private boolean anonymousIsAssignedToViewSpaceIgnoreUse() {
        SpacePermission anonymousViewSpace = SpacePermission.createAnonymousSpacePermission("VIEWSPACE", this.getSpace());
        return this.spacePermissionManager.permissionExists(anonymousViewSpace);
    }

    public boolean isAnyUserViewSpacePermissionEnabled() {
        return this.getSpace() != null && this.spacePermissionManager.permissionExists(SpacePermission.createAuthenticatedUsersSpacePermission("VIEWSPACE", this.getSpace()));
    }

    public boolean isUnlicensedGlobalPermissionEnabled() {
        return this.spacePermissionManager.permissionExists(SpacePermission.createAuthenticatedUsersSpacePermission("LIMITEDUSECONFLUENCE", null));
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.getSpace());
    }

    @Override
    public String getGuardPermission() {
        return "VIEWSPACE";
    }

    @Override
    public Space getSpace() {
        if (this.space == null && this.isValidSpaceKey()) {
            this.space = this.spaceManager.getSpace(this.key);
        }
        return this.space;
    }

    private boolean isValidSpaceKey() {
        return Space.isValidSpaceKey(this.key);
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public ThemeManager getThemeManager() {
        return this.themeManager;
    }
}

