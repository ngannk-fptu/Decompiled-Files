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

@EventName(value="plugins.oauth2.provider.client.deleted")
public class ClientConfigurationDeletedEvent
extends ClientConfigurationEvent {
    public ClientConfigurationDeletedEvent(Client oldClient) {
        super(oldClient, null);
    }

    public String getScope() {
        return this.oldClient.getScope().getName();
    }
}

