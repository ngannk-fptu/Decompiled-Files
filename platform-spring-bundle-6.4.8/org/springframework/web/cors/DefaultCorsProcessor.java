/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.cors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;

public class DefaultCorsProcessor
implements CorsProcessor {
    private static final Log logger = LogFactory.getLog(DefaultCorsProcessor.class);

    @Override
    public boolean processRequest(@Nullable CorsConfiguration config, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Collection varyHeaders = response.getHeaders("Vary");
        if (!varyHeaders.contains("Origin")) {
            response.addHeader("Vary", "Origin");
        }
        if (!varyHeaders.contains("Access-Control-Request-Method")) {
            response.addHeader("Vary", "Access-Control-Request-Method");
        }
        if (!varyHeaders.contains("Access-Control-Request-Headers")) {
            response.addHeader("Vary", "Access-Control-Request-Headers");
        }
        if (!CorsUtils.isCorsRequest(request)) {
            return true;
        }
        if (response.getHeader("Access-Control-Allow-Origin") != null) {
            logger.trace((Object)"Skip: response already contains \"Access-Control-Allow-Origin\"");
            return true;
        }
        boolean preFlightRequest = CorsUtils.isPreFlightRequest(request);
        if (config == null) {
            if (preFlightRequest) {
                this.rejectRequest(new ServletServerHttpResponse(response));
                return false;
            }
            return true;
        }
        return this.handleInternal(new ServletServerHttpRequest(request), new ServletServerHttpResponse(response), config, preFlightRequest);
    }

    protected void rejectRequest(ServerHttpResponse response) throws IOException {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getBody().write("Invalid CORS request".getBytes(StandardCharsets.UTF_8));
        response.flush();
    }

    protected boolean handleInternal(ServerHttpRequest request, ServerHttpResponse response, CorsConfiguration config, boolean preFlightRequest) throws IOException {
        String requestOrigin = request.getHeaders().getOrigin();
        String allowOrigin = this.checkOrigin(config, requestOrigin);
        HttpHeaders responseHeaders = response.getHeaders();
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
        response.flush();
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

