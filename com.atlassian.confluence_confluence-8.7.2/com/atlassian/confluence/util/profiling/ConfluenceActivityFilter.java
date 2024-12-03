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
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.util.profiling.Activity;
import com.atlassian.confluence.util.profiling.ActivityMonitor;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceActivityFilter
implements Filter {
    private final ActivityMonitor activityMonitor;

    public ConfluenceActivityFilter(ActivityMonitor activityMonitor) {
        this.activityMonitor = activityMonitor;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String userId = this.generateUserId((HttpServletRequest)request);
        String summary = this.generateActivitySummary((HttpServletRequest)request);
        try (Activity activity = this.activityMonitor.registerStart(userId, "web-request", summary);){
            chain.doFilter(request, response);
        }
    }

    private String generateUserId(HttpServletRequest request) {
        String username = request.getRemoteUser();
        return StringUtils.isBlank((CharSequence)username) ? "<unknown>" : username;
    }

    private String generateActivitySummary(HttpServletRequest request) {
        int contextOffset = request.getContextPath().length();
        String stripped = request.getRequestURI().substring(contextOffset);
        String queryStr = request.getQueryString();
        Object result = StringUtils.isNotBlank((CharSequence)queryStr) ? stripped + "?" + queryStr : stripped;
        return result;
    }
}

