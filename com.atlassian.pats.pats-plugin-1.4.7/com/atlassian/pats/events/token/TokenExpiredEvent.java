/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.events.token;

import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.events.token.TokenEvent;
import java.time.Instant;

public class TokenExpiredEvent
extends TokenEvent {
    public TokenExpiredEvent(TokenDTO token, Instant now, String triggeredBy) {
        super(token, now, triggeredBy);
    }
}

