/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.provider.api.event.client;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.event.client.ClientConfigurationEvent;

@EventName(value="plugins.oauth2.provider.client.updated")
public class ClientConfigurationUpdatedEvent
extends ClientConfigurationEvent {
    public ClientConfigurationUpdatedEvent(Client oldClient, Client newClient) {
        super(oldClient, newClient);
    }

    public String getScope() {
        return this.newClient.getScope().getName();
    }
}

