/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.handler;

import java.util.List;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.DefaultWebFilterChain;
import org.springframework.web.server.handler.WebHandlerDecorator;
import reactor.core.publisher.Mono;

public class FilteringWebHandler
extends WebHandlerDecorator {
    private final DefaultWebFilterChain chain;

    public FilteringWebHandler(WebHandler handler, List<WebFilter> filters) {
        super(handler);
        this.chain = new DefaultWebFilterChain(handler, filters);
    }

    public List<WebFilter> getFilters() {
        return this.chain.getFilters();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange2) {
        return this.chain.filter(exchange2);
    }
}

