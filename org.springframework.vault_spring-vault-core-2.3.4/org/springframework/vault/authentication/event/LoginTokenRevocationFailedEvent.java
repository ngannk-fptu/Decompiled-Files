/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication.event;

import org.springframework.vault.authentication.event.AuthenticationErrorEvent;
import org.springframework.vault.support.VaultToken;

public class LoginTokenRevocationFailedEvent
extends AuthenticationErrorEvent {
    private static final long serialVersionUID = 1L;

    public LoginTokenRevocationFailedEvent(VaultToken source, Throwable exception) {
        super(source, exception);
    }

    public VaultToken getSource() {
        return (VaultToken)super.getSource();
    }
}

