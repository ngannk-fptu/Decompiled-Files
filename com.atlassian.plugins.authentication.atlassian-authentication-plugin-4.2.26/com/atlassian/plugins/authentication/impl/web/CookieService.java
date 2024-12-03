/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named
public class CookieService {
    private static final String COOKIE_WITHOUT_SAMESITE_RESTRICTIONS_PATTERN = "%s=%s; Path=%s; Max-Age=%s; Secure; HttpOnly; SameSite=None";
    private final ApplicationProperties applicationProperties;

    @Inject
    public CookieService(@ComponentImport ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public Optional<Cookie> getCookieFromRequest(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]);
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findAny();
    }

    public void removeCookie(String cookieName, HttpServletResponse response) {
        Cookie cookieToRemove = new Cookie(cookieName, null);
        cookieToRemove.setMaxAge(0);
        cookieToRemove.setPath(this.buildCookiePath());
        response.addCookie(cookieToRemove);
    }

    private String buildCookiePath() {
        String relativeBaseUrl = this.applicationProperties.getBaseUrl(UrlMode.RELATIVE);
        return Strings.isNullOrEmpty((String)relativeBaseUrl) ? "/" : relativeBaseUrl;
    }

    public void storeCookieWithoutSameSiteRestrictions(String cookieName, String cookieValue, int maxAgeInSeconds, HttpServletResponse response) {
        response.addHeader("Set-Cookie", String.format(COOKIE_WITHOUT_SAMESITE_RESTRICTIONS_PATTERN, cookieName, cookieValue, this.buildCookiePath(), maxAgeInSeconds));
    }
}

