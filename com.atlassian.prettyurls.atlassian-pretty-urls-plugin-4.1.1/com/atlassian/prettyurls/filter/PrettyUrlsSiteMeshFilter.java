/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.opensymphony.sitemesh.webapp.SiteMeshFilter
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.prettyurls.filter;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.prettyurls.filter.PrettyUrlsCommonFilter;
import com.atlassian.prettyurls.internal.util.UrlUtils;
import com.atlassian.prettyurls.module.SiteMeshModuleDescriptor;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PrettyUrlsSiteMeshFilter
extends PrettyUrlsCommonFilter {
    private final Filter siteMeshFilterDelegate;
    private final PluginAccessor pluginAccessor;

    public PrettyUrlsSiteMeshFilter(@ComponentImport PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
        Class<?> siteMeshClass = null;
        try {
            siteMeshClass = Class.forName("com.opensymphony.sitemesh.webapp.SiteMeshFilter");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        this.siteMeshFilterDelegate = siteMeshClass != null ? new SiteMeshFilter() : null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (this.siteMeshFilterDelegate != null) {
            this.siteMeshFilterDelegate.init(filterConfig);
        }
    }

    @Override
    public void destroy() {
        if (this.siteMeshFilterDelegate != null) {
            this.siteMeshFilterDelegate.destroy();
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (this.siteMeshFilterDelegate == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletRequest httpServletRequest = this.preventDoubleInvocation(servletRequest, servletResponse, filterChain);
        if (httpServletRequest == null) {
            return;
        }
        if (this.needsSiteMeshDecoration(httpServletRequest)) {
            this.siteMeshFilterDelegate.doFilter(servletRequest, servletResponse, filterChain);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean needsSiteMeshDecoration(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getAttribute("com.opensymphony.sitemesh.APPLIED_ONCE") != null) {
            return false;
        }
        String requestURI = this.makeRequestURI(httpServletRequest);
        List siteMeshModules = this.pluginAccessor.getEnabledModuleDescriptorsByClass(SiteMeshModuleDescriptor.class);
        for (SiteMeshModuleDescriptor module : siteMeshModules) {
            if (!requestURI.startsWith(module.getPath())) continue;
            return true;
        }
        return false;
    }

    private String makeRequestURI(HttpServletRequest httpServletRequest) {
        String context;
        String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.startsWith(context = httpServletRequest.getContextPath())) {
            requestURI = requestURI.substring(context.length());
        }
        return UrlUtils.startWithSlash(requestURI);
    }
}

