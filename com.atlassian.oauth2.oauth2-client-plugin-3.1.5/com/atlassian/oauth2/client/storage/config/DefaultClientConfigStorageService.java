/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.client.api.storage.event.ClientConfigurationCreatedEvent
 *  com.atlassian.oauth2.client.api.storage.event.ClientConfigurationDeletedEvent
 *  com.atlassian.oauth2.client.api.storage.event.ClientConfigurationUpdatedEvent
 *  com.atlassian.oauth2.client.api.storage.event.ClientTokenDeletedEvent
 *  com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.storage.config;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.event.ClientConfigurationCreatedEvent;
import com.atlassian.oauth2.client.api.storage.event.ClientConfigurationDeletedEvent;
import com.atlassian.oauth2.client.api.storage.event.ClientConfigurationUpdatedEvent;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenDeletedEvent;
import com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException;
import com.atlassian.oauth2.client.storage.config.dao.ClientConfigStore;
import com.atlassian.oauth2.client.storage.token.dao.ClientTokenStore;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultClientConfigStorageService
implements ClientConfigStorageService {
    private final ClientConfigStore clientConfigStore;
    private final ClientTokenStore clientTokenStore;
    private final EventPublisher eventPublisher;

    public DefaultClientConfigStorageService(ClientConfigStore clientConfigStore, ClientTokenStore clientTokenStore, EventPublisher eventPublisher) {
        this.clientConfigStore = clientConfigStore;
        this.clientTokenStore = clientTokenStore;
        this.eventPublisher = eventPublisher;
    }

    @Nonnull
    public ClientConfigurationEntity save(@Nonnull ClientConfigurationEntity clientConfiguration) throws ConfigurationNotFoundException {
        ClientConfigurationEntity savedEntity;
        if (clientConfiguration.getId() == null) {
            savedEntity = this.clientConfigStore.create(clientConfiguration);
            this.eventPublisher.publish((Object)new ClientConfigurationCreatedEvent(savedEntity.getId()));
        } else {
            savedEntity = this.clientConfigStore.update(clientConfiguration);
            this.eventPublisher.publish((Object)new ClientConfigurationUpdatedEvent(savedEntity.getId()));
        }
        return savedEntity;
    }

    public void delete(@Nonnull String id) throws ConfigurationNotFoundException {
        List<String> removedTokens = this.clientTokenStore.deleteWithConfigId(id);
        this.clientConfigStore.delete(id);
        this.eventPublisher.publish((Object)new ClientConfigurationDeletedEvent(id));
        removedTokens.forEach(token -> this.eventPublisher.publish((Object)new ClientTokenDeletedEvent(token)));
    }

    @Nonnull
    public Optional<ClientConfigurationEntity> getById(@Nonnull String id) {
        return Optional.ofNullable(this.clientConfigStore.getById(id));
    }

    @Nonnull
    public ClientConfigurationEntity getByIdOrFail(@Nonnull String id) throws ConfigurationNotFoundException {
        return this.clientConfigStore.getByIdOrFail(id);
    }

    public Optional<ClientConfigurationEntity> getByName(String configName) {
        return this.clientConfigStore.getByName(configName);
    }

    @Nonnull
    public List<ClientConfigurationEntity> list() {
        return this.clientConfigStore.list();
    }

    public boolean isNameUnique(@Nullable String id, @Nonnull String name) {
        return this.clientConfigStore.isNameUnique(id, name);
    }
}

