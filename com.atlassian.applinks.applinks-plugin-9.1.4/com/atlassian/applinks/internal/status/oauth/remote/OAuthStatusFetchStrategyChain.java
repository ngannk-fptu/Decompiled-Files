/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthStatusFetchStrategy;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OAuthStatusFetchStrategyChain
implements OAuthStatusFetchStrategy {
    private final List<OAuthStatusFetchStrategy> strategies;

    public OAuthStatusFetchStrategyChain(@Nonnull List<OAuthStatusFetchStrategy> strategies) {
        this.strategies = ImmutableList.copyOf(strategies);
    }

    public OAuthStatusFetchStrategyChain(OAuthStatusFetchStrategy ... strategies) {
        this(Arrays.asList(strategies));
    }

    @Override
    @Nullable
    public ApplinkOAuthStatus fetch(@Nonnull ApplicationId localId, @Nonnull ApplicationLink applink) throws ApplinkStatusException, ResponseException {
        for (OAuthStatusFetchStrategy strategy : this.strategies) {
            ApplinkOAuthStatus result = strategy.fetch(localId, applink);
            if (result == null) continue;
            return result;
        }
        return null;
    }
}

