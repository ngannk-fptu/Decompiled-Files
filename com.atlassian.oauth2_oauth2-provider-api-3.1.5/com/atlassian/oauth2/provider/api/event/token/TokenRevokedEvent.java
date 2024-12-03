/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.provider.api.event.token;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.provider.api.event.token.TokenEvent;

@EventName(value="plugins.oauth2.provider.token.revoked")
public class TokenRevokedEvent
extends TokenEvent {
    public TokenRevokedEvent(String clientId, String userKey) {
        super(clientId, userKey);
    }
}

