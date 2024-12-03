/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.domain;

import reactor.core.publisher.Mono;

public interface ReactiveAuditorAware<T> {
    public Mono<T> getCurrentAuditor();
}

