/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authentication;

import java.util.Arrays;
import java.util.List;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DelegatingReactiveAuthenticationManager
implements ReactiveAuthenticationManager {
    private final List<ReactiveAuthenticationManager> delegates;

    public DelegatingReactiveAuthenticationManager(ReactiveAuthenticationManager ... entryPoints) {
        this(Arrays.asList(entryPoints));
    }

    public DelegatingReactiveAuthenticationManager(List<ReactiveAuthenticationManager> entryPoints) {
        Assert.notEmpty(entryPoints, (String)"entryPoints cannot be null");
        this.delegates = entryPoints;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Flux.fromIterable(this.delegates).concatMap(m -> m.authenticate(authentication)).next();
    }
}

