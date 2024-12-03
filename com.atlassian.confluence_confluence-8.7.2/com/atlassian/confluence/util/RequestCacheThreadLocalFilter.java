/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.Tracing
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.util;

import brave.Span;
import brave.Tracing;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.logging.LoggingContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RequestCacheThreadLocalFilter
implements Filter {
    private static final String MOBILE_APP_REQUEST = "mobile-app-request";
    private final Supplier<Tracing> tracingReference = new LazyComponentReference("tracing");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        block6: {
            try {
                RequestCacheThreadLocal.setRequestCache(new HashMap());
                if (servletRequest instanceof HttpServletRequest) {
                    RequestCacheThreadLocal.getRequestCache().put("confluence.context.path", ((HttpServletRequest)servletRequest).getContextPath());
                    RequestCacheThreadLocal.getRequestCache().put("request.remote.address", servletRequest.getRemoteAddr());
                    RequestCacheThreadLocal.getRequestCache().put("x.forwarded.for", ((HttpServletRequest)servletRequest).getHeader("X-Forwarded-For"));
                    RequestCacheThreadLocal.getRequestCache().put("header.mobile.app.request", ((HttpServletRequest)servletRequest).getHeader(MOBILE_APP_REQUEST));
                    String traceId = this.obtainTraceId();
                    RequestCacheThreadLocal.getRequestCache().put("request.correlation.id", traceId);
                    LoggingContext.put((String)"traceId", (Object)traceId);
                    try {
                        filterChain.doFilter(servletRequest, servletResponse);
                    }
                    catch (Throwable throwable) {
                        LoggingContext.remove((String[])new String[]{"traceId", traceId});
                        throw throwable;
                    }
                    LoggingContext.remove((String[])new String[]{"traceId", traceId});
                    break block6;
                }
                filterChain.doFilter(servletRequest, servletResponse);
            }
            finally {
                RequestCacheThreadLocal.clearRequestCache();
            }
        }
    }

    private String obtainTraceId() {
        Span currentSpan;
        Optional<Long> traceId = Optional.empty();
        if (ContainerManager.isContainerSetup() && (currentSpan = ((Tracing)this.tracingReference.get()).tracer().currentSpan()) != null) {
            traceId = Optional.of(currentSpan.context().traceId());
        }
        return String.format("%016x", traceId.orElseGet(() -> ThreadLocalRandom.current().nextLong(0x100000000000000L)));
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}

