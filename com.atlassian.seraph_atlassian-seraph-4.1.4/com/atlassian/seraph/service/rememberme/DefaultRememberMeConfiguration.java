/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.seraph.service.rememberme;

import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.spi.rememberme.RememberMeConfiguration;
import com.atlassian.seraph.util.ServerInformationParser;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class DefaultRememberMeConfiguration
implements RememberMeConfiguration {
    public static final int TWO_WEEKS = 1209600;
    private final SecurityConfig config;

    public DefaultRememberMeConfiguration() {
        this(SecurityConfigFactory.getInstance());
    }

    public DefaultRememberMeConfiguration(SecurityConfig config) {
        this.config = config;
    }

    @Override
    public boolean isInsecureCookieAlwaysUsed() {
        return this.config.isInsecureCookie();
    }

    @Override
    public boolean isCookieHttpOnly(HttpServletRequest httpServletRequest) {
        ServletContext servletContext = httpServletRequest.getSession().getServletContext();
        try {
            boolean servletApiSupportsHttpOnlyCookies;
            ServerInformationParser.ServerInformation serverInfo = ServerInformationParser.parse(servletContext.getServerInfo());
            boolean bl = servletApiSupportsHttpOnlyCookies = servletContext.getMajorVersion() >= 3;
            if (servletApiSupportsHttpOnlyCookies) {
                return true;
            }
            return serverInfo.isApacheTomcat() && (serverInfo.getVersion().startsWith("5") && serverInfo.getVersion().compareTo("5.5.28") >= 0 || serverInfo.getVersion().startsWith("6") && serverInfo.getVersion().compareTo("6.0.19") >= 0);
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getCookieName() {
        return this.config.getLoginCookieKey();
    }

    @Override
    public int getCookieMaxAgeInSeconds() {
        int maxAge = this.config.getAutoLoginCookieAge();
        if (maxAge <= 0) {
            maxAge = 1209600;
        }
        return maxAge;
    }

    @Override
    public String getCookieDomain(HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public String getCookiePath(HttpServletRequest httpServletRequest) {
        String path = this.config.getLoginCookiePath();
        if (path != null) {
            return path;
        }
        String contextPath = httpServletRequest.getContextPath();
        if (StringUtils.isBlank((CharSequence)contextPath)) {
            return "/";
        }
        return contextPath;
    }
}

