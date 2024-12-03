/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.client.api.storage.event;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.Objects;

@EventName(value="plugins.oauth2.client.configuration.deleted")
public class ClientConfigurationDeletedEvent {
    private final String clientConfigurationId;

    public ClientConfigurationDeletedEvent(String clientConfigurationId) {
        this.clientConfigurationId = clientConfigurationId;
    }

    public String getClientConfigurationId() {
        return this.clientConfigurationId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientConfigurationDeletedEvent that = (ClientConfigurationDeletedEvent)o;
        return Objects.equals(this.clientConfigurationId, that.clientConfigurationId);
    }

    public int hashCode() {
        return Objects.hash(this.clientConfigurationId);
    }
}

