/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.event.AuthenticationErrorEvent;
import org.springframework.vault.authentication.event.AuthenticationErrorListener;
import org.springframework.vault.authentication.event.AuthenticationEvent;
import org.springframework.vault.authentication.event.AuthenticationListener;

public abstract class AuthenticationEventPublisher {
    private final Set<AuthenticationListener> listeners = new CopyOnWriteArraySet<AuthenticationListener>();
    private final Set<AuthenticationErrorListener> errorListeners = new CopyOnWriteArraySet<AuthenticationErrorListener>();

    public void addAuthenticationListener(AuthenticationListener listener) {
        Assert.notNull((Object)listener, "AuthenticationEventListener must not be null");
        this.listeners.add(listener);
    }

    public void removeAuthenticationListener(AuthenticationListener listener) {
        this.listeners.remove(listener);
    }

    public void addErrorListener(AuthenticationErrorListener listener) {
        Assert.notNull((Object)listener, "AuthenticationEventErrorListener must not be null");
        this.errorListeners.add(listener);
    }

    public void removeErrorListener(AuthenticationErrorListener listener) {
        this.errorListeners.remove(listener);
    }

    void dispatch(AuthenticationEvent authenticationEvent) {
        for (AuthenticationListener listener : this.listeners) {
            listener.onAuthenticationEvent(authenticationEvent);
        }
    }

    void dispatch(AuthenticationErrorEvent authenticationEvent) {
        for (AuthenticationErrorListener listener : this.errorListeners) {
            listener.onAuthenticationError(authenticationEvent);
        }
    }
}

