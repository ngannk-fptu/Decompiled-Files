/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.cors.reactive;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsProcessor;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.DefaultCorsProcessor;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class CorsWebFilter
implements WebFilter {
    private final CorsConfigurationSource configSource;
    private final CorsProcessor processor;

    public CorsWebFilter(CorsConfigurationSource configSource) {
        this(configSource, new DefaultCorsProcessor());
    }

    public CorsWebFilter(CorsConfigurationSource configSource, CorsProcessor processor) {
        Assert.notNull((Object)configSource, (String)"CorsConfigurationSource must not be null");
        Assert.notNull((Object)processor, (String)"CorsProcessor must not be null");
        this.configSource = configSource;
        this.processor = processor;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange2, WebFilterChain chain) {
        ServerHttpRequest request = exchange2.getRequest();
        CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(exchange2);
        boolean isValid = this.processor.process(corsConfiguration, exchange2);
        if (!isValid || CorsUtils.isPreFlightRequest(request)) {
            return Mono.empty();
        }
        return chain.filter(exchange2);
    }
}

