/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class CookieGenerator {
    public static final String DEFAULT_COOKIE_PATH = "/";
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private String cookieName;
    @Nullable
    private String cookieDomain;
    private String cookiePath = "/";
    @Nullable
    private Integer cookieMaxAge;
    private boolean cookieSecure = false;
    private boolean cookieHttpOnly = false;

    public void setCookieName(@Nullable String cookieName) {
        this.cookieName = cookieName;
    }

    @Nullable
    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieDomain(@Nullable String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    @Nullable
    public String getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getCookiePath() {
        return this.cookiePath;
    }

    public void setCookieMaxAge(@Nullable Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    @Nullable
    public Integer getCookieMaxAge() {
        return this.cookieMaxAge;
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public boolean isCookieSecure() {
        return this.cookieSecure;
    }

    public void setCookieHttpOnly(boolean cookieHttpOnly) {
        this.cookieHttpOnly = cookieHttpOnly;
    }

    public boolean isCookieHttpOnly() {
        return this.cookieHttpOnly;
    }

    public void addCookie(HttpServletResponse response, String cookieValue) {
        Assert.notNull((Object)response, "HttpServletResponse must not be null");
        Cookie cookie = this.createCookie(cookieValue);
        Integer maxAge = this.getCookieMaxAge();
        if (maxAge != null) {
            cookie.setMaxAge(maxAge.intValue());
        }
        if (this.isCookieSecure()) {
            cookie.setSecure(true);
        }
        if (this.isCookieHttpOnly()) {
            cookie.setHttpOnly(true);
        }
        response.addCookie(cookie);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Added cookie with name [" + this.getCookieName() + "] and value [" + cookieValue + "]");
        }
    }

    public void removeCookie(HttpServletResponse response) {
        Assert.notNull((Object)response, "HttpServletResponse must not be null");
        Cookie cookie = this.createCookie("");
        cookie.setMaxAge(0);
        if (this.isCookieSecure()) {
            cookie.setSecure(true);
        }
        if (this.isCookieHttpOnly()) {
            cookie.setHttpOnly(true);
        }
        response.addCookie(cookie);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Removed cookie with name [" + this.getCookieName() + "]");
        }
    }

    protected Cookie createCookie(String cookieValue) {
        Cookie cookie = new Cookie(this.getCookieName(), cookieValue);
        if (this.getCookieDomain() != null) {
            cookie.setDomain(this.getCookieDomain());
        }
        cookie.setPath(this.getCookiePath());
        return cookie;
    }
}

