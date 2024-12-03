/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.core.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface ReactiveUserDetailsPasswordService {
    public Mono<UserDetails> updatePassword(UserDetails var1, String var2);
}

