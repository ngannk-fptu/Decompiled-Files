/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package org.springframework.security.authentication.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

public abstract class AbstractAuthenticationEvent
extends ApplicationEvent {
    public AbstractAuthenticationEvent(Authentication authentication) {
        super((Object)authentication);
    }

    public Authentication getAuthentication() {
        return (Authentication)super.getSource();
    }
}

