/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.status.oauth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.exception.ConsumerInformationUnavailableException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import javax.annotation.Nonnull;

public interface OAuthStatusService {
    @Nonnull
    @Unrestricted(value="Clients that know application link ID should be free to examine its status")
    public ApplinkOAuthStatus getOAuthStatus(@Nonnull ApplicationId var1) throws NoSuchApplinkException;

    @Nonnull
    @Unrestricted(value="Clients that know application link ID should be free to examine its status")
    public ApplinkOAuthStatus getOAuthStatus(@Nonnull ApplicationLink var1);

    @Restricted(value={PermissionLevel.ADMIN, PermissionLevel.SYSADMIN}, reason="2LOi can only be set by sysadmins")
    public void updateOAuthStatus(@Nonnull ApplicationId var1, @Nonnull ApplinkOAuthStatus var2) throws NoSuchApplinkException, NoAccessException, ConsumerInformationUnavailableException;

    @Restricted(value={PermissionLevel.ADMIN, PermissionLevel.SYSADMIN}, reason="2LOi can only be set by sysadmins")
    public void updateOAuthStatus(@Nonnull ApplicationLink var1, @Nonnull ApplinkOAuthStatus var2) throws NoAccessException, ConsumerInformationUnavailableException;
}

