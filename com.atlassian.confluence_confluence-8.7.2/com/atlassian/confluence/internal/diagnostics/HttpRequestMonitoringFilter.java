/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.internal.diagnostics.HttpRequestMonitor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class HttpRequestMonitoringFilter
implements Filter {
    private static final boolean HTTP_SLOW_REQUEST_DISABLE = Boolean.getBoolean("diagnostics.http.slow.request.disable");
    private LazyComponentReference<HttpRequestMonitor> monitorReference = new LazyComponentReference("httpRequestMonitor");

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!ContainerManager.isContainerSetup() || HTTP_SLOW_REQUEST_DISABLE) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        Optional<HttpRequestMonitor> monitor = Optional.ofNullable((HttpRequestMonitor)((Object)this.monitorReference.get()));
        try {
            monitor.ifPresent(mon -> mon.start((HttpServletRequest)servletRequest));
            filterChain.doFilter(servletRequest, servletResponse);
        }
        finally {
            monitor.ifPresent(mon -> mon.stop((HttpServletRequest)servletRequest));
        }
    }

    public void destroy() {
    }
}

