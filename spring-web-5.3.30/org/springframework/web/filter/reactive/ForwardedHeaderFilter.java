/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.filter.reactive;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import reactor.core.publisher.Mono;

@Deprecated
public class ForwardedHeaderFilter
extends ForwardedHeaderTransformer
implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange2, WebFilterChain chain) {
        ServerHttpRequest request = exchange2.getRequest();
        if (this.hasForwardedHeaders(request)) {
            exchange2 = exchange2.mutate().request(this.apply(request)).build();
        }
        return chain.filter(exchange2);
    }
}

