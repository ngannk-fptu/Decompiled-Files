/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.client.api.storage.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenEvent;
import java.util.Objects;

@EventName(value="plugins.oauth2.client.token.unrecoverable")
public class ClientTokenUnrecoverableEvent
implements ClientTokenEvent {
    private final String clientTokenId;

    public ClientTokenUnrecoverableEvent(String clientTokenId) {
        this.clientTokenId = clientTokenId;
    }

    @Override
    public String getClientTokenId() {
        return this.clientTokenId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientTokenUnrecoverableEvent that = (ClientTokenUnrecoverableEvent)o;
        return Objects.equals(this.clientTokenId, that.clientTokenId);
    }

    public int hashCode() {
        return Objects.hash(this.clientTokenId);
    }
}

