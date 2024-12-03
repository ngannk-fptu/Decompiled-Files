/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package org.springframework.security.authentication.jaas.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

public abstract class JaasAuthenticationEvent
extends ApplicationEvent {
    public JaasAuthenticationEvent(Authentication auth) {
        super((Object)auth);
    }

    public Authentication getAuthentication() {
        return (Authentication)this.source;
    }
}

