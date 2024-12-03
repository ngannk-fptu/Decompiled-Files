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
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import com.atlassian.confluence.internal.diagnostics.ipd.http.IpdHttpMonitoringService;
import com.atlassian.confluence.internal.diagnostics.ipd.http.session.HttpSessionTracker;
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

public class IpdHttpMonitoringFilter
implements Filter {
    private final LazyComponentReference<IpdHttpMonitoringService> monitoringServiceRef = new LazyComponentReference("ipdHttpMonitoringService");

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (ContainerManager.isContainerSetup()) {
            Optional.ofNullable((IpdHttpMonitoringService)this.monitoringServiceRef.get()).ifPresent(service -> service.registerHttpRequest(request));
            HttpSessionTracker.recordInteraction((HttpServletRequest)request);
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}

