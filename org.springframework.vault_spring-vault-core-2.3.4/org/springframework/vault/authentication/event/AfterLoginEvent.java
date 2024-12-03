/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication.event;

import org.springframework.vault.authentication.event.AuthenticationEvent;
import org.springframework.vault.support.VaultToken;

public class AfterLoginEvent
extends AuthenticationEvent {
    private static final long serialVersionUID = 1L;

    public AfterLoginEvent(VaultToken source) {
        super(source);
    }
}

