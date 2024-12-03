/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public class AuthenticatedReactiveAuthorizationManager<T>
implements ReactiveAuthorizationManager<T> {
    private AuthenticationTrustResolver authTrustResolver = new AuthenticationTrustResolverImpl();

    AuthenticatedReactiveAuthorizationManager() {
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, T object) {
        return authentication.filter(this::isNotAnonymous).map(this::getAuthorizationDecision).defaultIfEmpty((Object)new AuthorizationDecision(false));
    }

    private AuthorizationDecision getAuthorizationDecision(Authentication authentication) {
        return new AuthorizationDecision(authentication.isAuthenticated());
    }

    private boolean isNotAnonymous(Authentication authentication) {
        return !this.authTrustResolver.isAnonymous(authentication);
    }

    public static <T> AuthenticatedReactiveAuthorizationManager<T> authenticated() {
        return new AuthenticatedReactiveAuthorizationManager<T>();
    }
}

