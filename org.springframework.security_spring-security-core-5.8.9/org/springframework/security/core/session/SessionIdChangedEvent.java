/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.session;

import org.springframework.security.core.session.AbstractSessionEvent;

public abstract class SessionIdChangedEvent
extends AbstractSessionEvent {
    public SessionIdChangedEvent(Object source) {
        super(source);
    }

    public abstract String getOldSessionId();

    public abstract String getNewSessionId();
}

