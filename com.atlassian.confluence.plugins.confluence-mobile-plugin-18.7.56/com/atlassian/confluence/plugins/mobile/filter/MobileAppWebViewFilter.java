/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.UserAgentUtil$BrowserFamily
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.mobile.filter;

import com.atlassian.confluence.plugins.mobile.filter.WrongCodeResponseWrapper;
import com.atlassian.confluence.plugins.mobile.service.MobileFeatureManager;
import com.atlassian.confluence.plugins.mobile.util.MobileUtil;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.UserAgentUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MobileAppWebViewFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(MobileAppWebViewFilter.class);
    private static final String DUO_LOGIN_URL = "/plugins/servlet/duologin";
    private static final String DUO_LOGIN_RESOURCE = "/download/resources/com.duosecurity.confluence.plugins.duo-twofactor:resources/";
    private static final String AUTHENTICATED_COOKIE_NAME = "authenticated";
    private static final int AUTHENTICATED_COOKIE_EXPIRED_TIME = 300;
    private static final String REST_PREFIX = "/rest";
    private static final String[] MOBILE_APP_USER_AGENTS = new String[]{UserAgentUtil.BrowserFamily.ATLASSIAN_MOBILE.getUserAgentString(), "Confluence/"};
    private MobileFeatureManager featureManager;

    @Autowired
    public MobileAppWebViewFilter(MobileFeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest)request;
        Object servletResponse = (HttpServletResponse)response;
        try {
            if (this.isMobileLoginFlowRequest(servletRequest)) {
                this.addAuthCookie(servletRequest, (HttpServletResponse)servletResponse);
            }
        }
        catch (Exception e) {
            log.error("Cannot add authenticated cookie to mobile app web view request", (Throwable)e);
        }
        if (this.isEligibleForStatusRewriting(servletRequest) && this.featureManager.isStatusCodeRewritingEnabled()) {
            servletResponse = new WrongCodeResponseWrapper((HttpServletResponse)servletResponse, servletRequest);
        }
        filterChain.doFilter(request, (ServletResponse)servletResponse);
    }

    public void destroy() {
    }

    private void addAuthCookie(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        boolean isAuthenticated = !AuthenticatedUserThreadLocal.isAnonymousUser() && !this.isDuoLogin(servletRequest);
        Cookie cookie = new Cookie(AUTHENTICATED_COOKIE_NAME, String.valueOf(isAuthenticated));
        cookie.setMaxAge(300);
        cookie.setPath(this.getCookiePath(servletRequest));
        servletResponse.addCookie(cookie);
    }

    private boolean isDuoLogin(HttpServletRequest request) {
        String url = MobileUtil.extractURL(request);
        return url.equals(DUO_LOGIN_URL) || url.startsWith(DUO_LOGIN_RESOURCE);
    }

    private String getCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return StringUtils.isBlank((CharSequence)contextPath) ? "/" : contextPath;
    }

    private boolean isMobileLoginFlowRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.contains(UserAgentUtil.BrowserFamily.ATLASSIAN_MOBILE.getUserAgentString());
    }

    private boolean isEligibleForStatusRewriting(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        boolean isMobileAppUserAgent = StringUtils.startsWithAny((CharSequence)userAgent, (CharSequence[])MOBILE_APP_USER_AGENTS);
        boolean isRestEndpoint = MobileUtil.extractURL(request).startsWith(REST_PREFIX);
        return isMobileAppUserAgent && isRestEndpoint;
    }
}

