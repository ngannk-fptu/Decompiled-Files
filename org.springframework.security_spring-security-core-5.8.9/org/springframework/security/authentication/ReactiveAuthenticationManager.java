/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveAuthenticationManager {
    public Mono<Authentication> authenticate(Authentication var1);
}

