/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.event.client;

import com.atlassian.oauth2.provider.api.client.Client;

public class ClientConfigurationEvent {
    protected final Client oldClient;
    protected final Client newClient;

    public ClientConfigurationEvent(Client oldClient, Client newClient) {
        this.oldClient = oldClient;
        this.newClient = newClient;
    }

    public Client getOldClient() {
        return this.oldClient;
    }

    public Client getNewClient() {
        return this.newClient;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientConfigurationEvent)) {
            return false;
        }
        ClientConfigurationEvent other = (ClientConfigurationEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Client this$oldClient = this.getOldClient();
        Client other$oldClient = other.getOldClient();
        if (this$oldClient == null ? other$oldClient != null : !this$oldClient.equals(other$oldClient)) {
            return false;
        }
        Client this$newClient = this.getNewClient();
        Client other$newClient = other.getNewClient();
        return !(this$newClient == null ? other$newClient != null : !this$newClient.equals(other$newClient));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClientConfigurationEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Client $oldClient = this.getOldClient();
        result = result * 59 + ($oldClient == null ? 43 : $oldClient.hashCode());
        Client $newClient = this.getNewClient();
        result = result * 59 + ($newClient == null ? 43 : $newClient.hashCode());
        return result;
    }

    public String toString() {
        return "ClientConfigurationEvent(oldClient=" + this.getOldClient() + ", newClient=" + this.getNewClient() + ")";
    }
}

