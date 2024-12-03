/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.actions.PermissionRow;
import com.atlassian.confluence.security.administrators.PermissionResolver;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.Group;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class DefaultPermissionsAdministrator
implements PermissionsAdministrator {
    protected final SpacePermissionManager spacePermissionManager;
    protected List<SpacePermission> permissions;
    private final PermissionResolver permissionResolver;
    private final UserAccessor userAccessor;

    protected DefaultPermissionsAdministrator(SpacePermissionManager spacePermissionManager, PermissionResolver permissionResolver, UserAccessor userAccessor) {
        this.spacePermissionManager = spacePermissionManager;
        this.permissionResolver = permissionResolver;
        this.userAccessor = userAccessor;
    }

    @Override
    public Collection<PermissionRow> buildUserPermissionTable() {
        return this.buildUserPermissionTable(this.userAccessor);
    }

    @Override
    public Collection<PermissionRow> buildGroupPermissionTable() {
        return this.buildGroupPermissionTable(this.userAccessor);
    }

    private Collection<PermissionRow> buildUserPermissionTable(UserAccessor userAccessor) {
        TreeMap<String, PermissionRow> userPermissionRows = new TreeMap<String, PermissionRow>();
        for (SpacePermission spacePermission : this.getPermissions()) {
            ConfluenceUser user = spacePermission.getUserSubject();
            if (user == null) continue;
            PermissionRow permissionRow = (PermissionRow)userPermissionRows.get(user.getName());
            if (permissionRow == null) {
                permissionRow = new UserPermissionRow(user);
                userPermissionRows.put(user.getName(), permissionRow);
            }
            permissionRow.addPermissionType(spacePermission);
        }
        return userPermissionRows.values();
    }

    private Collection<PermissionRow> buildGroupPermissionTable(UserAccessor userAccessor) {
        TreeMap<String, PermissionRow> groupPermissionRows = new TreeMap<String, PermissionRow>();
        for (SpacePermission spacePermission : this.getPermissions()) {
            if (!spacePermission.isGroupPermission()) continue;
            String groupName = spacePermission.getGroup();
            PermissionRow permissionRow = (PermissionRow)groupPermissionRows.get(groupName);
            if (permissionRow == null) {
                permissionRow = new GroupPermissionRow(groupName, userAccessor.getGroup(groupName));
                groupPermissionRows.put(groupName, permissionRow);
            }
            permissionRow.addPermissionType(spacePermission);
        }
        return groupPermissionRows.values();
    }

    @Override
    public PermissionRow buildUnlicensedAuthenticatedPermissionRow() {
        UnlicensedPermissionRow unlicensedAuthenticatedPermissionRow = UnlicensedPermissionRow.makeUnlicensedAuthenticatedUsersRow();
        for (SpacePermission spacePermission : this.getPermissions()) {
            if (!spacePermission.isAuthenticatedUsersPermission()) continue;
            unlicensedAuthenticatedPermissionRow.addPermissionType(spacePermission);
        }
        return unlicensedAuthenticatedPermissionRow;
    }

    @Override
    public PermissionRow buildAnonymousPermissionRow() {
        UnlicensedPermissionRow anonymousPermissionRow = UnlicensedPermissionRow.makeAnonymousRow();
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

    public void resetPermissions() {
        this.permissions = null;
    }

    public static class UnlicensedPermissionRow
    extends PermissionRow {
        private final String category;

        public static UnlicensedPermissionRow makeUnlicensedAuthenticatedUsersRow() {
            return new UnlicensedPermissionRow("authenticatedusers");
        }

        public static UnlicensedPermissionRow makeAnonymousRow() {
            return new UnlicensedPermissionRow("anonymous");
        }

        private UnlicensedPermissionRow(String category) {
            this.category = category;
        }

        @Override
        public String buildParameterName(String permissionType, String inputType) {
            StringBuilder result = new StringBuilder("confluence_").append(inputType).append("_");
            result.append(permissionType.toLowerCase(Locale.ENGLISH)).append("_");
            result.append(this.category);
            return result.toString();
        }

        @Override
        public boolean entityExists() {
            return true;
        }

        @Override
        public boolean isCaseInvalid() {
            return false;
        }
    }

    public static class GroupPermissionRow
    extends PermissionRow {
        private final String groupName;
        private final Group group;

        public GroupPermissionRow(String permissionGroupName, Group group) {
            this.groupName = permissionGroupName;
            this.group = group;
        }

        public String getGroupName() {
            return this.groupName;
        }

        @Override
        public String buildParameterName(String permissionType, String inputType) {
            StringBuilder result = new StringBuilder("confluence_").append(inputType).append("_");
            result.append(permissionType.toLowerCase(Locale.ENGLISH)).append("_");
            result.append("group_").append(this.groupName);
            return result.toString();
        }

        @Override
        public boolean entityExists() {
            return this.group != null;
        }

        @Override
        public boolean isCaseInvalid() {
            return this.group != null && !this.group.getName().equals(this.groupName);
        }

        public Group getGroup() {
            return this.group;
        }
    }

    public static class UserPermissionRow
    extends PermissionRow {
        private final ConfluenceUser user;

        public UserPermissionRow(ConfluenceUser user) {
            this.user = user;
        }

        public String getUsername() {
            return this.user.getName();
        }

        public String getUserKey() {
            return this.user.getKey().getStringValue();
        }

        @Override
        public String buildParameterName(String permissionType, String inputType) {
            return StringUtils.join((Object[])new String[]{"confluence_", inputType, "_", permissionType.toLowerCase(Locale.ENGLISH), "_", "user", "_", this.user.getKey().getStringValue()});
        }

        @Override
        public boolean entityExists() {
            return this.user != null;
        }

        @Override
        public boolean isCaseInvalid() {
            return false;
        }

        public ConfluenceUser getUser() {
            return this.user;
        }
    }
}

