/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization;

import java.util.function.Supplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public final class SpringAuthorizationEventPublisher
implements AuthorizationEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public SpringAuthorizationEventPublisher(ApplicationEventPublisher eventPublisher) {
        Assert.notNull((Object)eventPublisher, (String)"eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    @Override
    public <T> void publishAuthorizationEvent(Supplier<Authentication> authentication, T object, AuthorizationDecision decision) {
        if (decision == null || decision.isGranted()) {
            return;
        }
        AuthorizationDeniedEvent<T> failure = new AuthorizationDeniedEvent<T>(authentication, object, decision);
        this.eventPublisher.publishEvent(failure);
    }
}

