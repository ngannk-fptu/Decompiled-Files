/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet.filter;

import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugin.servlet.filter.IteratingFilterChain;
import com.atlassian.plugin.servlet.util.RequestUtil;
import com.atlassian.plugin.servlet.util.ServletContextServletModuleManagerAccessor;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletFilterModuleContainerFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ServletFilterModuleContainerFilter.class);
    private FilterConfig filterConfig;
    private FilterLocation location;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.location = FilterLocation.parse(filterConfig.getInitParameter("location"));
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.getServletModuleManager() == null) {
            log.info("Could not get ServletModuleManager. Skipping filter plugins.");
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        Iterable<Filter> filters = this.getServletModuleManager().getFilters(this.location, ServletFilterModuleContainerFilter.getPath(request), this.filterConfig, request.getDispatcherType());
        IteratingFilterChain pluginFilterChain = new IteratingFilterChain(filters.iterator(), chain);
        pluginFilterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    public void destroy() {
    }

    protected ServletModuleManager getServletModuleManager() {
        return ServletContextServletModuleManagerAccessor.getServletModuleManager(this.filterConfig.getServletContext());
    }

    protected final FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    protected final FilterLocation getFilterLocation() {
        return this.location;
    }

    private static String getPath(HttpServletRequest request) {
        return RequestUtil.getServletPath(request) + RequestUtil.getPathInfo(request);
    }
}

