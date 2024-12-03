/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.security.actions.PermissionRow;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import java.util.Collection;
import java.util.Map;

public interface PermissionsAware {
    public Collection<PermissionRow> getUserPermissionRows();

    public Collection<PermissionRow> getGroupPermissionRows();

    public PermissionRow getUnlicensedAuthenticatedPermissionRow();

    public PermissionRow getAnonymousPermissionRow();

    public String getGuardPermission();

    public Map getRequestParams();

    public PermissionsAdministrator getPermissionsAdministrator();

    public void populateAdministrator();
}

