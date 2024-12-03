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
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoringNameGenerator;
import com.atlassian.confluence.util.profiling.Split;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfluenceTimingFilter
implements Filter {
    private final ConfluenceMonitoring performanceMonitor;
    private String prefix = "<TBD>";

    public ConfluenceTimingFilter(ConfluenceMonitoring perfMonitor) {
        this.performanceMonitor = perfMonitor;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.prefix = (String)StringUtils.defaultIfBlank((CharSequence)StringUtils.stripToEmpty((String)filterConfig.getInitParameter("prefix")), (CharSequence)"URI");
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try (Split ignored = this.startTimer((HttpServletRequest)request);){
            chain.doFilter(request, response);
        }
    }

    private @NonNull Split startTimer(HttpServletRequest request) {
        return this.performanceMonitor.startSplit(this.prefix, Collections.singletonMap("pathPattern", ConfluenceMonitoringNameGenerator.generateName(request)));
    }
}

