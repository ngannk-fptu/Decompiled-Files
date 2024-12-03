/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.session;

import org.springframework.security.core.session.AbstractSessionEvent;

public abstract class SessionCreationEvent
extends AbstractSessionEvent {
    public SessionCreationEvent(Object source) {
        super(source);
    }
}

