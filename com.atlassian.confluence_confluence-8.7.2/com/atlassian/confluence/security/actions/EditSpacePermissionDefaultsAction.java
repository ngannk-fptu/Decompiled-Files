/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.Preparable
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.security.ReadOnlySpacePermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionDefaultsPermissionChecker;
import com.atlassian.confluence.security.SpacePermissionDefaultsStore;
import com.atlassian.confluence.security.SpacePermissionDefaultsStoreFactory;
import com.atlassian.confluence.security.actions.AbstractEditPermissionAction;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.security.administrators.PermissionsAdministratorBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.user.ConfluenceUser;
import com.opensymphony.xwork2.Preparable;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditSpacePermissionDefaultsAction
extends AbstractEditPermissionAction
implements Preparable,
SpaceAware {
    private static final Logger log = LoggerFactory.getLogger(EditSpacePermissionDefaultsAction.class);
    private SpacePermissionDefaultsStore spacePermissionDefaultsStore;
    private Space space;

    @Override
    public PermissionsAdministrator getPermissionsAdministrator() {
        return this.permissionsAdministrator;
    }

    @Override
    public void populateAdministrator() {
        log.debug("Extracting template space from DefaultSpacePermissionStore [{}]", (Object)ObjectUtils.identityToString((Object)this.spacePermissionDefaultsStore));
        Space templateSpace = this.spacePermissionDefaultsStore.getTemplateSpace();
        log.debug("Template space passed to permissionsAdministrator builder is: [{}]", (Object)ObjectUtils.identityToString((Object)templateSpace));
        this.permissionsAdministrator = this.permissionsAdministratorBuilder.buildEditSpaceAdministrator(templateSpace, this.getAuthenticatedUser(), this.getUsersToAddAsList(), this.getGroupsToAddAsList());
    }

    @Override
    public String execute() throws Exception {
        log.debug("Extracting template space again from DefaultSpacePermissionStore [{}]", (Object)ObjectUtils.identityToString((Object)this.spacePermissionDefaultsStore));
        Space templateSpace = this.spacePermissionDefaultsStore.getTemplateSpace();
        log.debug("Template space whose permissions are about to be saved as default space permissions is: [" + ObjectUtils.identityToString((Object)templateSpace) + "].");
        String errorMessageKey = "error.cannot.remove.all.admin.permissions.for.space";
        String result = this.executeAction("error.cannot.remove.all.admin.permissions.for.space");
        log.debug("About to save default space permissions...");
        this.spacePermissionDefaultsStore.save();
        log.debug("Default space permissions saved.");
        return result;
    }

    public String grantPermission() {
        if (this.space == null) {
            return "error";
        }
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        SpacePermission viewSpacePermission = SpacePermission.createUserSpacePermission("VIEWSPACE", this.space, remoteUser);
        SpacePermission adminSpacePermission = SpacePermission.createUserSpacePermission("SETSPACEPERMISSIONS", this.space, remoteUser);
        this.space.addPermission(viewSpacePermission);
        this.space.addPermission(adminSpacePermission);
        this.spacePermissionManager.savePermission(viewSpacePermission);
        this.spacePermissionManager.savePermission(adminSpacePermission);
        log.warn("User {} has granted themselves space administrator permission in {} space", (Object)remoteUser.getFullName(), (Object)this.space.getName());
        return "success";
    }

    @Override
    public boolean isPermitted() {
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        return this.permissionManager.isConfluenceAdministrator(remoteUser);
    }

    @Override
    public String getGuardPermission() {
        return "VIEWSPACE";
    }

    public String getExistingGroups() {
        StringBuilder result = new StringBuilder();
        for (String group : this.spacePermissionDefaultsStore.getGroups()) {
            result.append(" ,").append(group);
        }
        return result.toString();
    }

    @Override
    public void setPermissionsAdministratorBuilder(PermissionsAdministratorBuilder permissionsAdministratorBuilder) {
        log.debug("No-op in setPermissionsAdministratorBuilder");
    }

    public void setSpacePermissionDefaultsStoreFactory(SpacePermissionDefaultsStoreFactory spacePermissionDefaultsStoreFactory) {
        this.spacePermissionDefaultsStore = spacePermissionDefaultsStoreFactory.createStore();
    }

    public void prepare() throws Exception {
        log.debug("Instantiated DefaultSpacePermissionStore [{}]", (Object)ObjectUtils.identityToString((Object)this.spacePermissionDefaultsStore));
        this.permissionsAdministratorBuilder = new PermissionsAdministratorBuilder();
        this.permissionsAdministratorBuilder.setSetSpacePermissionChecker(new SpacePermissionDefaultsPermissionChecker(this.permissionManager));
        this.permissionsAdministratorBuilder.setSpacePermissionManager(new ReadOnlySpacePermissionManager(this.spacePermissionManager));
        this.permissionsAdministratorBuilder.setUserResolver(this.userAccessor);
        this.permissionsAdministratorBuilder.setGroupResolver(this.userAccessor);
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public boolean isSpaceRequired() {
        return false;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return false;
    }
}

