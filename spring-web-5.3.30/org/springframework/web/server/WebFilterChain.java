/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface WebFilterChain {
    public Mono<Void> filter(ServerWebExchange var1);
}

