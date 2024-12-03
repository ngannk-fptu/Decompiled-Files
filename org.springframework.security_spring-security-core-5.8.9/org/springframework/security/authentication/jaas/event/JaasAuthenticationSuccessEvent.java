/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas.event;

import org.springframework.security.authentication.jaas.event.JaasAuthenticationEvent;
import org.springframework.security.core.Authentication;

public class JaasAuthenticationSuccessEvent
extends JaasAuthenticationEvent {
    public JaasAuthenticationSuccessEvent(Authentication auth) {
        super(auth);
    }
}

