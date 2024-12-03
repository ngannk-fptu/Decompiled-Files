/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.cors.reactive;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface PreFlightRequestHandler {
    public Mono<Void> handlePreFlight(ServerWebExchange var1);
}

