/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

public class DefaultWebFilterChain
implements WebFilterChain {
    private final List<WebFilter> allFilters;
    private final WebHandler handler;
    @Nullable
    private final WebFilter currentFilter;
    @Nullable
    private final DefaultWebFilterChain chain;

    public DefaultWebFilterChain(WebHandler handler, List<WebFilter> filters) {
        Assert.notNull((Object)handler, (String)"WebHandler is required");
        this.allFilters = Collections.unmodifiableList(filters);
        this.handler = handler;
        DefaultWebFilterChain chain = DefaultWebFilterChain.initChain(filters, handler);
        this.currentFilter = chain.currentFilter;
        this.chain = chain.chain;
    }

    private static DefaultWebFilterChain initChain(List<WebFilter> filters, WebHandler handler) {
        DefaultWebFilterChain chain = new DefaultWebFilterChain(filters, handler, null, null);
        ListIterator<WebFilter> iterator = filters.listIterator(filters.size());
        while (iterator.hasPrevious()) {
            chain = new DefaultWebFilterChain(filters, handler, iterator.previous(), chain);
        }
        return chain;
    }

    private DefaultWebFilterChain(List<WebFilter> allFilters, WebHandler handler, @Nullable WebFilter currentFilter, @Nullable DefaultWebFilterChain chain) {
        this.allFilters = allFilters;
        this.currentFilter = currentFilter;
        this.handler = handler;
        this.chain = chain;
    }

    @Deprecated
    public DefaultWebFilterChain(WebHandler handler, WebFilter ... filters) {
        this(handler, Arrays.asList(filters));
    }

    public List<WebFilter> getFilters() {
        return this.allFilters;
    }

    public WebHandler getHandler() {
        return this.handler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange2) {
        return Mono.defer(() -> this.currentFilter != null && this.chain != null ? this.invokeFilter(this.currentFilter, this.chain, exchange2) : this.handler.handle(exchange2));
    }

    private Mono<Void> invokeFilter(WebFilter current, DefaultWebFilterChain chain, ServerWebExchange exchange2) {
        String currentName = current.getClass().getName();
        return current.filter(exchange2, chain).checkpoint(currentName + " [DefaultWebFilterChain]");
    }
}

