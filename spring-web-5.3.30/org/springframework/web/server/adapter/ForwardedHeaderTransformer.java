/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.server.adapter;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

public class ForwardedHeaderTransformer
implements Function<ServerHttpRequest, ServerHttpRequest> {
    static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap(new LinkedCaseInsensitiveMap(10, Locale.ENGLISH));
    private boolean removeOnly;

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    public boolean isRemoveOnly() {
        return this.removeOnly;
    }

    @Override
    public ServerHttpRequest apply(ServerHttpRequest request) {
        if (this.hasForwardedHeaders(request)) {
            ServerHttpRequest.Builder builder = request.mutate();
            if (!this.removeOnly) {
                URI uri = UriComponentsBuilder.fromHttpRequest(request).build(true).toUri();
                builder.uri(uri);
                String prefix = ForwardedHeaderTransformer.getForwardedPrefix(request);
                if (prefix != null) {
                    builder.path(prefix + uri.getRawPath());
                    builder.contextPath(prefix);
                }
                InetSocketAddress remoteAddress = request.getRemoteAddress();
                if ((remoteAddress = UriComponentsBuilder.parseForwardedFor(request, remoteAddress)) != null) {
                    builder.remoteAddress(remoteAddress);
                }
            }
            this.removeForwardedHeaders(builder);
            request = builder.build();
        }
        return request;
    }

    protected boolean hasForwardedHeaders(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        for (String headerName : FORWARDED_HEADER_NAMES) {
            if (!headers.containsKey(headerName)) continue;
            return true;
        }
        return false;
    }

    private void removeForwardedHeaders(ServerHttpRequest.Builder builder) {
        builder.headers(map -> FORWARDED_HEADER_NAMES.forEach(map::remove));
    }

    @Nullable
    private static String getForwardedPrefix(ServerHttpRequest request) {
        String[] rawPrefixes;
        HttpHeaders headers = request.getHeaders();
        String header = headers.getFirst("X-Forwarded-Prefix");
        if (header == null) {
            return null;
        }
        StringBuilder prefix = new StringBuilder(header.length());
        for (String rawPrefix : rawPrefixes = StringUtils.tokenizeToStringArray((String)header, (String)",")) {
            int endIndex;
            for (endIndex = rawPrefix.length(); endIndex > 1 && rawPrefix.charAt(endIndex - 1) == '/'; --endIndex) {
            }
            prefix.append(endIndex != rawPrefix.length() ? rawPrefix.substring(0, endIndex) : rawPrefix);
        }
        return prefix.toString();
    }

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-For");
    }
}

