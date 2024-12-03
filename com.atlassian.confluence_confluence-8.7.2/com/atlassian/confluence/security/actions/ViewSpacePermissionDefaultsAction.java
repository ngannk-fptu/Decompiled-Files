/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.security.SpacePermissionDefaultsStore;
import com.atlassian.confluence.security.SpacePermissionDefaultsStoreFactory;
import com.atlassian.confluence.security.actions.AbstractPermissionsAction;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.List;

public class ViewSpacePermissionDefaultsAction
extends AbstractPermissionsAction {
    private PermissionsAdministrator spacePermissionsAdministrator;
    private SpacePermissionDefaultsStore spacePermissionDefaultsStore;
    private SpaceManager spaceManager;
    private List<Space> spaces;

    @Override
    public PermissionsAdministrator getPermissionsAdministrator() {
        return this.spacePermissionsAdministrator;
    }

    @Override
    public void populateAdministrator() {
        this.spacePermissionsAdministrator = this.permissionsAdministratorBuilder.buildSpacePermissionAdministrator(this.spacePermissionDefaultsStore.getTemplateSpace());
    }

    @Override
    public String doDefault() {
        this.spaces = this.spaceManager.getAllSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        return "input";
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

    public void setSpacePermissionDefaultsStoreFactory(SpacePermissionDefaultsStoreFactory spacePermissionDefaultsStoreFactory) {
        this.spacePermissionDefaultsStore = spacePermissionDefaultsStoreFactory.createStore();
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public Collection<Space> getSpaces() {
        return this.spaces;
    }

    public boolean hasSpacePermission(Space space) {
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        return this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, remoteUser);
    }
}

