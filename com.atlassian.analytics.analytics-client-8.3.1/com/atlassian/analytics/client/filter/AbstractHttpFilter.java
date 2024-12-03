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
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.analytics.client.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractHttpFilter
implements Filter {
    static final Integer ZIPKIN_MAX_TRACE_BITS = 56;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
            return;
        }
        filterChain.doFilter(request, response);
    }

    protected abstract void doFilter(HttpServletRequest var1, HttpServletResponse var2, FilterChain var3) throws IOException, ServletException;

    private static String generateTraceId() {
        return Long.toHexString(ThreadLocalRandom.current().nextLong() & (1L << ZIPKIN_MAX_TRACE_BITS) - 1L);
    }

    protected static void setB3TraceId(HttpServletRequest request) {
        if (request.getAttributeNames() == null || !Collections.list(request.getAttributeNames()).contains("B3-TraceId")) {
            request.setAttribute("B3-TraceId", (Object)StringUtils.defaultIfBlank((CharSequence)request.getHeader("X-B3-TraceId"), (CharSequence)AbstractHttpFilter.generateTraceId()));
        }
    }
}

