/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.event;

import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;

public class AuthenticationSuccessEvent
extends AbstractAuthenticationEvent {
    public AuthenticationSuccessEvent(Authentication authentication) {
        super(authentication);
    }
}

