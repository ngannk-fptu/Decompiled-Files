/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.baseurl;

import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class IncludeResourcesFilter
extends AbstractHttpFilter {
    static final String BASE_URL_RESOURCE_CONTEXT = "baseurl-checker-resource";
    private static final Set<String> UNFILTERED_REQUEST_URLS = Collections.unmodifiableSet(Stream.of("/plugins/servlet/mobile").collect(Collectors.toSet()));
    private final ConfluenceWebResourceManager webResourceManager;
    private final PermissionManager permissionManager;

    @Autowired
    public IncludeResourcesFilter(@ComponentImport ConfluenceWebResourceManager webResourceManager, @ComponentImport PermissionManager permissionManager) {
        this.webResourceManager = webResourceManager;
        this.permissionManager = permissionManager;
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (this.permissionManager.isSystemAdministrator((User)AuthenticatedUserThreadLocal.get()) && this.isURIFilterable(request.getRequestURI().substring(request.getContextPath().length()))) {
            this.webResourceManager.requireResourcesForContext(BASE_URL_RESOURCE_CONTEXT);
            this.webResourceManager.putMetadata("server-scheme", String.valueOf(request.getScheme()));
            this.webResourceManager.putMetadata("server-port", String.valueOf(request.getServerPort()));
            this.webResourceManager.putMetadata("server-name", String.valueOf(request.getServerName()));
            this.webResourceManager.putMetadata("behind-proxy", String.valueOf(request.getHeader("Http-X-Forwarded-For")));
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private boolean isURIFilterable(String requestURI) {
        return UNFILTERED_REQUEST_URLS.stream().noneMatch(unfilteredRequestURL -> requestURI.startsWith((String)unfilteredRequestURL));
    }
}

