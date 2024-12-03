/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mobile.filter;

import com.atlassian.confluence.plugins.mobile.MobileUtils;
import com.atlassian.confluence.plugins.mobile.event.MobileLoginEvent;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class CanUseFilter
extends AbstractHttpFilter {
    private final PermissionManager permissionManager;
    private final EventPublisher eventPublisher;
    private String redirectLocation;

    public CanUseFilter(PermissionManager permissionManager, EventPublisher eventPublisher) {
        this.permissionManager = permissionManager;
        this.eventPublisher = eventPublisher;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.redirectLocation = filterConfig.getInitParameter("loginRedirectLocation");
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ConfluenceUser remoteUser;
        if (MobileUtils.isMobileViewRequest(request) && !this.permissionManager.hasPermission((User)(remoteUser = AuthenticatedUserThreadLocal.get()), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            if (remoteUser == null) {
                response.sendRedirect(this.createLoginUrl(request));
                return;
            }
            response.sendError(403, "Not permitted to use the application.");
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private String createLoginUrl(HttpServletRequest request) throws UnsupportedEncodingException {
        this.eventPublisher.publish((Object)new MobileLoginEvent(request));
        String originalPath = request.getRequestURI().substring(request.getContextPath().length());
        String originalUrl = originalPath + (String)(StringUtils.isNotBlank((CharSequence)request.getQueryString()) ? "?" + request.getQueryString() : "");
        String loginUrl = request.getContextPath() + this.redirectLocation + "?os_destination=" + URLEncoder.encode(originalUrl, "UTF-8");
        return loginUrl;
    }
}

