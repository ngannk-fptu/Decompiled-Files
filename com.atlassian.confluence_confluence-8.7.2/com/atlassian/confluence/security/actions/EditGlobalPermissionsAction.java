/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.admin.GlobalPermissionsUpdateEvent;
import com.atlassian.confluence.impl.security.administrators.EditGlobalPermissionsAdministrator;
import com.atlassian.confluence.security.EntityRuntimeException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.actions.AbstractEditPermissionAction;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditGlobalPermissionsAction
extends AbstractEditPermissionAction
implements Evented<GlobalPermissionsUpdateEvent> {
    private static final Logger log = LoggerFactory.getLogger(EditGlobalPermissionsAction.class);
    private static final SpacePermission UNLICENSED_AUTHENTICATED_CAN_USE_PERM = SpacePermission.createAuthenticatedUsersSpacePermission("LIMITEDUSECONFLUENCE", null);
    private static final SpacePermission UNLICENSED_AUTHENTICATED_CAN_BROWSE_USERS = SpacePermission.createAuthenticatedUsersSpacePermission("VIEWUSERPROFILES", null);
    private static final SpacePermission ANONYMOUS_CAN_USE_PERM = SpacePermission.createAnonymousSpacePermission("USECONFLUENCE", null);
    private static final SpacePermission ANONYMOUS_CAN_BROWSE_USERS_PERM = SpacePermission.createAnonymousSpacePermission("VIEWUSERPROFILES", null);

    @Override
    public void validate() {
        super.validate();
        if (!this.isAnonymousCanUseSetBeforeMoreSpecificPermissions(this.getRequestParams())) {
            this.addActionError(this.getText("error.anonymous.can.use.required.first"));
        }
        if (!this.isUnlicensedAuthenticatedCanUseSetBeforeMoreSpecificPermissions(this.getRequestParams())) {
            this.addActionError(this.getText("error.unlicensed.can.use.required.first"));
        }
    }

    @Override
    public String execute() throws Exception {
        String errorMessageKey = "error.cannot.remove.all.admin.permissions";
        return this.executeAction("error.cannot.remove.all.admin.permissions");
    }

    @Override
    public GlobalPermissionsUpdateEvent getEventToPublish(String result) {
        if ("success".equals(result)) {
            return new GlobalPermissionsUpdateEvent(this);
        }
        return null;
    }

    public boolean canSetPermissionOnUser(String permission, String username) {
        try {
            return ((EditGlobalPermissionsAdministrator)this.permissionsAdministrator).canSetPermissionOnUser(permission, FindUserHelper.getUserByUsername(username));
        }
        catch (EntityRuntimeException e) {
            log.error("Error determining whether a permission can be set on user: " + username, (Throwable)e);
            return false;
        }
    }

    public boolean canSetPermissionOnGroup(String permission, String groupName) {
        if (this.isConfluenceAdministratorsGroup(groupName)) {
            return false;
        }
        try {
            return ((EditGlobalPermissionsAdministrator)this.permissionsAdministrator).canSetPermissionOnGroup(permission, groupName);
        }
        catch (EntityRuntimeException e) {
            log.error("Error determining whether a permission can be set on group: " + groupName, (Throwable)e);
            return false;
        }
    }

    public boolean isConfluenceAdministratorsGroup(String groupName) {
        return "confluence-administrators".equals(groupName);
    }

    public boolean canSetSystemAdministratorPermission() {
        return ((EditGlobalPermissionsAdministrator)this.permissionsAdministrator).canSetSystemAdministratorPermission();
    }

    public boolean isAllowAnyLicensing() {
        return !DarkFeatures.isDarkFeatureEnabled("unified.usermanagement.licensing.disable");
    }

    public boolean isAllowPerGroupLicensing() {
        return this.isAllowAnyLicensing() && !DarkFeatures.isDarkFeatureEnabled("unified.usermanagement.licensing.groups.disable");
    }

    public boolean isAllowPerUserLicensing() {
        return this.isAllowAnyLicensing() && !DarkFeatures.isDarkFeatureEnabled("unified.usermanagement.licensing.users.disable");
    }

    @Override
    public String getGuardPermission() {
        return "USECONFLUENCE";
    }

    @Override
    public void populateAdministrator() {
        this.permissionsAdministrator = this.permissionsAdministratorBuilder.buildEditGlobalPermissionAdministrator(this.getAuthenticatedUser(), this.getUsersToAddAsList(), this.getGroupsToAddAsList());
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    private boolean isUnlicensedAuthenticatedCanUseSetBeforeMoreSpecificPermissions(Map requestParams) {
        Collection<SpacePermission> permissionsRequested = this.permissionsAdministrator.getRequestedPermissionsFromForm(requestParams);
        if (permissionsRequested.contains(UNLICENSED_AUTHENTICATED_CAN_USE_PERM)) {
            return true;
        }
        return !permissionsRequested.contains(UNLICENSED_AUTHENTICATED_CAN_BROWSE_USERS);
    }

    private boolean isAnonymousCanUseSetBeforeMoreSpecificPermissions(Map requestParams) {
        Collection<SpacePermission> permissionsRequested = this.permissionsAdministrator.getRequestedPermissionsFromForm(requestParams);
        if (permissionsRequested.contains(ANONYMOUS_CAN_USE_PERM)) {
            return true;
        }
        return !permissionsRequested.contains(ANONYMOUS_CAN_BROWSE_USERS_PERM);
    }
}

