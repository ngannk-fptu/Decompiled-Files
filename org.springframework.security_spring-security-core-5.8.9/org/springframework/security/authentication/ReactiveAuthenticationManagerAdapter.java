/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Scheduler
 *  reactor.core.scheduler.Schedulers
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class ReactiveAuthenticationManagerAdapter
implements ReactiveAuthenticationManager {
    private final AuthenticationManager authenticationManager;
    private Scheduler scheduler = Schedulers.boundedElastic();

    public ReactiveAuthenticationManagerAdapter(AuthenticationManager authenticationManager) {
        Assert.notNull((Object)authenticationManager, (String)"authenticationManager cannot be null");
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication token) {
        return Mono.just((Object)token).publishOn(this.scheduler).flatMap(this::doAuthenticate).filter(Authentication::isAuthenticated);
    }

    private Mono<Authentication> doAuthenticate(Authentication authentication) {
        try {
            return Mono.just((Object)this.authenticationManager.authenticate(authentication));
        }
        catch (Throwable ex) {
            return Mono.error((Throwable)ex);
        }
    }

    public void setScheduler(Scheduler scheduler) {
        Assert.notNull((Object)scheduler, (String)"scheduler cannot be null");
        this.scheduler = scheduler;
    }
}

