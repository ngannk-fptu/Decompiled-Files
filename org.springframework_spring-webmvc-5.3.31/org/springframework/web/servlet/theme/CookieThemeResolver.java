/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.CookieGenerator
 *  org.springframework.web.util.WebUtils
 */
package org.springframework.web.servlet.theme;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

public class CookieThemeResolver
extends CookieGenerator
implements ThemeResolver {
    public static final String ORIGINAL_DEFAULT_THEME_NAME = "theme";
    public static final String THEME_REQUEST_ATTRIBUTE_NAME = CookieThemeResolver.class.getName() + ".THEME";
    public static final String DEFAULT_COOKIE_NAME = CookieThemeResolver.class.getName() + ".THEME";
    private String defaultThemeName = "theme";

    public CookieThemeResolver() {
        this.setCookieName(DEFAULT_COOKIE_NAME);
    }

    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

    public String getDefaultThemeName() {
        return this.defaultThemeName;
    }

    @Override
    public String resolveThemeName(HttpServletRequest request) {
        String value;
        Cookie cookie;
        String themeName = (String)request.getAttribute(THEME_REQUEST_ATTRIBUTE_NAME);
        if (themeName != null) {
            return themeName;
        }
        String cookieName = this.getCookieName();
        if (cookieName != null && (cookie = WebUtils.getCookie((HttpServletRequest)request, (String)cookieName)) != null && StringUtils.hasText((String)(value = cookie.getValue()))) {
            themeName = value;
        }
        if (themeName == null) {
            themeName = this.getDefaultThemeName();
        }
        request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, (Object)themeName);
        return themeName;
    }

    @Override
    public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
        Assert.notNull((Object)response, (String)"HttpServletResponse is required for CookieThemeResolver");
        if (StringUtils.hasText((String)themeName)) {
            request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, (Object)themeName);
            this.addCookie(response, themeName);
        } else {
            request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, (Object)this.getDefaultThemeName());
            this.removeCookie(response);
        }
    }
}

