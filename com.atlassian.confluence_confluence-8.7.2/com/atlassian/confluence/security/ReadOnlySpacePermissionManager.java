/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.security;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class ReadOnlySpacePermissionManager
implements SpacePermissionManagerInternal {
    private final SpacePermissionManager spacePermissionManager;

    public ReadOnlySpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    @Override
    public void createDefaultSpacePermissions(Space space) {
    }

    @Override
    public void createPrivateSpacePermissions(Space space) {
    }

    @Override
    public void flushCaches() {
    }

    @Override
    public List<SpacePermission> getAllPermissionsForGroup(String arg0) {
        return this.spacePermissionManager.getAllPermissionsForGroup(arg0);
    }

    @Override
    public List<SpacePermission> getGlobalPermissions() {
        return this.spacePermissionManager.getGlobalPermissions();
    }

    @Override
    public List<SpacePermission> getGlobalPermissions(String arg0) {
        return this.spacePermissionManager.getGlobalPermissions(arg0);
    }

    @Override
    public Map<String, Long> getGroupsForPermissionType(String arg0, Space arg1) {
        return this.spacePermissionManager.getGroupsForPermissionType(arg0, arg1);
    }

    @Override
    public Collection<Group> getGroupsWithPermissions(@Nullable Space arg0) {
        return this.spacePermissionManager.getGroupsWithPermissions(arg0);
    }

    @Override
    public Map<String, Long> getUsersForPermissionType(String arg0, Space arg1) {
        return this.spacePermissionManager.getUsersForPermissionType(arg0, arg1);
    }

    @Override
    public Collection<User> getUsersWithPermissions(@Nullable Space arg0) {
        return this.spacePermissionManager.getUsersWithPermissions(arg0);
    }

    @Override
    public boolean groupHasPermission(String arg0, @Nullable Space arg1, String arg2) {
        return this.spacePermissionManager.groupHasPermission(arg0, arg1, arg2);
    }

    @Override
    public boolean hasPermission(String arg0, @Nullable Space arg1, @Nullable User arg2) {
        return this.spacePermissionManager.hasPermission(arg0, arg1, arg2);
    }

    @Override
    public boolean hasPermissionNoExemptions(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        return this.spacePermissionManager.hasPermissionNoExemptions(permissionType, space, remoteUser);
    }

    @Override
    public boolean hasAllPermissions(List<String> permissionTypes, @Nullable Space space, @Nullable User user) {
        return this.spacePermissionManager.hasAllPermissions(permissionTypes, space, user);
    }

    @Override
    public boolean hasPermissionForSpace(@Nullable User arg0, List arg1, @Nullable Space arg2) {
        return this.spacePermissionManager.hasPermissionForSpace(arg0, arg1, arg2);
    }

    @Override
    public boolean permissionExists(SpacePermission arg0) {
        return this.spacePermissionManager.permissionExists(arg0);
    }

    @Override
    public Set<SpacePermission> getDefaultGlobalPermissions() {
        return this.spacePermissionManager.getDefaultGlobalPermissions();
    }

    @Override
    public boolean isPermittedInReadOnlyAccessMode(String permissionType) {
        return this.spacePermissionManager.isPermittedInReadOnlyAccessMode(permissionType);
    }

    @Override
    @Deprecated
    public void removeAllPermissions(Space space) {
    }

    @Override
    public void removeAllPermissions(Space space, SpacePermissionContext context) {
    }

    @Override
    @Deprecated
    public void removeAllPermissionsForGroup(String group) {
    }

    @Override
    public void removeAllPermissionsForGroup(String group, SpacePermissionContext context) {
    }

    @Override
    @Deprecated
    public void removeAllUserPermissions(@NonNull ConfluenceUser user) {
    }

    @Override
    public void removeAllUserPermissions(ConfluenceUser user, SpacePermissionContext context) {
    }

    @Override
    @Deprecated
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType) {
    }

    @Override
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType, SpacePermissionContext context) {
    }

    @Override
    @Deprecated
    public void removePermission(SpacePermission permission) {
    }

    @Override
    public void removePermission(SpacePermission permission, SpacePermissionContext context) {
    }

    @Override
    @Deprecated
    public void savePermission(SpacePermission permission) {
    }

    @Override
    public void savePermission(SpacePermission permission, SpacePermissionContext context) {
    }
}

