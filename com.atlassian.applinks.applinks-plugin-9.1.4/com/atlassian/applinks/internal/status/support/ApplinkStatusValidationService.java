/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.status.support;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.remote.RemoteVersionIncompatibleException;
import javax.annotation.Nonnull;

public interface ApplinkStatusValidationService {
    public void checkLocalCompatibility(@Nonnull ApplicationLink var1) throws ApplinkStatusException;

    @Restricted(value={PermissionLevel.ADMIN})
    public void checkVersionCompatibility(@Nonnull ApplicationLink var1) throws NoAccessException, RemoteVersionIncompatibleException;

    public void checkOAuthSupportedCompatibility(@Nonnull ApplinkOAuthStatus var1) throws ApplinkStatusException;

    public void checkOAuthMismatch(@Nonnull ApplinkOAuthStatus var1, @Nonnull ApplinkOAuthStatus var2) throws ApplinkStatusException;

    public void checkDisabled(@Nonnull ApplinkOAuthStatus var1, @Nonnull ApplinkOAuthStatus var2) throws ApplinkStatusException;

    @Restricted(value={PermissionLevel.ADMIN})
    public void checkLegacyAuthentication(@Nonnull ApplicationLink var1, @Nonnull ApplinkOAuthStatus var2, @Nonnull ApplinkOAuthStatus var3) throws NoSuchApplinkException, NoAccessException;

    @Restricted(value={PermissionLevel.ADMIN})
    public void checkEditable(@Nonnull ApplicationLink var1) throws NoAccessException, ApplinkStatusException;
}

