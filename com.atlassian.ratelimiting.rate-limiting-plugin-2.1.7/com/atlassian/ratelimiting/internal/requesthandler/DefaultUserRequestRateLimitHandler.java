/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  net.oauth.OAuth$Parameter
 *  net.oauth.OAuthMessage
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.PathMatcher
 */
package com.atlassian.ratelimiting.internal.requesthandler;

import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.requesthandler.RateLimitUserRequestHandler;
import javax.servlet.http.HttpServletRequest;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class DefaultUserRequestRateLimitHandler
implements RateLimitUserRequestHandler {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_MOBILE_APP_REQUEST = "mobile-app-request";
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    private final RateLimitingProperties rateLimitingProperties;
    private final RateLimitUiRequestHandler rateLimitUiRequestHandler;

    public DefaultUserRequestRateLimitHandler(RateLimitingProperties rateLimitingProperties, RateLimitUiRequestHandler rateLimitUiRequestHandler) {
        this.rateLimitingProperties = rateLimitingProperties;
        this.rateLimitUiRequestHandler = rateLimitUiRequestHandler;
    }

    @Override
    public boolean shouldApplyRateLimiting(HttpServletRequest request) {
        return !this.rateLimitUiRequestHandler.isUiRequest(request) && !this.isMobileAppRequest(request) && !this.isUrlWhitelisted(request.getRequestURI()) && !this.isWhitelistedOAuthConsumer(request);
    }

    private boolean isUrlWhitelisted(String url) {
        return this.rateLimitingProperties.getWhitelistedUrlPatterns().stream().anyMatch(urlPattern -> PATH_MATCHER.match(urlPattern, url));
    }

    private boolean isWhitelistedOAuthConsumer(HttpServletRequest request) {
        return OAuthMessage.decodeAuthorization((String)request.getHeader(HEADER_AUTHORIZATION)).stream().filter(it -> it.getKey().equals("oauth_consumer_key")).map(OAuth.Parameter::getValue).findFirst().filter(this::isWhitelistedOAuthConsumerPrefix).isPresent();
    }

    private boolean isWhitelistedOAuthConsumerPrefix(String consumer) {
        return this.rateLimitingProperties.getWhitelistedOAuthConsumers().stream().anyMatch(it -> StringUtils.equalsIgnoreCase((CharSequence)consumer, (CharSequence)it));
    }

    private boolean isMobileAppRequest(HttpServletRequest request) {
        return request.getHeader(HEADER_MOBILE_APP_REQUEST) != null;
    }
}

