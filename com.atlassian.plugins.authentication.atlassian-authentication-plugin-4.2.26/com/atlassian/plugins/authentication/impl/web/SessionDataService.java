/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.ws.rs.core.UriBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.impl.web.CookieService;
import com.atlassian.plugins.authentication.impl.web.SessionData;
import com.atlassian.plugins.authentication.impl.web.SessionDataCache;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheConfiguration;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class SessionDataService {
    private static final Logger log = LoggerFactory.getLogger(SessionDataService.class);
    public static final String COOKIE_NAME_PREFIX = "session-data-";
    static final String AUTH_SESSION_DATA = "com.atlassian.plugins.authentication.sessiondata";
    static final String SESSION_DATA_LIFETIME_IN_SECONDS_PROPERTY_NAME = "com.atlassian.plugins.authentication.impl.web.SessionDataService.requestIdGracePeriodSeconds";
    static final int DEFAULT_SESSION_DATA_LIFETIME_IN_SECONDS = 300;
    static final String USER_LOGGED_IN_WITH_SSO = "com.atlassian.plugins.authentication.userLoggedInWithSso";
    private static final String LOGGED_OUT_FROM_SERVICE_DESK = "was-logged-out";
    private final CookieService cookieService;
    private final SessionDataCache globalSessionDataCache;
    private final int requestIdGracePeriod;
    private final ApplicationProperties applicationProperties;

    @Inject
    public SessionDataService(CookieService cookieService, ApplicationProperties applicationProperties, SessionDataCacheFactory sessionDataCacheFactory) {
        this(cookieService, applicationProperties, Integer.getInteger(SESSION_DATA_LIFETIME_IN_SECONDS_PROPERTY_NAME, 300), sessionDataCacheFactory);
    }

    @VisibleForTesting
    SessionDataService(CookieService cookieService, ApplicationProperties applicationProperties, int requestIdGracePeriod, SessionDataCacheFactory sessionDataCacheFactory) {
        this.cookieService = cookieService;
        this.applicationProperties = applicationProperties;
        this.requestIdGracePeriod = requestIdGracePeriod;
        this.globalSessionDataCache = sessionDataCacheFactory.createSessionDataCache(new SessionDataCacheConfiguration(requestIdGracePeriod));
    }

    public void setSessionData(HttpServletRequest request, HttpServletResponse response, String key, SessionData sessionData) {
        Preconditions.checkNotNull((Object)key);
        Preconditions.checkNotNull((Object)sessionData);
        request.getSession(true);
        this.cookieService.storeCookieWithoutSameSiteRestrictions(COOKIE_NAME_PREFIX + key, "", this.requestIdGracePeriod, response);
        this.globalSessionDataCache.put(key, sessionData);
        log.debug("Saved login session data {} in user session: {} using key {}", new Object[]{sessionData, request.getSession().getId(), key});
    }

    public Optional<SessionData> getSessionData(HttpServletRequest request, HttpServletResponse response, String key) {
        if (key == null) {
            return Optional.empty();
        }
        String cookieName = COOKIE_NAME_PREFIX + key;
        return this.cookieService.getCookieFromRequest(cookieName, request).flatMap(cookie -> {
            this.cookieService.removeCookie(cookieName, response);
            SessionData sessionData = this.globalSessionDataCache.get(key);
            this.globalSessionDataCache.remove(key);
            if (sessionData != null) {
                Optional<String> urlFragment = this.extractAndInvalidateFragmentCookieValue(request, response, sessionData.getAuthenticationRequest().getPublicId());
                return Optional.of(new SessionData(sessionData.getAuthenticationRequest(), this.prepareTargetUrlWithFragment(sessionData.getTargetUrl(), urlFragment), sessionData.getIdpConfigId()));
            }
            return Optional.empty();
        });
    }

    private Optional<String> extractAndInvalidateFragmentCookieValue(HttpServletRequest request, HttpServletResponse response, String key) {
        String cookieName = "atlassian-authentication-plugin-url-fragment_" + key;
        Optional<Cookie> fragmentCookie = this.cookieService.getCookieFromRequest(cookieName, request);
        fragmentCookie.ifPresent(cookie -> this.cookieService.removeCookie(cookieName, response));
        return fragmentCookie.map(Cookie::getValue);
    }

    private URI prepareTargetUrlWithFragment(Optional<URI> targetUri, Optional<String> fragment) {
        try {
            return UriBuilder.fromUri((URI)targetUri.orElse(new URI(""))).fragment((String)fragment.orElse(null)).build(new Object[0]);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void ensureSessionExists(HttpServletRequest request) {
        request.getSession(true);
    }

    public void requireNewSession(HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            try {
                oldSession.invalidate();
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        this.ensureSessionExists(request);
    }

    public void setUserLoggedInWithSso(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_LOGGED_IN_WITH_SSO, (Object)Boolean.TRUE);
    }

    public boolean isUserLoggedInWithSso(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object sessionAttribute = session.getAttribute(USER_LOGGED_IN_WITH_SSO);
        return sessionAttribute != null && (Boolean)sessionAttribute != false;
    }

    public boolean wasLoggedOutFromJsmCustomerPortal(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getSession(true).getAttribute(LOGGED_OUT_FROM_SERVICE_DESK));
    }

    public void setLoggedOutFromJsmCustomerPortal(HttpServletRequest request, boolean value) {
        HttpSession session = request.getSession(true);
        if (value) {
            session.setAttribute(LOGGED_OUT_FROM_SERVICE_DESK, (Object)value);
        } else {
            session.removeAttribute(LOGGED_OUT_FROM_SERVICE_DESK);
        }
    }

    public String extractTargetUrlOrReturnBaseUrl(Optional<SessionData> sessionData) {
        String targetUrl = sessionData.flatMap(SessionData::getTargetUrl).map(uri -> UriBuilder.fromUri((URI)uri).replacePath("").path(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE)).path(uri.getPath()).build(new Object[0]).toString()).orElse(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE));
        if (targetUrl.isEmpty()) {
            targetUrl = "/";
        }
        return targetUrl;
    }
}

