/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.cors.reactive;

import org.springframework.util.Assert;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.PreFlightRequestHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class PreFlightRequestWebFilter
implements WebFilter {
    private final PreFlightRequestHandler handler;

    public PreFlightRequestWebFilter(PreFlightRequestHandler handler) {
        Assert.notNull((Object)handler, "PreFlightRequestHandler is required");
        this.handler = handler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange2, WebFilterChain chain) {
        return CorsUtils.isPreFlightRequest(exchange2.getRequest()) ? this.handler.handlePreFlight(exchange2) : chain.filter(exchange2);
    }
}

