/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpRequest
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.web.cors.CorsConfiguration
 *  org.springframework.web.util.WebUtils
 */
package org.springframework.web.socket.server.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

public class OriginHandshakeInterceptor
implements HandshakeInterceptor {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final CorsConfiguration corsConfiguration = new CorsConfiguration();

    public OriginHandshakeInterceptor() {
    }

    public OriginHandshakeInterceptor(Collection<String> allowedOrigins) {
        this.setAllowedOrigins(allowedOrigins);
    }

    public void setAllowedOrigins(Collection<String> allowedOrigins) {
        Assert.notNull(allowedOrigins, (String)"Allowed origins Collection must not be null");
        this.corsConfiguration.setAllowedOrigins(new ArrayList<String>(allowedOrigins));
    }

    public Collection<String> getAllowedOrigins() {
        List allowedOrigins = this.corsConfiguration.getAllowedOrigins();
        return CollectionUtils.isEmpty((Collection)allowedOrigins) ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet(allowedOrigins));
    }

    public void setAllowedOriginPatterns(Collection<String> allowedOriginPatterns) {
        Assert.notNull(allowedOriginPatterns, (String)"Allowed origin patterns Collection must not be null");
        this.corsConfiguration.setAllowedOriginPatterns(new ArrayList<String>(allowedOriginPatterns));
    }

    public Collection<String> getAllowedOriginPatterns() {
        List allowedOriginPatterns = this.corsConfiguration.getAllowedOriginPatterns();
        return CollectionUtils.isEmpty((Collection)allowedOriginPatterns) ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet(allowedOriginPatterns));
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (!WebUtils.isSameOrigin((HttpRequest)request) && this.corsConfiguration.checkOrigin(request.getHeaders().getOrigin()) == null) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Handshake request rejected, Origin header value " + request.getHeaders().getOrigin() + " not allowed"));
            }
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
    }
}

