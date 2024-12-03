/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UrlPathHelper
 */
package com.atlassian.plugins.authentication.impl.basicauth.filter;

import com.atlassian.plugins.authentication.impl.basicauth.filter.DisableBasicAuthResponseWriter;
import com.atlassian.plugins.authentication.impl.basicauth.service.BasicAuthRequestMatcher;
import com.atlassian.plugins.authentication.impl.basicauth.service.CachingBasicAuthService;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Base64;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;

public class DisableBasicAuthFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(DisableBasicAuthFilter.class);
    @VisibleForTesting
    static final String INVALIDATE_SESSION_SYSTEM_PROPERTY = "com.atlassian.plugins.authentication.basic.auth.filter.invalidate.session";
    private static final String BASIC_AUTH_TYPE_PREFIX = "Basic ";
    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private final CachingBasicAuthService cachingBasicAuthService;
    private final DisableBasicAuthResponseWriter disableBasicAuthResponseWriter;

    @Inject
    public DisableBasicAuthFilter(CachingBasicAuthService cachingBasicAuthService, DisableBasicAuthResponseWriter disableBasicAuthResponseWriter) {
        this.cachingBasicAuthService = cachingBasicAuthService;
        this.disableBasicAuthResponseWriter = disableBasicAuthResponseWriter;
    }

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        Result result = this.checkRequest(request);
        if (result == Result.BLOCK_REQUEST) {
            log.trace("Blocking HTTP request - Basic Authentication is not allowed: {}", (Object)request.getRequestURI());
            this.disableBasicAuthResponseWriter.write(request, response);
        } else {
            HttpSession session;
            log.trace("Allowing HTTP request: {}", (Object)request.getRequestURI());
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
            if (result == Result.INVALIDATE_SESSION && !"false".equalsIgnoreCase(System.getProperty(INVALIDATE_SESSION_SYSTEM_PROPERTY)) && (session = request.getSession(false)) != null) {
                try {
                    log.debug("Invalidating session {} for HTTP request: {}", (Object)session.getId(), (Object)request.getRequestURI());
                    session.invalidate();
                }
                catch (IllegalStateException illegalStateException) {
                    // empty catch block
                }
            }
        }
    }

    private Result checkRequest(HttpServletRequest request) {
        String authorizationHeader = StringUtils.trim((String)request.getHeader("Authorization"));
        BasicAuthRequestMatcher matcher = this.cachingBasicAuthService.getMatcher();
        if (matcher.isBlockRequests() && this.isBasicAuthorizationHeader(authorizationHeader)) {
            String path = URL_PATH_HELPER.getPathWithinApplication(request);
            String user = this.decodeBasicAuthorizationUsername(authorizationHeader);
            log.debug("Basic Authentication is not allowed, checking if request is allow-listed (path={}, user={})", (Object)path, (Object)user);
            if (matcher.isPathAllowed(path)) {
                log.debug("Path is allowed - allowing the request, but will invalidate session afterwards (path={}, user={})", (Object)path, (Object)user);
                return Result.INVALIDATE_SESSION;
            }
            if (matcher.isUserAllowed(user)) {
                log.debug("User is allowed - allowing the request (path={}, user={})", (Object)path, (Object)user);
                return Result.ALLOW_REQUEST;
            }
            log.debug("Neither path nor user are allowed - blocking the request (path={}, user={})", (Object)path, (Object)user);
            return Result.BLOCK_REQUEST;
        }
        return Result.ALLOW_REQUEST;
    }

    private boolean isBasicAuthorizationHeader(@Nullable String header) {
        return StringUtils.startsWithIgnoreCase((CharSequence)header, (CharSequence)BASIC_AUTH_TYPE_PREFIX);
    }

    @Nullable
    private String decodeBasicAuthorizationUsername(@Nullable String authHeader) {
        try {
            String encodedCredentials = StringUtils.substring((String)authHeader, (int)BASIC_AUTH_TYPE_PREFIX.length());
            String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));
            return StringUtils.substringBefore((String)decodedCredentials, (String)":");
        }
        catch (IllegalArgumentException e) {
            log.debug("Could not decode Authorisation header - not a base64 encoded value", (Throwable)e);
            return null;
        }
    }

    public void destroy() {
    }

    private static enum Result {
        BLOCK_REQUEST,
        INVALIDATE_SESSION,
        ALLOW_REQUEST;

    }
}

