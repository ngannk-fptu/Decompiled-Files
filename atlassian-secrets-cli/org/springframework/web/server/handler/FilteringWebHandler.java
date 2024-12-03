/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.handler;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.DefaultWebFilterChain;
import org.springframework.web.server.handler.WebHandlerDecorator;
import reactor.core.publisher.Mono;

public class FilteringWebHandler
extends WebHandlerDecorator {
    private final WebFilter[] filters;

    public FilteringWebHandler(WebHandler webHandler, List<WebFilter> filters) {
        super(webHandler);
        this.filters = filters.toArray(new WebFilter[0]);
    }

    public List<WebFilter> getFilters() {
        return Arrays.asList(this.filters);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange2) {
        return this.filters.length != 0 ? new DefaultWebFilterChain(this.getDelegate(), this.filters).filter(exchange2) : super.handle(exchange2);
    }
}

