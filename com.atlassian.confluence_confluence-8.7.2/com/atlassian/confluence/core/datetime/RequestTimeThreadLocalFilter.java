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
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.core.datetime;

import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class RequestTimeThreadLocalFilter
implements Filter {
    private static final String REQUEST_TIME_ATTRIBUTE_NAME = "Confluence-Request-Time";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long requestTime = this.getTimeForRequest(servletRequest);
        RequestTimeThreadLocal.setTime(requestTime);
        try {
            if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletResponse)servletResponse).setHeader("X-Confluence-Request-Time", Long.toString(requestTime));
            }
            servletRequest.setAttribute(REQUEST_TIME_ATTRIBUTE_NAME, (Object)requestTime);
            filterChain.doFilter(servletRequest, servletResponse);
        }
        finally {
            RequestTimeThreadLocal.clearTime();
        }
    }

    protected long getTimeForRequest(ServletRequest servletRequest) {
        return System.currentTimeMillis();
    }

    public void destroy() {
    }

    public static Optional<Instant> getRequestStartTime(ServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getAttribute(REQUEST_TIME_ATTRIBUTE_NAME)).map(Number.class::cast).map(Number::longValue).map(Instant::ofEpochMilli);
    }
}

