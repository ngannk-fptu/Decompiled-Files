/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.google.common.collect.ImmutableSet
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security.access;

import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.access.SpacePermissionSubjectType;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSpacePermissionAccessMapper
implements SpacePermissionAccessMapper {
    private static final Logger log = LoggerFactory.getLogger(DefaultSpacePermissionAccessMapper.class);
    private static final Set<SpacePermissionSubjectType> LICENSED_ACCESS_SUBJ_TYPES = ImmutableSet.builder().add((Object)SpacePermissionSubjectType.GROUP).add((Object)SpacePermissionSubjectType.USER).add((Object)SpacePermissionSubjectType.ALL_AUTHENTICATED_USERS).add((Object)SpacePermissionSubjectType.ANONYMOUS).build();
    private static final Set<SpacePermissionSubjectType> UNLICENSED_AUTHENTICATED_ACCESS_SUBJ_TYPES = ImmutableSet.builder().add((Object)SpacePermissionSubjectType.ALL_AUTHENTICATED_USERS).build();
    private static final Set<SpacePermissionSubjectType> ANONYMOUS_ACCESS_SUBJ_TYPES = ImmutableSet.builder().add((Object)SpacePermissionSubjectType.ANONYMOUS).build();
    private static final Either<AccessDenied, Set<SpacePermissionSubjectType>> NO_ACCESS_RESULT = Either.left((Object)AccessDenied.INSTANCE);
    private static final Either<AccessDenied, Set<SpacePermissionSubjectType>> LICENSED_ACCESS_RESULT = Either.right(LICENSED_ACCESS_SUBJ_TYPES);
    private static final Either<AccessDenied, Set<SpacePermissionSubjectType>> UNLICENSED_AUTHENTICATED_ACCESS_RESULT = Either.right(UNLICENSED_AUTHENTICATED_ACCESS_SUBJ_TYPES);
    private static final Either<AccessDenied, Set<SpacePermissionSubjectType>> ANONYMOUS_ACCESS_RESULT = Either.right(ANONYMOUS_ACCESS_SUBJ_TYPES);

    private boolean canBeGrantedPermission(AccessStatus accessStatus, String permissionType) {
        if (accessStatus.hasLicensedAccess()) {
            return true;
        }
        if (accessStatus.hasUnlicensedAuthenticatedAccess()) {
            return SpacePermission.isValidAuthenticatedUsersPermission(permissionType);
        }
        if (accessStatus.hasAnonymousAccess()) {
            return SpacePermission.isValidAnonymousPermission(permissionType);
        }
        return false;
    }

    @Override
    public Either<AccessDenied, Set<SpacePermissionSubjectType>> getPermissionCheckSubjectTypes(@NonNull AccessStatus accessStatus, @NonNull String permissionType) {
        if (!this.canBeGrantedPermission(accessStatus, permissionType)) {
            log.debug("Permission type: {} is not valid for access status: {} - ACCESS DENIED", (Object)permissionType, (Object)accessStatus);
            return NO_ACCESS_RESULT;
        }
        if (accessStatus.hasLicensedAccess()) {
            log.debug("User has licensed access to Confluence - returning licensed space permission subject types");
            return LICENSED_ACCESS_RESULT;
        }
        if (accessStatus.hasUnlicensedAuthenticatedAccess()) {
            log.debug("User has unlicensed authenticated access to Confluence - returning unlicensed access space permission subject types");
            return UNLICENSED_AUTHENTICATED_ACCESS_RESULT;
        }
        if (accessStatus.hasAnonymousAccess()) {
            log.debug("User is anonymous and anonymous access to Confluence is enabled - returning anonymous space permission subject types");
            return ANONYMOUS_ACCESS_RESULT;
        }
        log.debug("User or anonymous does not have access to Confluence - ACCESS DENIED");
        return NO_ACCESS_RESULT;
    }
}

