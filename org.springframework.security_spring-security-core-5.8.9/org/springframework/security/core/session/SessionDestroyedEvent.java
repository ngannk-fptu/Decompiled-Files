/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.session;

import java.util.List;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.AbstractSessionEvent;

public abstract class SessionDestroyedEvent
extends AbstractSessionEvent {
    public SessionDestroyedEvent(Object source) {
        super(source);
    }

    public abstract List<SecurityContext> getSecurityContexts();

    public abstract String getId();
}

