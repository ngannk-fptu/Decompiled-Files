/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.pats.events.token;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.events.token.TokenEvent;
import java.time.Instant;

@EventName(value="personalaccesstoken.deleted")
public class TokenDeletedEvent
extends TokenEvent {
    public TokenDeletedEvent(TokenDTO token, Instant now, String triggeredBy) {
        super(token, now, triggeredBy);
    }
}

