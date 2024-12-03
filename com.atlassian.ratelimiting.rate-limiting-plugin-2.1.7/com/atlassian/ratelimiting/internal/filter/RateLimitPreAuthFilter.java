/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.filter;

import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.internal.requesthandler.logging.RateLimitedRequestLogger;
import com.atlassian.ratelimiting.internal.settings.RateLimitLightweightAccessService;
import com.atlassian.ratelimiting.node.RateLimitService;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.requesthandler.PreAuthRequestDecoder;
import com.atlassian.ratelimiting.requesthandler.RateLimitResponseHandler;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.requesthandler.RateLimitUserRequestHandler;
import com.atlassian.sal.api.user.UserKey;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimitPreAuthFilter
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitPreAuthFilter.class);
    private final RateLimitService rateLimitService;
    private final RateLimitLightweightAccessService rateLimitSettingsService;
    private final RateLimitingProperties rateLimitingProperties;
    private final PreAuthRequestDecoder preAuthRequestDecoder;
    private final RateLimitResponseHandler rateLimitResponseHandler;
    private final RateLimitedRequestLogger rateLimitedRequestLogger;
    private final RateLimitUiRequestHandler rateLimitUiRequestHandler;
    private final RateLimitUserRequestHandler userRequestRateLimitHandler;

    public RateLimitPreAuthFilter(RateLimitService rateLimitService, RateLimitLightweightAccessService rateLimitSettingsService, RateLimitingProperties rateLimitingProperties, PreAuthRequestDecoder preAuthRequestDecoder, RateLimitResponseHandler rateLimitResponseHandler, RateLimitedRequestLogger rateLimitedRequestLogger, RateLimitUiRequestHandler rateLimitUiRequestHandler, RateLimitUserRequestHandler userRequestRateLimitHandler) {
        this.rateLimitService = rateLimitService;
        this.rateLimitSettingsService = rateLimitSettingsService;
        this.rateLimitingProperties = rateLimitingProperties;
        this.preAuthRequestDecoder = preAuthRequestDecoder;
        this.rateLimitResponseHandler = rateLimitResponseHandler;
        this.rateLimitedRequestLogger = rateLimitedRequestLogger;
        this.rateLimitUiRequestHandler = rateLimitUiRequestHandler;
        this.userRequestRateLimitHandler = userRequestRateLimitHandler;
    }

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        if (this.requestHasBeenRateLimitedPreAuth(httpRequest, httpResponse)) {
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean requestHasBeenRateLimitedPreAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.rateLimitUiRequestHandler.logRequestInfo(request);
        if (this.rateLimitingProperties.isPreAuthFilterEnabled() && this.rateLimitSettingsService.getRateLimitingMode().equals((Object)RateLimitingMode.ON)) {
            boolean rateLimited;
            if (this.userRequestRateLimitHandler.shouldApplyRateLimiting(request) && (rateLimited = this.userHasBeenRateLimitedPreAuth(request, response))) {
                logger.trace("Request has been rate limited before authentication - stopping request here");
                return true;
            }
            logger.trace("Request has passed before authentication rate limiting - continuing on...");
        } else {
            logger.trace("Rate limiting before authentication is off - continuing on...");
        }
        return false;
    }

    private boolean userHasBeenRateLimitedPreAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean userHasBeenRateLimited;
        Optional<UserKey> userKey = this.preAuthRequestDecoder.getUserKey(request);
        boolean bl = userHasBeenRateLimited = userKey.isPresent() && this.rateLimitService.tryRateLimitPreAuth(userKey.get());
        if (userHasBeenRateLimited) {
            this.rateLimitResponseHandler.applyRateLimitingInfo(response, request, userKey.get(), () -> this.rateLimitService.getBucket((UserKey)userKey.get()));
            this.rateLimitedRequestLogger.logRateLimitedRequestPreAuth(userKey, request);
            return true;
        }
        return false;
    }

    public void destroy() {
    }
}

