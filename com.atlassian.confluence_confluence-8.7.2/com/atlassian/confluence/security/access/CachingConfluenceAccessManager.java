/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.security.access;

import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.access.AbstractConfluenceAccessManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CachingConfluenceAccessManager
extends AbstractConfluenceAccessManager {
    private ConfluenceAccessManager delegate;

    public CachingConfluenceAccessManager(ConfluenceAccessManager delegate, PermissionCheckExemptions permissionCheckExemptions) {
        super(permissionCheckExemptions);
        this.delegate = delegate;
    }

    @Override
    public @NonNull AccessStatus getUserAccessStatusNoExemptions(@Nullable User user) {
        Option<AccessStatus> accessStatusOpt = ThreadLocalPermissionsCacheInternal.getUserAccessStatus(user);
        if (accessStatusOpt.isDefined()) {
            return (AccessStatus)accessStatusOpt.get();
        }
        AccessStatus computedAccessStatus = this.delegate.getUserAccessStatusNoExemptions(user);
        ThreadLocalPermissionsCacheInternal.cacheUserAccessStatus(user, computedAccessStatus);
        this.cacheCanUseConfluence(user, computedAccessStatus);
        return computedAccessStatus;
    }

    private void cacheCanUseConfluence(User user, AccessStatus accessStatus) {
        boolean legacyHasCanUseConfluencePermission = accessStatus.hasLicensedAccess() || accessStatus.hasAnonymousAccess();
        ThreadLocalPermissionsCacheInternal.cacheCanUseConfluence(user, legacyHasCanUseConfluencePermission);
    }
}

