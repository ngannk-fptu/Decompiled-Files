/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication.event;

import org.springframework.vault.authentication.event.AuthenticationEvent;

@FunctionalInterface
public interface AuthenticationListener {
    public void onAuthenticationEvent(AuthenticationEvent var1);
}

