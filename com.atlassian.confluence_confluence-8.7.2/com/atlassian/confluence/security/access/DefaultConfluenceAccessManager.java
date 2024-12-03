/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.access;

import com.atlassian.confluence.impl.security.recovery.RecoveryUtil;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.access.AbstractConfluenceAccessManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.AccessStatusImpl;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.DisabledUserManager;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.user.User;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfluenceAccessManager
extends AbstractConfluenceAccessManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultConfluenceAccessManager.class);
    private final DisabledUserManager disabledUserManager;
    private final Supplier<AccessManagerPermissionChecker> globalPermissionChecker;

    public DefaultConfluenceAccessManager(DisabledUserManager disabledUserManager, PermissionCheckExemptions permissionCheckExemptions, Supplier<AccessManagerPermissionChecker> globalPermissionChecker) {
        super(permissionCheckExemptions);
        this.disabledUserManager = disabledUserManager;
        this.globalPermissionChecker = globalPermissionChecker;
    }

    @Override
    public @NonNull AccessStatus getUserAccessStatusNoExemptions(@Nullable User user) {
        if (this.isDisabled(user)) {
            return AccessStatusImpl.NOT_PERMITTED;
        }
        if (user == null) {
            return this.getAccessStatusForAnonymous();
        }
        return this.getAccessStatusForLoggedInUser(user);
    }

    private boolean isDisabled(User user) {
        return this.disabledUserManager.isDisabled(user);
    }

    private AccessStatus getAccessStatusForAnonymous() {
        if (this.anonymousCanUseConfluence()) {
            log.debug("Anonymous user has USE permission because anonymous users have been granted this permission - ANONYMOUS_ACCESS");
            return AccessStatusImpl.ANONYMOUS_ACCESS;
        }
        log.debug("User is anonymous and USE Confluence permission not granted to anonymous users - NOT_PERMITTED");
        return AccessStatusImpl.NOT_PERMITTED;
    }

    private AccessStatus getAccessStatusForLoggedInUser(@NonNull User user) {
        if (RecoveryUtil.isRecoveryAdmin(user)) {
            return AccessStatusImpl.LICENSED_ACCESS;
        }
        if (this.getGlobalPermissionChecker().hasGlobalPermissionViaGroups(user, "USECONFLUENCE")) {
            log.debug("User {} has USE permission via group - LICENSED_ACCESS", (Object)user.getName());
            return AccessStatusImpl.LICENSED_ACCESS;
        }
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        if (confluenceUser == null) {
            log.debug("User {} was not found in ConfluenceUserDao - NOT_PERMITTED", (Object)user.getName());
            return AccessStatusImpl.NOT_PERMITTED;
        }
        if (this.hasGlobalPermissionAsUser(confluenceUser, "USECONFLUENCE")) {
            log.debug("User {} has USE permission directly as individual - LICENSED_ACCESS", (Object)user.getName());
            return AccessStatusImpl.LICENSED_ACCESS;
        }
        if (this.hasAuthenticatedUnlicensedConfluenceAccess(user)) {
            return AccessStatusImpl.UNLICENSED_AUTHENTICATED_ACCESS;
        }
        log.debug("User {} does not have access via groups or as individual user - NOT_PERMITTED", (Object)user.getName());
        return AccessStatusImpl.NOT_PERMITTED;
    }

    private boolean anonymousCanUseConfluence() {
        return this.hasGlobalPermissionAsUser(null, "USECONFLUENCE");
    }

    private boolean hasAuthenticatedUnlicensedConfluenceAccess(@NonNull User user) {
        SpacePermission constructedPermission = SpacePermission.createAuthenticatedUsersSpacePermission("LIMITEDUSECONFLUENCE", null);
        if (this.getGlobalPermissionChecker().permissionExists(constructedPermission)) {
            log.debug("User {} has limited access to Confluence because all authenticated users have been granted LIMITED_USE_CONFLUENCE_PERMISSION - UNLICENSED_AUTHENTICATED_ACCESS", (Object)user.getName());
            return true;
        }
        return false;
    }

    private boolean hasGlobalPermissionAsUser(@Nullable ConfluenceUser user, @NonNull String permissionType) {
        SpacePermission constructedPermission = user == null ? SpacePermission.createAnonymousSpacePermission(permissionType, null) : SpacePermission.createUserSpacePermission(permissionType, null, user);
        return this.getGlobalPermissionChecker().permissionExists(constructedPermission);
    }

    private AccessManagerPermissionChecker getGlobalPermissionChecker() {
        return this.globalPermissionChecker.get();
    }

    public static interface AccessManagerPermissionChecker {
        public boolean permissionExists(SpacePermission var1);

        public boolean hasGlobalPermissionViaGroups(@NonNull User var1, String var2);
    }
}

