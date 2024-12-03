/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.cors.reactive;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class CorsUtils {
    public static boolean isCorsRequest(ServerHttpRequest request) {
        return request.getHeaders().get("Origin") != null;
    }

    public static boolean isPreFlightRequest(ServerHttpRequest request) {
        return request.getMethod() == HttpMethod.OPTIONS && CorsUtils.isCorsRequest(request) && request.getHeaders().get("Access-Control-Request-Method") != null;
    }

    public static boolean isSameOrigin(ServerHttpRequest request) {
        String origin = request.getHeaders().getOrigin();
        if (origin == null) {
            return true;
        }
        UriComponents actualUrl = UriComponentsBuilder.fromHttpRequest(request).build();
        String actualHost = actualUrl.getHost();
        int actualPort = CorsUtils.getPort(actualUrl.getScheme(), actualUrl.getPort());
        Assert.notNull((Object)actualHost, "Actual request host must not be null");
        Assert.isTrue(actualPort != -1, "Actual request port must not be undefined");
        UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
        return actualHost.equals(originUrl.getHost()) && actualPort == CorsUtils.getPort(originUrl.getScheme(), originUrl.getPort());
    }

    private static int getPort(@Nullable String scheme, int port) {
        if (port == -1) {
            if ("http".equals(scheme) || "ws".equals(scheme)) {
                port = 80;
            } else if ("https".equals(scheme) || "wss".equals(scheme)) {
                port = 443;
            }
        }
        return port;
    }
}

