/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.retention.manager;

import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.impl.retention.manager.GlobalRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.retention.RetentionPolicyPermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultRetentionPolicyPermissionManager
implements RetentionPolicyPermissionManager {
    private final PermissionManager permissionManager;
    private final GlobalRetentionPolicyManager globalRetentionPolicyManager;
    private final SpaceRetentionPolicyManager spaceRetentionPolicyManager;
    private final SpaceManagerInternal spaceManagerInternal;

    public DefaultRetentionPolicyPermissionManager(PermissionManager permissionManager, GlobalRetentionPolicyManager globalRetentionPolicyManager, SpaceRetentionPolicyManager spaceRetentionPolicyManager, SpaceManagerInternal spaceManagerInternal) {
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.globalRetentionPolicyManager = Objects.requireNonNull(globalRetentionPolicyManager);
        this.spaceRetentionPolicyManager = Objects.requireNonNull(spaceRetentionPolicyManager);
        this.spaceManagerInternal = Objects.requireNonNull(spaceManagerInternal);
    }

    @Override
    public boolean canViewGlobalPolicy(@Nullable ConfluenceUser user) {
        return this.permissionManager.isSystemAdministrator(user) || this.isAdminOfSomeSpace(user);
    }

    @Override
    public boolean canEditGlobalPolicy(@Nullable ConfluenceUser user) {
        return this.permissionManager.isSystemAdministrator(user);
    }

    @Override
    public boolean canViewSpacePolicy(@Nullable ConfluenceUser user, Space space) {
        Objects.requireNonNull(space);
        return this.permissionManager.isSystemAdministrator(user) || this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, space);
    }

    @Override
    public boolean canEditSpacePolicy(@Nullable ConfluenceUser user, Space space) {
        Objects.requireNonNull(space);
        if (this.permissionManager.isSystemAdministrator(user)) {
            return true;
        }
        if (this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, space)) {
            Optional<SpaceRetentionPolicy> spacePolicy = this.spaceRetentionPolicyManager.getPolicy(space.getKey());
            return spacePolicy.map(SpaceRetentionPolicy::getSpaceAdminCanEdit).orElseGet(() -> this.globalRetentionPolicyManager.getPolicy().getSpaceOverridesAllowed());
        }
        return false;
    }

    private boolean isAdminOfSomeSpace(ConfluenceUser user) {
        SpacesQuery spaceAdminQuery = SpacesQuery.newQuery().forUser(user).withPermission("SETSPACEPERMISSIONS").build();
        return this.spaceManagerInternal.getSpaces(spaceAdminQuery, LimitedRequestImpl.create((int)1), new Predicate[0]).size() > 0;
    }
}

