/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package org.springframework.vault.authentication.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.vault.support.VaultToken;

public abstract class AuthenticationEvent
extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    protected AuthenticationEvent(VaultToken source) {
        super((Object)source);
    }

    public VaultToken getSource() {
        return (VaultToken)super.getSource();
    }
}

