/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.token.access.dao;

import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface AccessTokenDao {
    @Nonnull
    public AccessToken create(AccessToken var1);

    public void updateTokenLastAccessed(String var1);

    @Nonnull
    public Collection<AccessToken> findByClientId(String var1);

    @Nonnull
    public Optional<AccessToken> findByToken(String var1);

    @Nonnull
    public Optional<AccessToken> removeTokenById(String var1);

    @Nonnull
    public List<AccessToken> removeAllByClientId(String var1);

    @Nonnull
    public List<AccessToken> removeAllByUserKey(String var1);

    public void removeExpiredTokensAfter(Duration var1);

    @Nonnull
    public List<String> findUserKeysByClientId(String var1);

    @Nonnull
    public List<AccessToken> findByUserKey(String var1);
}

