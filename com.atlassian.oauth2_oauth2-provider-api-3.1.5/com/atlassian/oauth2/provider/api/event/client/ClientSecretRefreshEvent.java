/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.event.client;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.provider.api.client.Client;
import lombok.NonNull;

@EventName(value="plugins.oauth2.provider.client.secret.refreshed")
public class ClientSecretRefreshEvent {
    @NonNull
    private final Client client;

    public ClientSecretRefreshEvent(@NonNull Client client) {
        if (client == null) {
            throw new NullPointerException("client is marked non-null but is null");
        }
        this.client = client;
    }

    @NonNull
    public Client getClient() {
        return this.client;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientSecretRefreshEvent)) {
            return false;
        }
        ClientSecretRefreshEvent other = (ClientSecretRefreshEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Client this$client = this.getClient();
        Client other$client = other.getClient();
        return !(this$client == null ? other$client != null : !this$client.equals(other$client));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClientSecretRefreshEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Client $client = this.getClient();
        result = result * 59 + ($client == null ? 43 : $client.hashCode());
        return result;
    }

    public String toString() {
        return "ClientSecretRefreshEvent(client=" + this.getClient() + ")";
    }
}

