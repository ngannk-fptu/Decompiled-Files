/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 *  reactor.util.context.Context
 */
package org.springframework.web.filter.reactive;

import java.util.Optional;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ServerWebExchangeContextFilter
implements WebFilter {
    public static final String EXCHANGE_CONTEXT_ATTRIBUTE = ServerWebExchangeContextFilter.class.getName() + ".EXCHANGE_CONTEXT";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange2, WebFilterChain chain) {
        return chain.filter(exchange2).contextWrite(cxt -> cxt.put((Object)EXCHANGE_CONTEXT_ATTRIBUTE, (Object)exchange2));
    }

    public static Optional<ServerWebExchange> get(Context context) {
        return context.getOrEmpty((Object)EXCHANGE_CONTEXT_ATTRIBUTE);
    }
}

