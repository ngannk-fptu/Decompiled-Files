/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
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
import com.atlassian.ratelimiting.internal.concurrent.OperationThrottler;
import com.atlassian.ratelimiting.internal.requesthandler.logging.RateLimitedRequestLogger;
import com.atlassian.ratelimiting.internal.settings.RateLimitLightweightAccessService;
import com.atlassian.ratelimiting.node.RateLimitService;
import com.atlassian.ratelimiting.requesthandler.RateLimitResponseHandler;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.requesthandler.RateLimitUserRequestHandler;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.io.IOException;
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

public class RateLimitFilter
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    private final RateLimitUserRequestHandler userRequestRateLimitHandler;
    private final RateLimitService rateLimitService;
    private final RateLimitLightweightAccessService rateLimitSettingsService;
    private final UserService userService;
    private final OperationThrottler<UserKey> operationThrottler;
    private final RateLimitResponseHandler rateLimitResponseHandler;
    private final RateLimitedRequestLogger rateLimitedRequestLogger;
    private final RateLimitUiRequestHandler rateLimitUiRequestHandler;

    public RateLimitFilter(RateLimitUserRequestHandler userRequestRateLimitHandler, RateLimitService rateLimitService, RateLimitLightweightAccessService rateLimitSettingsService, UserService userService, OperationThrottler<UserKey> operationThrottler, RateLimitResponseHandler rateLimitResponseHandler, RateLimitedRequestLogger rateLimitedRequestLogger, RateLimitUiRequestHandler rateLimitUiRequestHandler) {
        this.rateLimitService = rateLimitService;
        this.rateLimitSettingsService = rateLimitSettingsService;
        this.userService = userService;
        this.operationThrottler = operationThrottler;
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
        if (this.requestHasBeenRateLimited(httpRequest, httpResponse)) {
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean requestHasBeenRateLimited(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RateLimitingMode rateLimitingMode = this.rateLimitSettingsService.getRateLimitingMode();
        if (rateLimitingMode.isEnabled()) {
            boolean rateLimited;
            logger.trace("Checking if rate limiting logic needs to be applied to user request: [{}]", (Object)request.getRequestURI());
            this.rateLimitUiRequestHandler.logRequestInfo(request);
            if (this.userRequestRateLimitHandler.shouldApplyRateLimiting(request) && (rateLimited = this.userHasBeenRateLimited(request, response, rateLimitingMode))) {
                logger.trace("Request has been rate limited - stopping request here");
                return true;
            }
            logger.trace("Request has passed rate limiting - continuing on...");
        } else {
            logger.trace("Rate limiting is off - continuing on...");
        }
        return false;
    }

    private boolean userHasBeenRateLimited(HttpServletRequest request, HttpServletResponse response, RateLimitingMode rateLimitingMode) throws IOException {
        boolean userHasBeenRateLimited;
        UserKey userKey = (UserKey)Preconditions.checkNotNull((Object)this.userService.getUserKey(request), (Object)("Unexpected null userkey found for request: " + request));
        boolean bl = userHasBeenRateLimited = !this.rateLimitService.tryAcquire(userKey) && !rateLimitingMode.isDryRun();
        if (userHasBeenRateLimited) {
            this.operationThrottler.tryRun(userKey, () -> logger.warn("User [{}] has been rate limited", (Object)userKey));
            this.rateLimitResponseHandler.applyRateLimitingInfo(response, request, userKey, () -> this.rateLimitService.getBucket(userKey));
            this.rateLimitedRequestLogger.logRateLimitedRequest(userKey, request);
            return true;
        }
        this.rateLimitResponseHandler.addRateLimitingHeaders(response, userKey, () -> this.rateLimitService.getBucket(userKey));
        return false;
    }

    public void destroy() {
    }
}

