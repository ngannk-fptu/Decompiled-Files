/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveAuthenticationManagerResolver<C> {
    public Mono<ReactiveAuthenticationManager> resolve(C var1);
}

