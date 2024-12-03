/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.pats.events;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.events.token.TokenCreatedEvent;
import com.atlassian.pats.events.token.TokenDeletedEvent;
import com.atlassian.pats.events.token.TokenExpireSoonEvent;
import com.atlassian.pats.events.token.TokenExpiredEvent;
import com.atlassian.pats.events.token.TokenSummaryEvent;
import java.time.Clock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokenEventPublisher {
    private final EventPublisher eventPublisher;
    private final Clock utcClock;

    public TokenEventPublisher(EventPublisher eventPublisher, Clock utcClock) {
        this.eventPublisher = eventPublisher;
        this.utcClock = utcClock;
    }

    public void tokenDeletedEvent(@Nonnull TokenDTO token, @Nullable String triggeredBy) {
        this.eventPublisher.publish((Object)new TokenDeletedEvent(token, this.utcClock.instant(), triggeredBy));
    }

    public void tokenCreatedEvent(@Nonnull TokenDTO token, @Nullable String triggeredBy) {
        this.eventPublisher.publish((Object)new TokenCreatedEvent(token, this.utcClock.instant(), triggeredBy));
    }

    public void tokenExpireSoonEvent(@Nonnull TokenDTO token, @Nullable String triggeredBy) {
        this.eventPublisher.publish((Object)new TokenExpireSoonEvent(token, this.utcClock.instant(), triggeredBy));
    }

    public void tokenExpiredEvent(@Nonnull TokenDTO token, @Nullable String triggeredBy) {
        this.eventPublisher.publish((Object)new TokenExpiredEvent(token, this.utcClock.instant(), triggeredBy));
    }

    public void tokenSummaryEvent(long tokenTotal) {
        this.eventPublisher.publish((Object)new TokenSummaryEvent(tokenTotal));
    }
}

