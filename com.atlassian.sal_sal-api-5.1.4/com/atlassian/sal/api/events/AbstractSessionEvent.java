/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.events;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

public abstract class AbstractSessionEvent {
    private static final String SESSION_ID_NULL_MSG = "Session ID must be supplied";
    protected final String sessionId;
    protected final String userName;

    protected AbstractSessionEvent(String sessionId, String userName) {
        this.sessionId = (String)Preconditions.checkNotNull((Object)sessionId, (Object)SESSION_ID_NULL_MSG);
        this.userName = userName;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    @Nullable
    public String getUserName() {
        return this.userName;
    }

    public static abstract class Builder {
        protected String sessionId;
        protected String userName;

        protected Builder() {
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = (String)Preconditions.checkNotNull((Object)sessionId, (Object)AbstractSessionEvent.SESSION_ID_NULL_MSG);
            return this;
        }

        public Builder userName(@Nullable String userName) {
            this.userName = userName;
            return this;
        }

        public abstract AbstractSessionEvent build();
    }
}

