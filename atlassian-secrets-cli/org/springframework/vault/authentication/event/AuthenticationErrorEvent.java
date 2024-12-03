/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication.event;

import org.springframework.context.ApplicationEvent;

public class AuthenticationErrorEvent
extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    private final Throwable exception;

    public AuthenticationErrorEvent(Object source, Throwable exception) {
        super(source);
        this.exception = exception;
    }

    public Throwable getException() {
        return this.exception;
    }
}

