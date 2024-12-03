/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.cors.reactive;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class CorsUtils {
    public static boolean isCorsRequest(ServerHttpRequest request) {
        return request.getHeaders().containsKey("Origin") && !CorsUtils.isSameOrigin(request);
    }

    public static boolean isPreFlightRequest(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        return request.getMethod() == HttpMethod.OPTIONS && headers.containsKey("Origin") && headers.containsKey("Access-Control-Request-Method");
    }

    @Deprecated
    public static boolean isSameOrigin(ServerHttpRequest request) {
        String origin = request.getHeaders().getOrigin();
        if (origin == null) {
            return true;
        }
        URI uri = request.getURI();
        String actualScheme = uri.getScheme();
        String actualHost = uri.getHost();
        int actualPort = CorsUtils.getPort(uri.getScheme(), uri.getPort());
        Assert.notNull((Object)actualScheme, "Actual request scheme must not be null");
        Assert.notNull((Object)actualHost, "Actual request host must not be null");
        Assert.isTrue(actualPort != -1, "Actual request port must not be undefined");
        UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
        return actualScheme.equals(originUrl.getScheme()) && actualHost.equals(originUrl.getHost()) && actualPort == CorsUtils.getPort(originUrl.getScheme(), originUrl.getPort());
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

