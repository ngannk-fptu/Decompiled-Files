/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.http.HttpTracing
 *  brave.servlet.TracingFilter
 *  com.atlassian.annotations.Internal
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.confluence.web.filter;

import brave.http.HttpTracing;
import brave.servlet.TracingFilter;
import com.atlassian.annotations.Internal;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.ResettableLazyReference;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Internal
public class ZipkinTracingFilter
implements Filter {
    private FilterConfig filterConfig;
    private final ResettableLazyReference<Filter> tracingFilter = new ResettableLazyReference<Filter>(){

        protected Filter create() throws Exception {
            LazyComponentReference httpTracingReference = new LazyComponentReference("httpTracing");
            HttpTracing httpTracing = Objects.requireNonNull((HttpTracing)httpTracingReference.get());
            Filter filter = TracingFilter.create((HttpTracing)httpTracing);
            filter.init(Objects.requireNonNull(ZipkinTracingFilter.this.filterConfig));
            return filter;
        }
    };

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (ContainerManager.isContainerSetup()) {
            ((Filter)this.tracingFilter.get()).doFilter(servletRequest, servletResponse, filterChain);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {
        if (this.tracingFilter.isInitialized()) {
            ((Filter)this.tracingFilter.get()).destroy();
            this.tracingFilter.reset();
        }
    }
}

