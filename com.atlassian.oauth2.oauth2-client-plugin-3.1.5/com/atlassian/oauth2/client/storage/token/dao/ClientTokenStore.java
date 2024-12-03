/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.storage.token.dao;

import com.atlassian.annotations.Internal;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public interface ClientTokenStore {
    @Nonnull
    public ClientTokenEntity create(@Nonnull ClientTokenEntity var1);

    @Nonnull
    public ClientTokenEntity update(@Nonnull ClientTokenEntity var1) throws TokenNotFoundException;

    public void delete(@Nonnull String var1) throws TokenNotFoundException;

    public List<String> deleteWithConfigId(@Nonnull String var1);

    public List<String> deleteTokensExpiringBefore(@Nonnull Instant var1);

    public List<String> deleteTokensUnrecoverableSince(@Nonnull Instant var1);

    @Nullable
    public ClientTokenEntity getById(@Nonnull String var1);

    @Nonnull
    public ClientTokenEntity getByIdOrFail(@Nonnull String var1) throws TokenNotFoundException;

    @Nonnull
    public List<ClientTokenEntity> getAccessTokensExpiringBefore(@Nonnull Instant var1);

    @Nonnull
    public List<ClientTokenEntity> getRefreshTokensExpiringBefore(@Nonnull Instant var1);

    @Nonnull
    public List<ClientTokenEntity> list();
}

