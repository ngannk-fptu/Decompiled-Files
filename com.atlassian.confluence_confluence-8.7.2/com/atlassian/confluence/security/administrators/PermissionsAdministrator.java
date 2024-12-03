/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.actions.PermissionRow;
import java.util.Collection;
import java.util.List;

public interface PermissionsAdministrator {
    public Collection<PermissionRow> buildUserPermissionTable();

    public Collection<PermissionRow> buildGroupPermissionTable();

    public PermissionRow buildUnlicensedAuthenticatedPermissionRow();

    public PermissionRow buildAnonymousPermissionRow();

    public List<SpacePermission> getPermissions();
}

