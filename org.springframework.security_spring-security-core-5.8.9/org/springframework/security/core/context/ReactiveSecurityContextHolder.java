/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 *  reactor.util.context.Context
 */
package org.springframework.security.core.context;

import java.util.function.Function;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public final class ReactiveSecurityContextHolder {
    private static final Class<?> SECURITY_CONTEXT_KEY = SecurityContext.class;

    private ReactiveSecurityContextHolder() {
    }

    public static Mono<SecurityContext> getContext() {
        return Mono.subscriberContext().filter(ReactiveSecurityContextHolder::hasSecurityContext).flatMap(ReactiveSecurityContextHolder::getSecurityContext);
    }

    private static boolean hasSecurityContext(Context context) {
        return context.hasKey(SECURITY_CONTEXT_KEY);
    }

    private static Mono<SecurityContext> getSecurityContext(Context context) {
        return (Mono)context.get(SECURITY_CONTEXT_KEY);
    }

    public static Function<Context, Context> clearContext() {
        return context -> context.delete(SECURITY_CONTEXT_KEY);
    }

    public static Context withSecurityContext(Mono<? extends SecurityContext> securityContext) {
        return Context.of(SECURITY_CONTEXT_KEY, securityContext);
    }

    public static Context withAuthentication(Authentication authentication) {
        return ReactiveSecurityContextHolder.withSecurityContext((Mono<? extends SecurityContext>)Mono.just((Object)new SecurityContextImpl(authentication)));
    }
}

