/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionSaver;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@ParametersAreNonnullByDefault
@Transactional
public interface SpacePermissionManager
extends SpacePermissionSaver {
    @Deprecated
    public void removeAllPermissions(Space var1);

    @Deprecated
    public void removePermission(SpacePermission var1);

    public boolean hasPermission(String var1, @Nullable Space var2, @Nullable User var3);

    public boolean hasPermissionNoExemptions(String var1, @Nullable Space var2, @Nullable User var3);

    public boolean hasAllPermissions(List<String> var1, @Nullable Space var2, @Nullable User var3);

    @Deprecated
    public void removeAllUserPermissions(ConfluenceUser var1);

    @Deprecated
    public void removeGlobalPermissionForUser(ConfluenceUser var1, String var2);

    @Deprecated
    public void removeAllPermissionsForGroup(String var1);

    @Transactional(readOnly=true)
    public List<SpacePermission> getAllPermissionsForGroup(String var1);

    @Transactional(readOnly=true)
    public List<SpacePermission> getGlobalPermissions();

    @Transactional(readOnly=true)
    public List<SpacePermission> getGlobalPermissions(String var1);

    public void flushCaches();

    public boolean groupHasPermission(String var1, @Nullable Space var2, String var3);

    public void createDefaultSpacePermissions(Space var1);

    public void createPrivateSpacePermissions(Space var1);

    public boolean hasPermissionForSpace(@Nullable User var1, List var2, @Nullable Space var3);

    @Transactional(readOnly=true)
    public Collection<Group> getGroupsWithPermissions(@Nullable Space var1);

    @Transactional(readOnly=true)
    public Map<String, Long> getGroupsForPermissionType(String var1, Space var2);

    @Transactional(readOnly=true)
    public Collection<User> getUsersWithPermissions(@Nullable Space var1);

    @Transactional(readOnly=true)
    public Map<String, Long> getUsersForPermissionType(String var1, Space var2);

    public boolean permissionExists(SpacePermission var1);

    @Transactional(readOnly=true)
    public Set<SpacePermission> getDefaultGlobalPermissions();

    public boolean isPermittedInReadOnlyAccessMode(String var1);
}

