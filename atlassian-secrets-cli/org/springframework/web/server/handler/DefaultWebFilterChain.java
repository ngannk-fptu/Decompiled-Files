/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

public class DefaultWebFilterChain
implements WebFilterChain {
    private final List<WebFilter> filters;
    private final WebHandler handler;
    private final int index;

    public DefaultWebFilterChain(WebHandler handler, WebFilter ... filters) {
        Assert.notNull((Object)handler, "WebHandler is required");
        this.filters = ObjectUtils.isEmpty(filters) ? Collections.emptyList() : Arrays.asList(filters);
        this.handler = handler;
        this.index = 0;
    }

    private DefaultWebFilterChain(DefaultWebFilterChain parent, int index) {
        this.filters = parent.getFilters();
        this.handler = parent.getHandler();
        this.index = index;
    }

    public List<WebFilter> getFilters() {
        return this.filters;
    }

    public WebHandler getHandler() {
        return this.handler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange2) {
        return Mono.defer(() -> {
            if (this.index < this.filters.size()) {
                WebFilter filter = this.filters.get(this.index);
                DefaultWebFilterChain chain = new DefaultWebFilterChain(this, this.index + 1);
                return filter.filter(exchange2, chain);
            }
            return this.handler.handle(exchange2);
        });
    }
}

