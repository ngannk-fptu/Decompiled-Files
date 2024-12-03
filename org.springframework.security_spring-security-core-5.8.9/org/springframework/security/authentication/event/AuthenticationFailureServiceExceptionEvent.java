/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.event;

import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailureServiceExceptionEvent
extends AbstractAuthenticationFailureEvent {
    public AuthenticationFailureServiceExceptionEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}

