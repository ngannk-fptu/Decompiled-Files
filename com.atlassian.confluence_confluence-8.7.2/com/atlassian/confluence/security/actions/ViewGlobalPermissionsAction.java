/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.admin.GlobalPermissionsViewEvent;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.actions.AbstractPermissionsAction;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.user.User;

public class ViewGlobalPermissionsAction
extends AbstractPermissionsAction
implements Evented<GlobalPermissionsViewEvent> {
    private PermissionsAdministrator globalPermissionsAdministrator;

    @Override
    public GlobalPermissionsViewEvent getEventToPublish(String result) {
        return new GlobalPermissionsViewEvent(this);
    }

    @Override
    public PermissionsAdministrator getPermissionsAdministrator() {
        return this.globalPermissionsAdministrator;
    }

    @Override
    public void populateAdministrator() {
        this.globalPermissionsAdministrator = this.permissionsAdministratorBuilder.buildGlobalPermissionAdministrator();
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @Override
    public String getGuardPermission() {
        return "USECONFLUENCE";
    }
}

