/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.event.client.ClientConfigurationCreatedEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientConfigurationDeletedEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientConfigurationUpdatedEvent
 *  com.atlassian.oauth2.provider.api.event.client.ClientSecretRefreshEvent
 *  com.atlassian.oauth2.provider.api.event.token.TokenCreatedEvent
 *  com.atlassian.oauth2.provider.api.event.token.TokenRevokedEvent
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 */
package com.atlassian.oauth2.provider.core.event;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationCreatedEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationDeletedEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationUpdatedEvent;
import com.atlassian.oauth2.provider.api.event.client.ClientSecretRefreshEvent;
import com.atlassian.oauth2.provider.api.event.token.TokenCreatedEvent;
import com.atlassian.oauth2.provider.api.event.token.TokenRevokedEvent;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;

public class OAuth2ProviderEventPublisher {
    private final EventPublisher eventPublisher;

    public OAuth2ProviderEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishTokenCreatedEvent(String clientId, RefreshToken refreshToken) {
        this.eventPublisher.publish((Object)new TokenCreatedEvent(clientId, refreshToken));
    }

    public void publishTokenRevokedEvent(String clientId, String userKey) {
        this.eventPublisher.publish((Object)new TokenRevokedEvent(clientId, userKey));
    }

    public void publishClientConfigurationCreatedEvent(Client client) {
        this.eventPublisher.publish((Object)new ClientConfigurationCreatedEvent(client));
    }

    public void publishClientConfigurationDeletedEvent(Client client) {
        this.eventPublisher.publish((Object)new ClientConfigurationDeletedEvent(client));
    }

    public void publishClientConfigurationUpdatedEvent(Client oldClient, Client newClient) {
        this.eventPublisher.publish((Object)new ClientConfigurationUpdatedEvent(oldClient, newClient));
    }

    public void publishClientSecretRefreshedEvent(Client client) {
        this.eventPublisher.publish((Object)new ClientSecretRefreshEvent(client));
    }
}

