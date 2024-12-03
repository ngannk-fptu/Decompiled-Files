/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication.event;

import org.springframework.vault.authentication.event.AuthenticationErrorEvent;

@FunctionalInterface
public interface AuthenticationErrorListener {
    public void onAuthenticationError(AuthenticationErrorEvent var1);
}

