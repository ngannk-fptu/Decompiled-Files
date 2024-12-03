/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authorization.event;

import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.event.AuthorizationEvent;
import org.springframework.security.core.Authentication;

public class AuthorizationDeniedEvent<T>
extends AuthorizationEvent {
    public AuthorizationDeniedEvent(Supplier<Authentication> authentication, T object, AuthorizationDecision decision) {
        super(authentication, object, decision);
    }

    public T getObject() {
        return (T)this.getSource();
    }
}

