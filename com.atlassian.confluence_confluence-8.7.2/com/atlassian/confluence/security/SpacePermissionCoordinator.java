/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.impl.DefaultGroup
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.InsufficientPrivilegeException;
import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.security.EntityRuntimeException;
import com.atlassian.confluence.security.InvalidOperationException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultGroup;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class SpacePermissionCoordinator
implements SpacePermissionManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(SpacePermissionCoordinator.class);
    private PermissionManager permissionManager;
    private GroupManager groupManager;
    private SpacePermissionManagerInternal spacePermissionManager;
    private SetSpacePermissionChecker setSpacePermissionChecker;

    @Override
    @Deprecated
    public void savePermission(SpacePermission permission) {
        this.savePermission(permission, SpacePermissionContext.createDefault());
    }

    @Override
    public void savePermission(SpacePermission permission, SpacePermissionContext context) {
        if (!this.setSpacePermissionChecker.canSetPermission(AuthenticatedUserThreadLocal.get(), permission)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        this.spacePermissionManager.savePermission(permission, context);
    }

    @Override
    @Deprecated
    public void removeAllPermissions(Space space) {
        this.removeAllPermissions(space, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllPermissions(Space space, SpacePermissionContext context) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, space)) {
            this.logPermissionCheckFailure(space);
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        this.spacePermissionManager.removeAllPermissions(space, context);
    }

    private void logPermissionCheckFailure(Space space) {
        try {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            Pager groupsPager = user == null ? null : this.groupManager.getGroups((User)user);
            List list = groupsPager == null ? null : PagerUtils.toList((Pager)groupsPager);
            log.error("Permission check failed. User: {} is in groups {} and space permissions are {}", new Object[]{user, list, space.getPermissions()});
        }
        catch (EntityException e) {
            log.error("Things are even worse than we think: " + e.toString(), (Throwable)e);
        }
    }

    @Override
    @Deprecated
    public void removePermission(SpacePermission permission) {
        this.removePermission(permission, SpacePermissionContext.createDefault());
    }

    @Override
    public void removePermission(SpacePermission permission, SpacePermissionContext context) {
        if (!this.setSpacePermissionChecker.canSetPermission(AuthenticatedUserThreadLocal.get(), permission)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        this.spacePermissionManager.removePermission(permission, context);
    }

    @Override
    public void createDefaultSpacePermissions(Space space) {
        this.checkCanChangePermissions(space);
        this.spacePermissionManager.createDefaultSpacePermissions(space);
    }

    @Override
    public void createPrivateSpacePermissions(Space space) {
        this.checkCanChangePermissions(space);
        this.spacePermissionManager.createPrivateSpacePermissions(space);
    }

    private void checkCanChangePermissions(Space space) {
        boolean canChangePermissions;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        boolean bl = canChangePermissions = currentUser != null && (currentUser.getName().equals(space.getCreatorName()) || this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM));
        if (!canChangePermissions) {
            throw new InvalidOperationException("Must be the owner of the space or system administrator to create initial space permissions. User " + AuthenticatedUserThreadLocal.getUsername() + " tried to set permissions on space " + space.getKey() + " owned by " + space.getCreatorName());
        }
    }

    @Override
    public void removeAllUserPermissions(@NonNull ConfluenceUser user) {
        this.removeAllUserPermissions(user, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllUserPermissions(ConfluenceUser user, SpacePermissionContext context) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername(), "Failed to remove permissions of user with name: " + user.getName());
        }
        this.spacePermissionManager.removeAllUserPermissions(user, context);
    }

    @Override
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType) {
        this.removeGlobalPermissionForUser(user, permissionType, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType, SpacePermissionContext context) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername(), "Failed to remove permissions of user with name: " + user.getName());
        }
        this.spacePermissionManager.removeGlobalPermissionForUser(user, permissionType, context);
    }

    @Override
    public void removeAllPermissionsForGroup(String groupName) {
        this.removeAllPermissionsForGroup(groupName, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllPermissionsForGroup(String groupName, SpacePermissionContext context) {
        Group group = this.getGroup(groupName);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, group)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername(), "Failed to remove permissions of group with name: " + groupName);
        }
        this.spacePermissionManager.removeAllPermissionsForGroup(groupName, context);
    }

    private Group getGroup(String groupName) {
        Group group;
        try {
            group = this.groupManager.getGroup(groupName);
        }
        catch (EntityException e) {
            throw new EntityRuntimeException("Could not retrieve the group with name: " + groupName, e);
        }
        if (group == null) {
            log.warn("Group '" + groupName + "' could not be found. Continuing to attempt removal of permissions.");
            group = new DefaultGroup(groupName);
        }
        return group;
    }

    @Override
    public boolean hasPermission(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        return this.spacePermissionManager.hasPermission(permissionType, space, remoteUser);
    }

    @Override
    public boolean hasPermissionNoExemptions(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        return this.spacePermissionManager.hasPermissionNoExemptions(permissionType, space, remoteUser);
    }

    @Override
    public boolean hasAllPermissions(List<String> permissionTypes, @Nullable Space space, @Nullable User remoteUser) {
        return this.spacePermissionManager.hasAllPermissions(permissionTypes, space, remoteUser);
    }

    @Override
    public List<SpacePermission> getAllPermissionsForGroup(String group) {
        return this.spacePermissionManager.getAllPermissionsForGroup(group);
    }

    @Override
    public List<SpacePermission> getGlobalPermissions() {
        return this.spacePermissionManager.getGlobalPermissions();
    }

    @Override
    public List<SpacePermission> getGlobalPermissions(String permissionType) {
        return this.spacePermissionManager.getGlobalPermissions(permissionType);
    }

    @Override
    public void flushCaches() {
        this.spacePermissionManager.flushCaches();
    }

    @Override
    public boolean groupHasPermission(String permissionType, @Nullable Space space, String group) {
        return this.spacePermissionManager.groupHasPermission(permissionType, space, group);
    }

    @Override
    public boolean hasPermissionForSpace(@Nullable User user, List permissionTypes, @Nullable Space space) {
        return this.spacePermissionManager.hasPermissionForSpace(user, permissionTypes, space);
    }

    @Override
    public Collection<Group> getGroupsWithPermissions(@Nullable Space space) {
        return this.spacePermissionManager.getGroupsWithPermissions(space);
    }

    @Override
    public Map<String, Long> getGroupsForPermissionType(String permissionType, Space space) {
        return this.spacePermissionManager.getGroupsForPermissionType(permissionType, space);
    }

    @Override
    public Collection<User> getUsersWithPermissions(@Nullable Space space) {
        return this.spacePermissionManager.getUsersWithPermissions(space);
    }

    @Override
    public Map<String, Long> getUsersForPermissionType(String permissionType, Space space) {
        return this.spacePermissionManager.getUsersForPermissionType(permissionType, space);
    }

    @Override
    public boolean permissionExists(SpacePermission permission) {
        return this.spacePermissionManager.permissionExists(permission);
    }

    @Override
    public Set<SpacePermission> getDefaultGlobalPermissions() {
        return this.spacePermissionManager.getDefaultGlobalPermissions();
    }

    @Override
    public boolean isPermittedInReadOnlyAccessMode(String permissionType) {
        return this.spacePermissionManager.isPermittedInReadOnlyAccessMode(permissionType);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setSpacePermissionManager(SpacePermissionManagerInternal spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setSetSpacePermissionChecker(SetSpacePermissionChecker setSpacePermissionChecker) {
        this.setSpacePermissionChecker = setSpacePermissionChecker;
    }

    @Deprecated
    public void setUserManager(UserManager unused) {
    }

    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }
}

