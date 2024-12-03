/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.api.storage.token;

import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface ClientTokenStorageService {
    @Nonnull
    public ClientTokenEntity save(@Nonnull ClientTokenEntity var1) throws TokenNotFoundException;

    public void delete(@Nonnull String var1) throws TokenNotFoundException;

    @Nonnull
    public Optional<ClientTokenEntity> getById(@Nonnull String var1);

    @Nonnull
    public ClientTokenEntity getByIdOrFail(@Nonnull String var1) throws TokenNotFoundException;

    @Nonnull
    public List<ClientTokenEntity> getAccessTokensExpiringBefore(@Nonnull Instant var1);

    @Nonnull
    public List<ClientTokenEntity> getRefreshTokensExpiringBefore(@Nonnull Instant var1);
}

