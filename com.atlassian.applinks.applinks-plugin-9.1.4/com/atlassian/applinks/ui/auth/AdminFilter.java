/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.page.PageCapability
 *  com.atlassian.sal.api.user.UserRole
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.ui.auth;

import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.applinks.ui.auth.ServletSessionHandler;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.page.PageCapability;
import com.atlassian.sal.api.user.UserRole;
import java.io.IOException;
import java.net.URI;
import java.util.EnumSet;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminFilter
implements Filter {
    protected final AdminUIAuthenticator uiAuthenticator;
    private final LoginUriProvider loginUriProvider;
    private final ApplicationProperties applicationProperties;

    public AdminFilter(AdminUIAuthenticator uiAuthenticator, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties) {
        this.uiAuthenticator = uiAuthenticator;
        this.loginUriProvider = loginUriProvider;
        this.applicationProperties = applicationProperties;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String password;
        if (!(servletRequest instanceof HttpServletRequest)) {
            return;
        }
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String username = request.getParameter("al_username");
        if (!this.checkAccess(username, password = request.getParameter("al_password"), new ServletSessionHandler(request))) {
            this.handleAccessDenied(request, response);
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    protected void handleAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(this.loginUriProvider.getLoginUriForRole(this.getOriginalUrl(request), this.getForRole(), EnumSet.of(PageCapability.IFRAME)).toASCIIString());
    }

    UserRole getForRole() {
        return UserRole.ADMIN;
    }

    boolean checkAccess(String username, String password, AdminUIAuthenticator.SessionHandler sessionHandler) {
        return this.uiAuthenticator.checkAdminUIAccessBySessionOrPasswordAndActivateAdminSession(username, password, sessionHandler);
    }

    private URI getOriginalUrl(HttpServletRequest request) {
        String originalUrl = this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE) + request.getServletPath() + request.getPathInfo() + this.sanitiseQueryString(request);
        return URIUtil.uncheckedToUri(originalUrl);
    }

    private String sanitiseQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            queryString = "";
        } else if ((queryString = queryString.replaceAll("(&|^)al_(username|password)=[^&]*", "")).length() > 0) {
            queryString = "?" + queryString;
        }
        return queryString;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}

