/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.fugue.Either
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.access.SpacePermissionSubjectType;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.access.DefaultConfluenceAccessManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.fugue.Either;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public abstract class AbstractSpacePermissionManager
implements SpacePermissionManagerInternal,
DefaultConfluenceAccessManager.AccessManagerPermissionChecker {
    private final Logger log = LoggerFactory.getLogger(AbstractSpacePermissionManager.class);
    private final PermissionCheckExemptions permissionCheckExemptions;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final SpacePermissionAccessMapper spacePermissionAccessMapper;
    private final CrowdService crowdService;
    private final AccessModeManager accessModeManager;
    private final ScopesRequestCacheDelegate scopesRequestCacheDelegate;
    private final GlobalSettingsManager settingsManager;

    protected AbstractSpacePermissionManager(PermissionCheckExemptions permissionCheckExemptions, ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, CrowdService crowdService, AccessModeManager accessModeManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate, GlobalSettingsManager settingsManager) {
        this.confluenceAccessManager = confluenceAccessManager;
        this.spacePermissionAccessMapper = spacePermissionAccessMapper;
        this.permissionCheckExemptions = permissionCheckExemptions;
        this.crowdService = crowdService;
        this.accessModeManager = accessModeManager;
        this.scopesRequestCacheDelegate = scopesRequestCacheDelegate;
        this.settingsManager = settingsManager;
    }

    @Override
    public final boolean hasPermission(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        if (!this.isPermittedInReadOnlyAccessMode(permissionType)) {
            return false;
        }
        if (!this.scopesRequestCacheDelegate.hasPermission(permissionType, (Object)space)) {
            return false;
        }
        if (this.permissionCheckExemptions.isExempt(remoteUser)) {
            DebuggingString permissionCheck = this.getPermissionCheckAsString(permissionType, space, remoteUser);
            this.log.debug("{} User is exempt from permission checks (i.e. super-user). PERMISSION GRANTED.", (Object)permissionCheck);
            return true;
        }
        return this.hasPermissionNoExemptions(permissionType, space, remoteUser);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean hasPermissionNoExemptions(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        DebuggingString permissionCheck = this.getPermissionCheckAsString(permissionType, space, remoteUser);
        try (Ticker ignored = Timers.start((String)("DefaultSpacePermissionManager.hasPermissionNoExemptions" + permissionCheck));){
            ConfluenceUser remoteConfluenceUser;
            if (!this.isPermitted(this.isPermittedInReadOnlyAccessMode(permissionType), this.scopesRequestCacheDelegate.hasPermission(permissionType, (Object)space))) {
                boolean bl = false;
                return bl;
            }
            AccessStatus userAccessStatus = this.confluenceAccessManager.getUserAccessStatusNoExemptions(remoteUser);
            if (!userAccessStatus.canUseConfluence()) {
                this.log.debug("{} User does not have Confluence access. PERMISSION DENIED.", (Object)permissionCheck);
                boolean bl = false;
                return bl;
            }
            if (space == null && this.hasConfluenceAccessPermission(userAccessStatus, permissionType, permissionCheck)) {
                boolean bl = true;
                return bl;
            }
            Either<AccessDenied, Set<SpacePermissionSubjectType>> permissionCheckSubjectTypesEither = this.spacePermissionAccessMapper.getPermissionCheckSubjectTypes(userAccessStatus, permissionType);
            if (permissionCheckSubjectTypesEither.isLeft()) {
                this.log.debug("{} This type of permission cannot be granted for the current user's access status: {}", (Object)permissionCheck, (Object)userAccessStatus);
                boolean bl = false;
                return bl;
            }
            Set permissionCheckSubjectTypes = (Set)permissionCheckSubjectTypesEither.right().get();
            if (permissionCheckSubjectTypes.contains((Object)SpacePermissionSubjectType.ANONYMOUS)) {
                this.log.debug("{} Checking if anonymous category grants permission", (Object)permissionCheck);
                if (this.anonymousCategoryHasPermission(space, permissionType, permissionCheck)) {
                    this.log.debug("{} Anonymous permissions allow access. PERMISSION GRANTED.", (Object)permissionCheck);
                    boolean bl = true;
                    return bl;
                }
            }
            if (remoteUser == null) {
                this.log.debug("{} No remaining checks for anonymous user. PERMISSION DENIED.", (Object)permissionCheck);
                boolean bl = false;
                return bl;
            }
            if (permissionCheckSubjectTypes.contains((Object)SpacePermissionSubjectType.ALL_AUTHENTICATED_USERS)) {
                this.log.debug("{} Checking if all users category grants permission", (Object)permissionCheck);
                if (this.allAuthenticatedUsersHavePermission(space, permissionType, permissionCheck)) {
                    this.log.debug("{} Permission granted to all authenticated users. PERMISSION GRANTED.", (Object)permissionCheck);
                    boolean bl = true;
                    return bl;
                }
            }
            if (permissionCheckSubjectTypes.contains((Object)SpacePermissionSubjectType.GROUP)) {
                this.log.debug("{} Checking if groups grant permission", (Object)permissionCheck);
                if (this.hasPermissionViaGroups(remoteUser, space, permissionType)) {
                    this.log.debug("{} User is a member of a group with access to space. PERMISSION GRANTED.", (Object)permissionCheck);
                    boolean bl = true;
                    return bl;
                }
            }
            if ((remoteConfluenceUser = FindUserHelper.getUser(remoteUser)) == null) {
                this.log.warn("{} User was not found in ConfluenceUserDao - PERMISSION DENIED.", (Object)permissionCheck);
                boolean bl = false;
                return bl;
            }
            if (permissionCheckSubjectTypes.contains((Object)SpacePermissionSubjectType.USER)) {
                this.log.debug("{} Checking if user is directly assigned permission", (Object)permissionCheck);
                if (this.hasPermissionAsUser(remoteConfluenceUser, space, permissionType)) {
                    this.log.debug("{} User is directly assigned permission for space. PERMISSION GRANTED.", (Object)permissionCheck);
                    boolean bl = true;
                    return bl;
                }
            }
            if (userAccessStatus.hasLicensedAccess() && this.shouldCheckSiteAdminPermissionsForMissingSpace(space, permissionType) && this.hasConfluenceAdministratorPermission(remoteConfluenceUser)) {
                this.log.debug("{} User has Confluence administrator permission so is granted space-level permission type for missing space. PERMISSION GRANTED.", (Object)permissionCheck);
                boolean bl = true;
                return bl;
            }
            this.log.debug("{} No remaining checks. PERMISSION DENIED.", (Object)permissionCheck);
            boolean bl = false;
            return bl;
        }
        catch (Exception e) {
            this.log.error("Error checking permission {}. Denying access.", (Object)permissionCheck, (Object)e);
            return false;
        }
    }

    @Override
    public final boolean hasAllPermissions(List<String> permissionTypes, @Nullable Space space, @Nullable User user) {
        return permissionTypes.stream().allMatch(type -> this.hasPermission((String)type, space, user));
    }

    private DebuggingString getPermissionCheckAsString(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        if (!Timers.getConfiguration().isEnabled() && !this.log.isDebugEnabled()) {
            return DebuggingString.EMPTY_DEBUG_STR;
        }
        String permissionCheckDescription = MessageFormat.format("({0}, {1}, {2})", permissionType, remoteUser != null ? remoteUser.getName() : "anonymous", space != null ? space.getKey() : "global");
        return DebuggingString.of(permissionCheckDescription);
    }

    private boolean hasConfluenceAccessPermission(AccessStatus userAccessStatus, String permissionType, DebuggingString permissionCheck) {
        if ("USECONFLUENCE".equals(permissionType)) {
            if (userAccessStatus.hasLicensedAccess()) {
                this.log.debug("{} Authenticated user has USE_CONFLUENCE_PERMISSION. PERMISSION GRANTED.", (Object)permissionCheck);
                return true;
            }
            if (userAccessStatus.hasAnonymousAccess()) {
                this.log.debug("{} Anonymous user has USE_CONFLUENCE_PERMISSION. PERMISSION GRANTED.", (Object)permissionCheck);
                return true;
            }
        }
        if ("LIMITEDUSECONFLUENCE".equals(permissionType) && userAccessStatus.hasUnlicensedAuthenticatedAccess()) {
            this.log.debug("{} User has LIMITED_USE_CONFLUENCE_PERMISSION - limited authenticated access. PERMISSION GRANTED.", (Object)permissionCheck);
            return true;
        }
        return false;
    }

    private boolean anonymousCategoryHasPermission(@Nullable Space space, String permissionType, DebuggingString permissionCheck) {
        if (!SpacePermission.isValidAnonymousPermission(permissionType)) {
            this.log.debug("{} Permission is not valid for 'anonymous' category", (Object)permissionCheck);
            return false;
        }
        SpacePermission permission = this.shouldCheckGlobalPermissions(space, permissionType) ? SpacePermission.createAnonymousSpacePermission(permissionType, null) : SpacePermission.createAnonymousSpacePermission(permissionType, space);
        return this.permissionExists(permission);
    }

    private boolean allAuthenticatedUsersHavePermission(@Nullable Space space, String permissionType, DebuggingString permissionCheck) {
        if (!SpacePermission.isValidAuthenticatedUsersPermission(permissionType)) {
            this.log.debug("{} Permission is not valid for all 'all authenticated users' category", (Object)permissionCheck);
            return false;
        }
        SpacePermission permission = this.shouldCheckGlobalPermissions(space, permissionType) ? SpacePermission.createAuthenticatedUsersSpacePermission(permissionType, null) : SpacePermission.createAuthenticatedUsersSpacePermission(permissionType, space);
        return this.permissionExists(permission);
    }

    private boolean shouldCheckSiteAdminPermissionsForMissingSpace(@Nullable Space space, String permissionType) {
        return space == null && SpacePermission.GENERIC_SPACE_PERMISSIONS.contains(permissionType);
    }

    private boolean hasConfluenceAdministratorPermission(@NonNull ConfluenceUser remoteConfluenceUser) {
        if (this.hasPermissionViaGroups(remoteConfluenceUser, null, "ADMINISTRATECONFLUENCE")) {
            this.log.debug("User is a member of a group with Confluence administrative permission.");
            return true;
        }
        if (this.hasPermissionAsUser(remoteConfluenceUser, null, "ADMINISTRATECONFLUENCE")) {
            this.log.debug("User has been individually assigned Confluence administrative permission.");
            return true;
        }
        return false;
    }

    private boolean hasPermissionAsUser(@NonNull ConfluenceUser user, @Nullable Space space, String permissionType) {
        SpacePermission constructedPermission = this.shouldCheckGlobalPermissions(space, permissionType) ? SpacePermission.createUserSpacePermission(permissionType, null, user) : SpacePermission.createUserSpacePermission(permissionType, space, user);
        return this.permissionExists(constructedPermission);
    }

    @Override
    public final boolean hasGlobalPermissionViaGroups(@NonNull User user, String permissionType) {
        return this.hasPermissionViaGroups(user, null, permissionType);
    }

    private boolean hasPermissionViaGroups(@NonNull User user, @Nullable Space space, String permissionType) {
        boolean globalPermissionCheckRequired = this.shouldCheckGlobalPermissions(space, permissionType);
        Space targetSpace = globalPermissionCheckRequired ? null : space;
        String userName = user.getName();
        Iterable<String> groupsNamesWithPermission = this.getGroupNamesWithPermission(targetSpace, permissionType);
        for (String groupName : groupsNamesWithPermission) {
            if (!this.crowdService.isUserMemberOfGroup(userName, groupName)) continue;
            return true;
        }
        return false;
    }

    private boolean shouldCheckGlobalPermissions(@Nullable Space space, String permissionType) {
        return space == null || !SpacePermission.GENERIC_SPACE_PERMISSIONS.contains(permissionType);
    }

    @Override
    public boolean groupHasPermission(String permissionType, @Nullable Space space, String group) {
        SpacePermission constructedPermission = this.shouldCheckGlobalPermissions(space, permissionType) ? SpacePermission.createGroupSpacePermission(permissionType, null, group) : SpacePermission.createGroupSpacePermission(permissionType, space, group);
        return this.permissionExists(constructedPermission);
    }

    protected abstract Iterable<String> getGroupNamesWithPermission(@Nullable Space var1, String var2);

    @Override
    public final boolean hasPermissionForSpace(@Nullable User user, List permissionTypes, @Nullable Space space) {
        return this.hasAllPermissions(permissionTypes, space, user);
    }

    @Override
    public Set<SpacePermission> getDefaultGlobalPermissions() {
        String defaultUsersGroup = this.settingsManager.getGlobalSettings().getDefaultUsersGroup();
        ArrayList<SpacePermission> defaultPerms = new ArrayList<SpacePermission>();
        defaultPerms.add(SpacePermission.createGroupSpacePermission("USECONFLUENCE", null, defaultUsersGroup));
        defaultPerms.add(SpacePermission.createGroupSpacePermission("USECONFLUENCE", null, "confluence-administrators"));
        defaultPerms.add(SpacePermission.createGroupSpacePermission("PERSONALSPACE", null, defaultUsersGroup));
        defaultPerms.add(SpacePermission.createGroupSpacePermission("PERSONALSPACE", null, "confluence-administrators"));
        defaultPerms.add(SpacePermission.createGroupSpacePermission("ADMINISTRATECONFLUENCE", null, "confluence-administrators"));
        defaultPerms.add(SpacePermission.createGroupSpacePermission("SYSTEMADMINISTRATOR", null, "confluence-administrators"));
        defaultPerms.add(SpacePermission.createGroupSpacePermission("CREATESPACE", null, defaultUsersGroup));
        defaultPerms.add(SpacePermission.createGroupSpacePermission("CREATESPACE", null, "confluence-administrators"));
        return Collections.unmodifiableSet(new HashSet(defaultPerms));
    }

    @Override
    public boolean isPermittedInReadOnlyAccessMode(String permissionType) {
        return SpacePermission.READ_ONLY_SPACE_PERMISSIONS.contains(permissionType) || !this.accessModeManager.shouldEnforceReadOnlyAccess();
    }

    private boolean isPermitted(boolean ... conditionsToCheck) {
        for (boolean condition : conditionsToCheck) {
            if (condition) continue;
            return false;
        }
        return true;
    }

    private static class DebuggingString {
        static final DebuggingString EMPTY_DEBUG_STR = new DebuggingString("()");
        public final String value;

        private DebuggingString(String value) {
            this.value = value;
        }

        public static DebuggingString of(String value) {
            return new DebuggingString(value);
        }

        public String toString() {
            return this.value;
        }
    }
}

