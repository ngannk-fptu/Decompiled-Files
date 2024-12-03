/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.confluence.impl.security.administrators;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.actions.PermissionRow;
import com.atlassian.confluence.security.administrators.DefaultPermissionsAdministrator;
import com.atlassian.confluence.security.administrators.PermissionResolver;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.GroupResolver;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DefaultPermissionsAdministrator
implements PermissionsAdministrator {
    private final PermissionResolver permissionResolver;
    private final GroupResolver groupResolver;

    public DefaultPermissionsAdministrator(PermissionResolver permissionResolver, GroupResolver groupResolver) {
        this.permissionResolver = Objects.requireNonNull(permissionResolver);
        this.groupResolver = Objects.requireNonNull(groupResolver);
    }

    @Override
    public Collection<PermissionRow> buildGroupPermissionTable() {
        return this.buildGroupPermissionTable(this.groupResolver);
    }

    @Override
    public Collection<PermissionRow> buildUserPermissionTable() {
        TreeMap<String, PermissionRow> userPermissionRows = new TreeMap<String, PermissionRow>();
        for (SpacePermission spacePermission : this.getPermissions()) {
            ConfluenceUser user = spacePermission.getUserSubject();
            if (user == null) continue;
            PermissionRow permissionRow = (PermissionRow)userPermissionRows.get(user.getName());
            if (permissionRow == null) {
                permissionRow = new DefaultPermissionsAdministrator.UserPermissionRow(user);
                userPermissionRows.put(user.getName(), permissionRow);
            }
            permissionRow.addPermissionType(spacePermission);
        }
        return userPermissionRows.values();
    }

    private Collection<PermissionRow> buildGroupPermissionTable(GroupResolver groupResolver) {
        TreeMap<String, PermissionRow> groupPermissionRows = new TreeMap<String, PermissionRow>();
        for (SpacePermission spacePermission : this.getPermissions()) {
            if (!spacePermission.isGroupPermission()) continue;
            String groupName = spacePermission.getGroup();
            PermissionRow permissionRow = (PermissionRow)groupPermissionRows.get(groupName);
            if (permissionRow == null) {
                permissionRow = new DefaultPermissionsAdministrator.GroupPermissionRow(groupName, groupResolver.getGroup(groupName));
                groupPermissionRows.put(groupName, permissionRow);
            }
            permissionRow.addPermissionType(spacePermission);
        }
        return groupPermissionRows.values();
    }

    @Override
    public PermissionRow buildUnlicensedAuthenticatedPermissionRow() {
        DefaultPermissionsAdministrator.UnlicensedPermissionRow unlicensedAuthenticatedPermissionRow = DefaultPermissionsAdministrator.UnlicensedPermissionRow.makeUnlicensedAuthenticatedUsersRow();
        for (SpacePermission spacePermission : this.getPermissions()) {
            if (!spacePermission.isAuthenticatedUsersPermission()) continue;
            unlicensedAuthenticatedPermissionRow.addPermissionType(spacePermission);
        }
        return unlicensedAuthenticatedPermissionRow;
    }

    @Override
    public PermissionRow buildAnonymousPermissionRow() {
        DefaultPermissionsAdministrator.UnlicensedPermissionRow anonymousPermissionRow = DefaultPermissionsAdministrator.UnlicensedPermissionRow.makeAnonymousRow();
        for (SpacePermission spacePermission : this.getPermissions()) {
            if (!spacePermission.isAnonymousPermission()) continue;
            anonymousPermissionRow.addPermissionType(spacePermission);
        }
        return anonymousPermissionRow;
    }

    @Override
    public List<SpacePermission> getPermissions() {
        return this.permissionResolver.getPermissions();
    }
}

