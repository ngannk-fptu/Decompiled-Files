/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface ReactiveAuthorizationManager<T> {
    public Mono<AuthorizationDecision> check(Mono<Authentication> var1, T var2);

    default public Mono<Void> verify(Mono<Authentication> authentication, T object) {
        return this.check(authentication, object).filter(AuthorizationDecision::isGranted).switchIfEmpty(Mono.defer(() -> Mono.error((Throwable)new AccessDeniedException("Access Denied")))).flatMap(decision -> Mono.empty());
    }
}

