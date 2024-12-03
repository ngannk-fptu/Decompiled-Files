/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.provider.api.event.token;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.provider.api.event.token.TokenEvent;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;

@EventName(value="plugins.oauth2.provider.token.created")
public class TokenCreatedEvent
extends TokenEvent {
    private final RefreshToken refreshToken;

    public TokenCreatedEvent(String clientId, RefreshToken refreshToken) {
        super(clientId, refreshToken.getUserKey());
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return this.refreshToken.getScope().getName();
    }

    public int getRefreshCount() {
        return this.refreshToken.getRefreshCount();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenCreatedEvent)) {
            return false;
        }
        TokenCreatedEvent other = (TokenCreatedEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        RefreshToken this$refreshToken = this.refreshToken;
        RefreshToken other$refreshToken = other.refreshToken;
        return !(this$refreshToken == null ? other$refreshToken != null : !this$refreshToken.equals(other$refreshToken));
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof TokenCreatedEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        RefreshToken $refreshToken = this.refreshToken;
        result = result * 59 + ($refreshToken == null ? 43 : $refreshToken.hashCode());
        return result;
    }
}

