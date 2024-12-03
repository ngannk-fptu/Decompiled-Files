/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EditPermissionsAdministrator
extends PermissionsAdministrator {
    public static final Integer MAX_ENTRIES = 20;

    public boolean isGroupsToAddTooLarge(Map var1);

    public boolean isGroupsToAddEmpty(Map var1);

    public int getNumOfGroupEntries();

    public boolean isUsersToAddTooLarge(Map var1);

    public boolean isUsersToAddEmpty(Map var1);

    public int getNumOfUserEntries();

    public Collection<SpacePermission> buildPermissionsFromWebForm(Map var1, String var2);

    public void splitPermissions(Collection<SpacePermission> var1, Collection<SpacePermission> var2, Collection<SpacePermission> var3, Set<SpacePermission> var4, Set<SpacePermission> var5);

    public boolean isRemoveAllAdminPermissions(Set<SpacePermission> var1);

    public Collection<SpacePermission> getInitialPermissionsFromForm(Map var1);

    public Collection<SpacePermission> getRequestedPermissionsFromForm(Map var1);

    @Deprecated
    public void denyAnonymousPermissions(Collection<SpacePermission> var1, Set<SpacePermission> var2, Set<SpacePermission> var3);

    public void addAllPermissions(Set<SpacePermission> var1);

    public void addPermission(SpacePermission var1);

    public void removeAllPermissions(Set<SpacePermission> var1);

    public void removePermission(SpacePermission var1);

    public List<String> addGuardPermissionToGroups(List<String> var1, String var2);

    @Deprecated
    public List<String> addGuardPermissionToGroups(List<String> var1, UserAccessor var2, String var3);

    public List<String> addGuardPermissionToUsers(List<String> var1, String var2);

    @Deprecated
    public List<String> addGuardPermissionToUsers(List<String> var1, UserAccessor var2, String var3);

    public SpacePermission createUserGuardPermission(String var1, ConfluenceUser var2);

    public SpacePermission createGroupGuardPermission(String var1, String var2);

    public String getAdministrativePermissionType();

    public void applyPermissionChanges(Collection<SpacePermission> var1, Collection<SpacePermission> var2);
}

