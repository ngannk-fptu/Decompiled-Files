/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.utils.ConstantTimeComparison
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.service.rememberme;

import com.atlassian.security.utils.ConstantTimeComparison;
import com.atlassian.seraph.ioc.ApplicationServicesRegistry;
import com.atlassian.seraph.service.rememberme.DefaultRememberMeToken;
import com.atlassian.seraph.service.rememberme.RememberMeService;
import com.atlassian.seraph.service.rememberme.RememberMeToken;
import com.atlassian.seraph.service.rememberme.RememberMeTokenGenerator;
import com.atlassian.seraph.spi.rememberme.RememberMeConfiguration;
import com.atlassian.seraph.spi.rememberme.RememberMeTokenDao;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRememberMeService
implements RememberMeService {
    private static final Logger log = LoggerFactory.getLogger(DefaultRememberMeService.class);
    private final RememberMeConfiguration rememberMeConfiguration;
    private final RememberMeTokenDao rememberMeTokenDao;
    private final RememberMeTokenGenerator rememberMeTokenGenerator;
    private static final String URL_ENCODING = "UTF-8";

    public DefaultRememberMeService(RememberMeConfiguration rememberMeConfiguration, RememberMeTokenDao rememberMeTokenDao, RememberMeTokenGenerator rememberMeTokenGenerator) {
        this.rememberMeConfiguration = rememberMeConfiguration;
        this.rememberMeTokenDao = rememberMeTokenDao;
        this.rememberMeTokenGenerator = rememberMeTokenGenerator;
        ApplicationServicesRegistry.setRememberMeService(this);
    }

    @Override
    public String getRememberMeCookieAuthenticatedUsername(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        RememberMeToken cookieToken = this.getCookieValue(httpServletRequest);
        if (cookieToken != null) {
            RememberMeToken storedToken = this.rememberMeTokenDao.findById(cookieToken.getId());
            if (storedToken != null && ConstantTimeComparison.isEqual((String)cookieToken.getRandomString(), (String)storedToken.getRandomString()) && !this.isExpired(storedToken)) {
                return storedToken.getUserName();
            }
            if (httpServletResponse != null) {
                this.removeRememberMeCookie(httpServletRequest, httpServletResponse);
            }
        }
        return null;
    }

    private boolean isExpired(RememberMeToken storedToken) {
        return storedToken.getCreatedTime() + TimeUnit.SECONDS.toMillis(this.rememberMeConfiguration.getCookieMaxAgeInSeconds()) < System.currentTimeMillis();
    }

    @Override
    public void addRememberMeCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String authenticatedUsername) {
        RememberMeToken token = this.rememberMeTokenGenerator.generateToken(authenticatedUsername);
        RememberMeToken persistedToken = this.rememberMeTokenDao.save(token);
        String desiredCookieName = this.rememberMeConfiguration.getCookieName();
        Cookie cookie = this.findRememberCookie(httpServletRequest, desiredCookieName);
        if (cookie == null) {
            cookie = new Cookie(desiredCookieName, persistedToken.getRandomString());
        }
        this.setValuesIntoCookie(httpServletRequest, cookie, this.toCookieValue(persistedToken), this.rememberMeConfiguration.getCookieMaxAgeInSeconds(), this.rememberMeConfiguration.getCookieDomain(httpServletRequest), this.rememberMeConfiguration.getCookiePath(httpServletRequest), this.rememberMeConfiguration.isInsecureCookieAlwaysUsed());
        this.setRememberMeCookie(httpServletRequest, httpServletResponse, cookie);
    }

    @Override
    public void removeRememberMeCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie cookie = this.findRememberCookie(httpServletRequest, this.rememberMeConfiguration.getCookieName());
        if (cookie != null) {
            RememberMeToken cookieToken = this.parseIntoToken(cookie);
            this.setValuesIntoCookie(httpServletRequest, cookie, "", 0, this.rememberMeConfiguration.getCookieDomain(httpServletRequest), this.rememberMeConfiguration.getCookiePath(httpServletRequest), this.rememberMeConfiguration.isInsecureCookieAlwaysUsed());
            this.setRememberMeCookie(httpServletRequest, httpServletResponse, cookie);
            if (cookieToken != null) {
                this.rememberMeTokenDao.remove(cookieToken.getId());
            }
        }
    }

    private void setValuesIntoCookie(HttpServletRequest httpServletRequest, Cookie cookie, String value, int maxAgeInSeconds, String cookieDomain, String cookiePath, boolean isInsecureCookieUsed) {
        if (StringUtils.isNotBlank((CharSequence)cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        if (StringUtils.isNotBlank((CharSequence)cookiePath)) {
            cookie.setPath(cookiePath);
        }
        if (!isInsecureCookieUsed) {
            cookie.setSecure(httpServletRequest.isSecure());
        }
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setValue(DefaultRememberMeService.escapeInvalidCookieCharacters(value));
    }

    private void setRememberMeCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Cookie cookie) {
        if (this.rememberMeConfiguration.isCookieHttpOnly(httpServletRequest)) {
            cookie.setHttpOnly(true);
        }
        httpServletResponse.addCookie(cookie);
    }

    private String toCookieValue(RememberMeToken persistedToken) {
        return persistedToken.getId() + ":" + persistedToken.getRandomString();
    }

    private RememberMeToken getCookieValue(HttpServletRequest httpServletRequest) {
        Cookie cookie = this.findRememberCookie(httpServletRequest, this.rememberMeConfiguration.getCookieName());
        if (cookie != null) {
            return this.parseIntoToken(cookie);
        }
        return null;
    }

    private RememberMeToken parseIntoToken(Cookie cookie) {
        Long id;
        String value = DefaultRememberMeService.unescapeInvalidCookieCharacters(cookie.getValue());
        if (StringUtils.isBlank((CharSequence)value)) {
            return null;
        }
        int indexColon = value.indexOf(58);
        if (indexColon <= 0 || indexColon == value.length() - 1) {
            return null;
        }
        try {
            id = Long.parseLong(value.substring(0, indexColon));
        }
        catch (NumberFormatException e) {
            return null;
        }
        String randomString = value.substring(indexColon + 1);
        return DefaultRememberMeToken.builder(id, randomString).build();
    }

    private Cookie findRememberCookie(HttpServletRequest httpServletRequest, String cookieName) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (!cookieName.equalsIgnoreCase(cookie.getName())) continue;
                return cookie;
            }
        }
        return null;
    }

    private static String escapeInvalidCookieCharacters(String s) {
        try {
            return URLEncoder.encode(s, URL_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError((Object)e);
        }
    }

    private static String unescapeInvalidCookieCharacters(String s) {
        try {
            return URLDecoder.decode(s, URL_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            log.error("UTF-8 encoding unsupported !!?!! How is that possible in java?", (Throwable)e);
            throw new AssertionError((Object)e);
        }
    }
}

