/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.sal.api.events;

import com.atlassian.sal.api.events.AbstractSessionEvent;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SessionDestroyedEvent
extends AbstractSessionEvent {
    private SessionDestroyedEvent(String sessionId, String userName) {
        super(sessionId, userName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends AbstractSessionEvent.Builder {
        private Builder() {
        }

        @Override
        public SessionDestroyedEvent build() {
            return new SessionDestroyedEvent(this.sessionId, this.userName);
        }
    }
}

