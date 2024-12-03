/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.migration;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import com.atlassian.applinks.internal.migration.AuthenticationStatus;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import javax.annotation.Nonnull;

@Restricted(value={PermissionLevel.ADMIN})
@Internal
public interface AuthenticationMigrationService {
    @Restricted(value={PermissionLevel.ADMIN, PermissionLevel.SYSADMIN})
    @Nonnull
    public AuthenticationStatus migrateToOAuth(@Nonnull ApplicationId var1) throws ServiceException;

    public boolean hasRemoteSysAdminAccess(@Nonnull ApplicationLink var1) throws NoSuchApplinkException, NoAccessException;

    @Nonnull
    public AuthenticationStatus getAuthenticationMigrationStatus(@Nonnull ApplicationLink var1, @Nonnull ApplinkOAuthStatus var2) throws NoSuchApplinkException, NoAccessException;
}

