/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authorization;

import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;

public interface AuthorizationEventPublisher {
    public <T> void publishAuthorizationEvent(Supplier<Authentication> var1, T var2, AuthorizationDecision var3);
}

