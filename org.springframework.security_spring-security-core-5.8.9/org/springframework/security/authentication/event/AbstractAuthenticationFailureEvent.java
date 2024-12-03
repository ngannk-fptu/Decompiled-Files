/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication.event;

import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

public abstract class AbstractAuthenticationFailureEvent
extends AbstractAuthenticationEvent {
    private final AuthenticationException exception;

    public AbstractAuthenticationFailureEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication);
        Assert.notNull((Object)exception, (String)"AuthenticationException is required");
        this.exception = exception;
    }

    public AuthenticationException getException() {
        return this.exception;
    }
}

