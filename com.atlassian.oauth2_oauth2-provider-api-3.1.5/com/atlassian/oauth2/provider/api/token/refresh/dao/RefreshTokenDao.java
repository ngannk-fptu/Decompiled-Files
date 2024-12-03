/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.token.refresh.dao;

import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface RefreshTokenDao {
    @Nonnull
    public RefreshToken create(RefreshToken var1);

    @Nonnull
    public Optional<RefreshToken> findByTokenId(String var1);

    @Nonnull
    public List<RefreshToken> findByClientId(String var1);

    @Nonnull
    public List<RefreshToken> findByUserKey(String var1);

    @Nonnull
    public List<RefreshToken> findByAuthorizationCode(String var1);

    @Nonnull
    public Optional<RefreshToken> removeByRefreshTokenId(String var1);

    public Optional<RefreshToken> removeByAccessTokenId(String var1);

    @Nonnull
    public List<RefreshToken> removeByClientId(String var1);

    public List<RefreshToken> removeAllByAuthorizationCode(String var1);

    public void removeExpiredTokensAfter(Duration var1);
}

