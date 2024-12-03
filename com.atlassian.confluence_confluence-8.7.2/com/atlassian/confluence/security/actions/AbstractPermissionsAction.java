/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.actions.beans.BootstrapAware;
import com.atlassian.confluence.security.PermissionUtils;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.actions.PermissionRow;
import com.atlassian.confluence.security.actions.PermissionsAware;
import com.atlassian.confluence.security.administrators.PermissionsAdministratorBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.struts2.ServletActionContext;

public abstract class AbstractPermissionsAction
extends ConfluenceActionSupport
implements PermissionsAware,
BootstrapAware {
    protected Collection<PermissionRow> userPermissionRows;
    protected Collection<PermissionRow> groupPermissionRows;
    private PermissionRow authenticatedPermissionsRow;
    protected PermissionRow anonymousPermissionRow;
    protected PermissionsAdministratorBuilder permissionsAdministratorBuilder;

    public void setPermissionsAdministratorBuilder(PermissionsAdministratorBuilder permissionsAdministratorBuilder) {
        this.permissionsAdministratorBuilder = permissionsAdministratorBuilder;
    }

    @Override
    public Collection<PermissionRow> getUserPermissionRows() {
        if (this.userPermissionRows == null) {
            this.userPermissionRows = this.getPermissionsAdministrator().buildUserPermissionTable();
        }
        return this.userPermissionRows;
    }

    @Override
    public Collection<PermissionRow> getGroupPermissionRows() {
        if (this.groupPermissionRows == null) {
            this.groupPermissionRows = this.getPermissionsAdministrator().buildGroupPermissionTable();
        }
        return this.groupPermissionRows;
    }

    @Override
    public PermissionRow getUnlicensedAuthenticatedPermissionRow() {
        if (this.authenticatedPermissionsRow == null) {
            this.authenticatedPermissionsRow = this.getPermissionsAdministrator().buildUnlicensedAuthenticatedPermissionRow();
        }
        return this.authenticatedPermissionsRow;
    }

    @Override
    public PermissionRow getAnonymousPermissionRow() {
        if (this.anonymousPermissionRow == null) {
            this.anonymousPermissionRow = this.getPermissionsAdministrator().buildAnonymousPermissionRow();
        }
        return this.anonymousPermissionRow;
    }

    public boolean isValidAnonymousPermission(String permissionType) {
        return SpacePermission.isValidAnonymousPermission(permissionType.toUpperCase());
    }

    public boolean isValidAuthenticatedUsersPermission(String permissionType) {
        return SpacePermission.isValidAuthenticatedUsersPermission(permissionType.toUpperCase());
    }

    @Override
    public Map getRequestParams() {
        return ServletActionContext.getRequest().getParameterMap();
    }

    @Override
    public void bootstrap() {
        this.populateAdministrator();
    }

    public boolean isAdminUser() {
        return PermissionUtils.isAdminUser(this.spacePermissionManager, this.getAuthenticatedUser());
    }

    public List<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<String>(Arrays.asList("useconfluence", "personalspace", "createspace", "administrateconfluence", "systemadministrator"));
        if (this.isUserStatusPluginEnabled()) {
            permissions.add(2, "updateuserstatus");
        }
        return permissions;
    }

    public boolean isShowGlobalUnlicensedUsersUI() {
        return this.spacePermissionManager.getGlobalPermissions().stream().anyMatch(SpacePermission::isAuthenticatedUsersPermission);
    }
}

