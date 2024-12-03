/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class UserDetailsRepositoryReactiveAuthenticationManager
extends AbstractUserDetailsReactiveAuthenticationManager {
    private ReactiveUserDetailsService userDetailsService;

    public UserDetailsRepositoryReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        Assert.notNull((Object)userDetailsService, (String)"userDetailsService cannot be null");
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return this.userDetailsService.findByUsername(username);
    }
}

