/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import javax.annotation.Nonnull;

@Restricted(value={PermissionLevel.ADMIN})
public interface RemoteOAuthStatusService {
    @Nonnull
    public ApplinkOAuthStatus fetchOAuthStatus(@Nonnull ApplicationId var1) throws NoSuchApplinkException, ApplinkStatusException, NoAccessException;

    @Nonnull
    public ApplinkOAuthStatus fetchOAuthStatus(@Nonnull ApplicationLink var1) throws ApplinkStatusException, NoAccessException;
}

