/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication.event;

import org.springframework.vault.authentication.event.AuthenticationErrorEvent;

public class LoginFailedEvent
extends AuthenticationErrorEvent {
    private static final long serialVersionUID = 1L;

    public LoginFailedEvent(Object source, Throwable exception) {
        super(source, exception);
    }
}

