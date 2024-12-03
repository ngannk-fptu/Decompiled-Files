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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling.filters;

import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.util.profiling.filters.FilterConfigAware;
import com.atlassian.util.profiling.filters.ProfilingStatusUpdateViaRequestStrategy;
import com.atlassian.util.profiling.filters.StatusUpdateStrategy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ProfilingFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ProfilingFilter.class);
    protected static final String AUTOSTART_PARAM = "autostart";
    protected final StatusUpdateStrategy statusUpdateStrategy;

    public ProfilingFilter() {
        this.statusUpdateStrategy = new ProfilingStatusUpdateViaRequestStrategy();
    }

    protected ProfilingFilter(StatusUpdateStrategy statusUpdateStrategy) {
        if (statusUpdateStrategy == null) {
            throw new IllegalArgumentException("statusUpdateStrategy must not be null!");
        }
        this.statusUpdateStrategy = statusUpdateStrategy;
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.statusUpdateStrategy.setStateViaRequest(request);
        if (!this.isFilterOn()) {
            chain.doFilter(request, response);
            return;
        }
        String resource = this.getResourceName(request);
        try (Ticker ignored = Timers.start(resource);){
            chain.doFilter(request, response);
        }
    }

    public void init(FilterConfig filterConfig) {
        if (filterConfig != null) {
            String autostartParam = filterConfig.getInitParameter(AUTOSTART_PARAM);
            if (autostartParam != null) {
                if ("true".equals(autostartParam)) {
                    log.debug("[Filter: {}] defaulting to on [{}=true]", (Object)filterConfig.getFilterName(), (Object)AUTOSTART_PARAM);
                    this.turnProfilingOn();
                } else if ("false".equals(autostartParam)) {
                    log.debug("[Filter: {}] defaulting to off [{}=false]", (Object)filterConfig.getFilterName(), (Object)AUTOSTART_PARAM);
                    this.turnProfilingOff();
                } else {
                    log.debug("[Filter: {}] autostart value: {} is unknown no action taken]", (Object)filterConfig.getFilterName(), (Object)autostartParam);
                }
            }
            if (this.statusUpdateStrategy instanceof FilterConfigAware) {
                ((FilterConfigAware)((Object)this.statusUpdateStrategy)).configure(filterConfig);
            }
        }
    }

    private boolean isFilterOn() {
        return Timers.getConfiguration().isEnabled();
    }

    private String getResourceName(ServletRequest request) {
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            return (String)request.getAttribute("javax.servlet.include.request_uri");
        }
        return ((HttpServletRequest)request).getRequestURI();
    }

    protected void turnProfilingOn() {
        Timers.getConfiguration().setEnabled(true);
    }

    protected void turnProfilingOff() {
        ProfilerConfiguration config = Timers.getConfiguration();
        config.setMinTraceTime(0L, TimeUnit.MILLISECONDS);
        config.setEnabled(false);
    }
}

