/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.sal.api.net.ResponseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface OAuthStatusFetchStrategy {
    @Nullable
    public ApplinkOAuthStatus fetch(@Nonnull ApplicationId var1, @Nonnull ApplicationLink var2) throws ApplinkStatusException, ResponseException;
}

