/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.oauth.util.RequestAnnotations
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.MultimapBuilder
 *  com.google.common.collect.MultimapBuilder$ListMultimapBuilder
 *  com.google.common.collect.Multimaps
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  lombok.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.requesthandler;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth.util.RequestAnnotations;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRateLimitUiRequestHandler
implements RateLimitUiRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRateLimitUiRequestHandler.class);
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ORIGIN = "origin";
    public static final String HEADER_REFERER = "Referer";
    public static final String COOKIE_SESSION_ID = "JSESSIONID";
    public static final String COOKIE_CSRF_TOKEN = "atlassian.xsrf.token";
    @NonNull
    private final Set<String> uiHeaderNames;
    @NonNull
    private final Set<String> uiCookieNames;
    private final int uiHeaderAndCookieCount;

    @Override
    public boolean isUiRequest(HttpServletRequest request) {
        if (this.isAccessTokenRequest(request) || DefaultRateLimitUiRequestHandler.isBasicAuthOrBearerAuthorization(request)) {
            return false;
        }
        long uiHeaderCount = this.getUiHeaderCount(request);
        long uiCookieCount = this.getUiCookieCount(request);
        return this.looksLikeUIRequest(uiHeaderCount, uiCookieCount);
    }

    private boolean isAccessTokenRequest(HttpServletRequest httpServletRequest) {
        return RequestAnnotations.isOAuthRequest((HttpServletRequest)httpServletRequest) || httpServletRequest.getAttribute("access.token.request") != null;
    }

    private static boolean isBasicAuthOrBearerAuthorization(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authHeader != null) {
            String lower = authHeader.toLowerCase(Locale.ROOT);
            return lower.startsWith("basic") || lower.startsWith("bearer");
        }
        return false;
    }

    @Override
    public void logRequestInfo(HttpServletRequest request) {
        if (this.isTraceEnabled()) {
            long uiHeaderCount = this.getUiHeaderCount(request);
            Multimap uiHeaders = (Multimap)this.uiHeaderNames.stream().collect(Multimaps.flatteningToMultimap(Function.identity(), h -> Collections.list(request.getHeaders(h)).stream(), () -> ((MultimapBuilder.ListMultimapBuilder)MultimapBuilder.linkedHashKeys().arrayListValues()).build()));
            long uiCookieCount = this.getUiCookieCount(request);
            Multimap uiCookies = (Multimap)this.getUiCookies(request).collect(Multimaps.toMultimap(Cookie::getName, Cookie::getValue, () -> ((MultimapBuilder.ListMultimapBuilder)MultimapBuilder.linkedHashKeys().arrayListValues()).build()));
            boolean isUiRequest = this.looksLikeUIRequest(uiHeaderCount, uiCookieCount);
            logger.trace("All request headers: [{}], UI headers: [{}] (count: {}), UI cookies [{}] (count: {}), is UI request: [{}]", new Object[]{Collections.list(request.getHeaderNames()), uiHeaders, uiHeaderCount, uiCookies, uiCookieCount, isUiRequest});
            String authHeader = Objects.nonNull(request.getHeader(HEADER_AUTHORIZATION)) ? request.getHeader(HEADER_AUTHORIZATION) : "no header found";
            logger.trace("Authorization: {}", (Object)authHeader);
        }
    }

    private long getUiHeaderCount(HttpServletRequest request) {
        long count = 0L;
        for (String uiHeaderName : this.uiHeaderNames) {
            if (request.getHeader(uiHeaderName) == null) continue;
            ++count;
        }
        return count;
    }

    private long getUiCookieCount(HttpServletRequest request) {
        return this.getUiCookies(request).count();
    }

    private Stream<Cookie> getUiCookies(HttpServletRequest request) {
        return this.streamOrEmpty(request.getCookies()).filter(cookie -> this.uiCookieNames.stream().filter(Objects::nonNull).anyMatch(cookieName -> cookieName.equalsIgnoreCase(cookie.getName())));
    }

    private <T> Stream<T> streamOrEmpty(T[] array) {
        return array != null ? Arrays.stream(array) : Stream.empty();
    }

    private boolean looksLikeUIRequest(long uiHeaderCount, long uiCookieCount) {
        return uiHeaderCount + uiCookieCount >= (long)this.uiHeaderAndCookieCount;
    }

    @VisibleForTesting
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public DefaultRateLimitUiRequestHandler(@NonNull Set<String> uiHeaderNames, @NonNull Set<String> uiCookieNames, int uiHeaderAndCookieCount) {
        if (uiHeaderNames == null) {
            throw new NullPointerException("uiHeaderNames is marked non-null but is null");
        }
        if (uiCookieNames == null) {
            throw new NullPointerException("uiCookieNames is marked non-null but is null");
        }
        this.uiHeaderNames = uiHeaderNames;
        this.uiCookieNames = uiCookieNames;
        this.uiHeaderAndCookieCount = uiHeaderAndCookieCount;
    }
}

