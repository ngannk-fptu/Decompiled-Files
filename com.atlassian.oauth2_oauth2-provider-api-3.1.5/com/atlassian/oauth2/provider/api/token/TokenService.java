/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.token;

import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.sal.api.user.UserKey;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface TokenService {
    @Nonnull
    public AccessToken createAccessToken(String var1, String var2, String var3, long var4, Scope var6);

    @Nonnull
    public RefreshToken createRefreshToken(String var1, String var2, long var3, String var5, Scope var6, String var7, int var8);

    public void updateAccessTokenLastAccessed(String var1);

    @Nonnull
    public Optional<AccessToken> findByAccessTokenId(String var1);

    @Nonnull
    public Optional<RefreshToken> findByRefreshTokenId(String var1);

    @Nonnull
    public Optional<AccessToken> removeAccessTokenById(String var1);

    @Nonnull
    public List<RefreshToken> removeTokensByAuthorizationCode(String var1);

    public void removeByClientId(String var1);

    @Nonnull
    public List<AccessToken> removeByUserKey(String var1);

    public void removeExpiredAccessTokens(@Nonnull Duration var1);

    public void removeExpiredRefreshTokens(@Nonnull Duration var1);

    @Nonnull
    public List<String> findUserKeysByClientId(String var1);

    @Nonnull
    public List<RefreshToken> findRefreshTokensForClientId(String var1);

    public List<AccessToken> findAccessTokensByUserKey(@Nonnull UserKey var1);

    public List<RefreshToken> findRefreshTokensByUserKey(@Nonnull UserKey var1);

    public Optional<AccessToken> removeAccessTokenAssociatedWith(String var1);

    public Optional<RefreshToken> removeRefreshTokenAssociatedWith(String var1);

    public boolean isAccessTokenValid(@Nonnull String var1, @Nonnull String var2);

    public boolean isRefreshTokenValid(@Nonnull String var1, @Nonnull String var2);

    public Optional<RefreshToken> removeRefreshToken(String var1);

    public boolean isCodeRedeemed(String var1);

    public boolean removeTokensById(@Nonnull String var1);
}

