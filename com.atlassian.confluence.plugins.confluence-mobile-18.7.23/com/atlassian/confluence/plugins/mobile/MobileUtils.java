/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mobile;

import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public final class MobileUtils {
    public static final String DESKTOP_URL_PARAMETER = "desktop";
    private static final Pattern MOBILE_USER_AGENT_REGEX = Pattern.compile("(iPhone;|iPad;|iPhone Simulator;|iPod;|iPod touch;|Linux; U; Android)");
    private static final Pattern ANDROID_CHROME = Pattern.compile("Chrome/[.\\d]* Mobile");
    private static final Pattern OLD_ANDROID = Pattern.compile("Linux; U; Android (?:[23]\\.\\d|4\\.0\\.[12])");
    private static final Pattern ANDROID_FIREFOX = Pattern.compile("\\((Mobile|Android \\d(.\\d)*; (Mobile|Tablet|Mobi));");

    public static boolean isMobileViewRequest(HttpServletRequest request) {
        return MobileUtils.isSupportedUserAgent(request) && !MobileUtils.isDesktopSwitchRequired(request);
    }

    public static boolean isSupportedUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        return StringUtils.isNotBlank((CharSequence)userAgent) && !OLD_ANDROID.matcher(userAgent).find() && (MOBILE_USER_AGENT_REGEX.matcher(userAgent).find() || ANDROID_CHROME.matcher(userAgent).find() || ANDROID_FIREFOX.matcher(userAgent).find());
    }

    public static boolean isDesktopSwitchRequired(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (!"confluence.mobile.desktop.switch".equals(cookie.getName()) || !"true".equals(cookie.getValue())) continue;
                return true;
            }
        }
        return Boolean.valueOf(request.getParameter(DESKTOP_URL_PARAMETER));
    }
}

