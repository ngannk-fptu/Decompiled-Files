/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.space.SpacePermissionsUpdateEvent;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.actions.AbstractEditPermissionAction;
import com.atlassian.confluence.security.actions.EditPermissionsAware;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
public class EditSpacePermissionsAction
extends AbstractEditPermissionAction
implements EditPermissionsAware,
SpaceAware,
SpaceAdministrative,
Spaced,
Evented<SpacePermissionsUpdateEvent> {
    private static final Logger log = LoggerFactory.getLogger(EditSpacePermissionsAction.class);
    private SpaceManager spaceManager;
    private Space space;
    private String key;
    private ThemeManager themeManager;

    public String doAddBrowseSpacePermission() {
        if (StringUtils.isNotEmpty((CharSequence)this.getSelectedUsername())) {
            ConfluenceUser selectedUser = this.userAccessor.getUserByName(this.selectedUsername);
            SpacePermission newBrowsePermission = SpacePermission.createUserSpacePermission(this.getGuardPermission(), this.getSpace(), selectedUser);
            this.permissionsAdministrator.addPermission(newBrowsePermission);
        } else if (StringUtils.isNotEmpty((CharSequence)this.getSelectedGroup())) {
            SpacePermission newBrowsePermission = SpacePermission.createGroupSpacePermission(this.getGuardPermission(), this.getSpace(), this.selectedGroup);
            this.permissionsAdministrator.addPermission(newBrowsePermission);
        }
        return "success";
    }

    public String doDisableAnyUserViewSpacePermission() {
        Space space = this.getSpace();
        if (space != null) {
            SpacePermission anyUserViewSpacePermission = SpacePermission.createAuthenticatedUsersSpacePermission("VIEWSPACE", space);
            space.getPermissions().stream().filter(permission -> permission.equals(anyUserViewSpacePermission)).findFirst().ifPresent(existingHibernatePermissionObject -> this.spacePermissionManager.removePermission((SpacePermission)existingHibernatePermissionObject));
        } else {
            log.error("'doDisableAnyUserViewSpacePermission' - space not found, cannot disable view space permission for all authenticated users");
        }
        return "success";
    }

    @Override
    public void populateAdministrator() {
        this.permissionsAdministrator = this.permissionsAdministratorBuilder.buildEditSpaceAdministrator(this.getSpace(), this.getAuthenticatedUser(), this.getUsersToAddAsList(), this.getGroupsToAddAsList());
    }

    @Override
    public String execute() throws Exception {
        String errorMessageKey = "error.cannot.remove.all.admin.permissions.for.space";
        return this.executeAction("error.cannot.remove.all.admin.permissions.for.space");
    }

    @Override
    public SpacePermissionsUpdateEvent getEventToPublish(String result) {
        if ("success".equals(result)) {
            return new SpacePermissionsUpdateEvent(this, this.getSpace());
        }
        return null;
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
        if (this.space == null && Space.isValidSpaceKey(this.key)) {
            this.space = this.spaceManager.getSpace(this.key);
        }
        return this.space;
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

    public boolean hasSpaceSideBar() {
        if (this.themeManager == null) {
            return false;
        }
        Theme spaceTheme = this.themeManager.getSpaceTheme(this.key);
        return spaceTheme.hasSpaceSideBar();
    }
}

