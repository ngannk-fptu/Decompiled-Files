/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.event.token;

import lombok.NonNull;

public abstract class TokenEvent {
    @NonNull
    private final String clientId;
    private final String userKey;

    public TokenEvent(@NonNull String clientId, String userKey) {
        if (clientId == null) {
            throw new NullPointerException("clientId is marked non-null but is null");
        }
        this.clientId = clientId;
        this.userKey = userKey;
    }

    @NonNull
    public String getClientId() {
        return this.clientId;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenEvent)) {
            return false;
        }
        TokenEvent other = (TokenEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$clientId = this.getClientId();
        String other$clientId = other.getClientId();
        if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        return !(this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TokenEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        return result;
    }

    public String toString() {
        return "TokenEvent(clientId=" + this.getClientId() + ", userKey=" + this.getUserKey() + ")";
    }
}

