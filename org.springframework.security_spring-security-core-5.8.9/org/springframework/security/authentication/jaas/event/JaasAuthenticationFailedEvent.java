/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas.event;

import org.springframework.security.authentication.jaas.event.JaasAuthenticationEvent;
import org.springframework.security.core.Authentication;

public class JaasAuthenticationFailedEvent
extends JaasAuthenticationEvent {
    private final Exception exception;

    public JaasAuthenticationFailedEvent(Authentication auth, Exception exception) {
        super(auth);
        this.exception = exception;
    }

    public Exception getException() {
        return this.exception;
    }
}

