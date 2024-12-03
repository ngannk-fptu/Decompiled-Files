/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.filter.reactive;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

public class ForwardedHeaderFilter
implements WebFilter {
    private static final Set<String> FORWARDED_HEADER_NAMES = new LinkedHashSet<String>(5);
    private boolean removeOnly;

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange2, WebFilterChain chain) {
        if (this.shouldNotFilter(exchange2.getRequest())) {
            return chain.filter(exchange2);
        }
        if (this.removeOnly) {
            ServerWebExchange withoutForwardHeaders = exchange2.mutate().request(builder -> builder.headers(headers -> {
                for (String headerName : FORWARDED_HEADER_NAMES) {
                    headers.remove(headerName);
                }
            })).build();
            return chain.filter(withoutForwardHeaders);
        }
        URI uri = UriComponentsBuilder.fromHttpRequest(exchange2.getRequest()).build(true).toUri();
        String prefix = ForwardedHeaderFilter.getForwardedPrefix(exchange2.getRequest().getHeaders());
        ServerWebExchange withChangedUri = exchange2.mutate().request(builder -> {
            builder.uri(uri);
            if (prefix != null) {
                builder.path(prefix + uri.getPath());
                builder.contextPath(prefix);
            }
        }).build();
        return chain.filter(withChangedUri);
    }

    private boolean shouldNotFilter(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        for (String headerName : FORWARDED_HEADER_NAMES) {
            if (!headers.containsKey(headerName)) continue;
            return false;
        }
        return true;
    }

    @Nullable
    private static String getForwardedPrefix(HttpHeaders headers) {
        String prefix = headers.getFirst("X-Forwarded-Prefix");
        if (prefix != null) {
            while (prefix.endsWith("/")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }
        return prefix;
    }

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
    }
}

