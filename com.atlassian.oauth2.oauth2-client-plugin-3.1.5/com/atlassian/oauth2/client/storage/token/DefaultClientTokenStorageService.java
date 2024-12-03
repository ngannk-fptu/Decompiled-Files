/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.storage.event.ClientTokenCreatedEvent
 *  com.atlassian.oauth2.client.api.storage.event.ClientTokenDeletedEvent
 *  com.atlassian.oauth2.client.api.storage.event.ClientTokenUpdatedEvent
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.storage.token;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenCreatedEvent;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenDeletedEvent;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenUpdatedEvent;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import com.atlassian.oauth2.client.storage.token.dao.ClientTokenStore;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public class DefaultClientTokenStorageService
implements ClientTokenStorageService {
    private final ClientTokenStore clientTokenStore;
    private final EventPublisher eventPublisher;

    public DefaultClientTokenStorageService(ClientTokenStore clientTokenStore, EventPublisher eventPublisher) {
        this.clientTokenStore = clientTokenStore;
        this.eventPublisher = eventPublisher;
    }

    @Nonnull
    public ClientTokenEntity save(@Nonnull ClientTokenEntity clientToken) throws TokenNotFoundException {
        ClientTokenEntity savedToken;
        if (clientToken.getId() == null) {
            savedToken = this.clientTokenStore.create(clientToken);
            this.eventPublisher.publish((Object)new ClientTokenCreatedEvent(savedToken.getId()));
        } else {
            savedToken = this.clientTokenStore.update(clientToken);
            this.eventPublisher.publish((Object)new ClientTokenUpdatedEvent(savedToken.getId()));
        }
        return savedToken;
    }

    public void delete(@Nonnull String id) throws TokenNotFoundException {
        this.clientTokenStore.delete(id);
        this.eventPublisher.publish((Object)new ClientTokenDeletedEvent(id));
    }

    @Nonnull
    public Optional<ClientTokenEntity> getById(@Nonnull String id) {
        return Optional.ofNullable(this.clientTokenStore.getById(id));
    }

    @Nonnull
    public ClientTokenEntity getByIdOrFail(@Nonnull String id) throws TokenNotFoundException {
        return this.clientTokenStore.getByIdOrFail(id);
    }

    @Nonnull
    public List<ClientTokenEntity> getAccessTokensExpiringBefore(@Nonnull Instant timestamp) {
        return this.clientTokenStore.getAccessTokensExpiringBefore(timestamp);
    }

    @Nonnull
    public List<ClientTokenEntity> getRefreshTokensExpiringBefore(@Nonnull Instant timestamp) {
        return this.clientTokenStore.getRefreshTokensExpiringBefore(timestamp);
    }
}

