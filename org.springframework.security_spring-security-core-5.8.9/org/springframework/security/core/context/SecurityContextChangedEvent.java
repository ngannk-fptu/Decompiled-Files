/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package org.springframework.security.core.context;

import java.util.function.Supplier;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextChangedEvent
extends ApplicationEvent {
    public static final Supplier<SecurityContext> NO_CONTEXT = () -> null;
    private final Supplier<SecurityContext> oldContext;
    private final Supplier<SecurityContext> newContext;

    public SecurityContextChangedEvent(Supplier<SecurityContext> oldContext, Supplier<SecurityContext> newContext) {
        super(SecurityContextHolder.class);
        this.oldContext = oldContext;
        this.newContext = newContext;
    }

    public SecurityContextChangedEvent(SecurityContext oldContext, SecurityContext newContext) {
        this(() -> oldContext, newContext != null ? () -> newContext : NO_CONTEXT);
    }

    public SecurityContext getOldContext() {
        return this.oldContext.get();
    }

    public SecurityContext getNewContext() {
        return this.newContext.get();
    }

    public boolean isCleared() {
        return this.newContext == NO_CONTEXT;
    }
}

