/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.cors.reactive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsProcessor;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;

public class DefaultCorsProcessor
implements CorsProcessor {
    private static final Log logger = LogFactory.getLog(DefaultCorsProcessor.class);
    private static final List<String> VARY_HEADERS = Arrays.asList("Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers");

    @Override
    public boolean process(@Nullable CorsConfiguration config, ServerWebExchange exchange2) {
        ServerHttpRequest request = exchange2.getRequest();
        ServerHttpResponse response = exchange2.getResponse();
        HttpHeaders responseHeaders = response.getHeaders();
        Object varyHeaders = responseHeaders.get("Vary");
        if (varyHeaders == null) {
            responseHeaders.addAll("Vary", VARY_HEADERS);
        } else {
            for (String header : VARY_HEADERS) {
                if (varyHeaders.contains(header)) continue;
                responseHeaders.add("Vary", header);
            }
        }
        if (!CorsUtils.isCorsRequest(request)) {
            return true;
        }
        if (responseHeaders.getFirst("Access-Control-Allow-Origin") != null) {
            logger.trace((Object)"Skip: response already contains \"Access-Control-Allow-Origin\"");
            return true;
        }
        boolean preFlightRequest = CorsUtils.isPreFlightRequest(request);
        if (config == null) {
            if (preFlightRequest) {
                this.rejectRequest(response);
                return false;
            }
            return true;
        }
        return this.handleInternal(exchange2, config, preFlightRequest);
    }

    protected void rejectRequest(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
    }

    protected boolean handleInternal(ServerWebExchange exchange2, CorsConfiguration config, boolean preFlightRequest) {
        ServerHttpRequest request = exchange2.getRequest();
        ServerHttpResponse response = exchange2.getResponse();
        HttpHeaders responseHeaders = response.getHeaders();
        String requestOrigin = request.getHeaders().getOrigin();
        String allowOrigin = this.checkOrigin(config, requestOrigin);
        if (allowOrigin == null) {
            logger.debug((Object)("Reject: '" + requestOrigin + "' origin is not allowed"));
            this.rejectRequest(response);
            return false;
        }
        HttpMethod requestMethod = this.getMethodToUse(request, preFlightRequest);
        List<HttpMethod> allowMethods = this.checkMethods(config, requestMethod);
        if (allowMethods == null) {
            logger.debug((Object)("Reject: HTTP '" + (Object)((Object)requestMethod) + "' is not allowed"));
            this.rejectRequest(response);
            return false;
        }
        List<String> requestHeaders = this.getHeadersToUse(request, preFlightRequest);
        List<String> allowHeaders = this.checkHeaders(config, requestHeaders);
        if (preFlightRequest && allowHeaders == null) {
            logger.debug((Object)("Reject: headers '" + requestHeaders + "' are not allowed"));
            this.rejectRequest(response);
            return false;
        }
        responseHeaders.setAccessControlAllowOrigin(allowOrigin);
        if (preFlightRequest) {
            responseHeaders.setAccessControlAllowMethods(allowMethods);
        }
        if (preFlightRequest && !allowHeaders.isEmpty()) {
            responseHeaders.setAccessControlAllowHeaders(allowHeaders);
        }
        if (!CollectionUtils.isEmpty(config.getExposedHeaders())) {
            responseHeaders.setAccessControlExposeHeaders(config.getExposedHeaders());
        }
        if (Boolean.TRUE.equals(config.getAllowCredentials())) {
            responseHeaders.setAccessControlAllowCredentials(true);
        }
        if (preFlightRequest && config.getMaxAge() != null) {
            responseHeaders.setAccessControlMaxAge(config.getMaxAge());
        }
        return true;
    }

    @Nullable
    protected String checkOrigin(CorsConfiguration config, @Nullable String requestOrigin) {
        return config.checkOrigin(requestOrigin);
    }

    @Nullable
    protected List<HttpMethod> checkMethods(CorsConfiguration config, @Nullable HttpMethod requestMethod) {
        return config.checkHttpMethod(requestMethod);
    }

    @Nullable
    private HttpMethod getMethodToUse(ServerHttpRequest request, boolean isPreFlight) {
        return isPreFlight ? request.getHeaders().getAccessControlRequestMethod() : request.getMethod();
    }

    @Nullable
    protected List<String> checkHeaders(CorsConfiguration config, List<String> requestHeaders) {
        return config.checkHeaders(requestHeaders);
    }

    private List<String> getHeadersToUse(ServerHttpRequest request, boolean isPreFlight) {
        HttpHeaders headers = request.getHeaders();
        return isPreFlight ? headers.getAccessControlRequestHeaders() : new ArrayList<String>(headers.keySet());
    }
}

