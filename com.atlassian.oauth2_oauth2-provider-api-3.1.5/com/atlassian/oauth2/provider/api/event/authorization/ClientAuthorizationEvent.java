/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.event.authorization;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.provider.api.authorization.Authorization;
import lombok.NonNull;

@EventName(value="plugins.oauth2.provider.client.authorization")
public class ClientAuthorizationEvent {
    @NonNull
    private final Authorization authorization;

    public String getScope() {
        return this.authorization.getScope().getName();
    }

    public ClientAuthorizationEvent(@NonNull Authorization authorization) {
        if (authorization == null) {
            throw new NullPointerException("authorization is marked non-null but is null");
        }
        this.authorization = authorization;
    }

    @NonNull
    public Authorization getAuthorization() {
        return this.authorization;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientAuthorizationEvent)) {
            return false;
        }
        ClientAuthorizationEvent other = (ClientAuthorizationEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Authorization this$authorization = this.getAuthorization();
        Authorization other$authorization = other.getAuthorization();
        return !(this$authorization == null ? other$authorization != null : !this$authorization.equals(other$authorization));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClientAuthorizationEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Authorization $authorization = this.getAuthorization();
        result = result * 59 + ($authorization == null ? 43 : $authorization.hashCode());
        return result;
    }

    public String toString() {
        return "ClientAuthorizationEvent(authorization=" + this.getAuthorization() + ")";
    }
}

