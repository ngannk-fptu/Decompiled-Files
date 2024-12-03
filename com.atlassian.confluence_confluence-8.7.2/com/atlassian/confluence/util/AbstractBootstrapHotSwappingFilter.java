/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBootstrapHotSwappingFilter
extends AbstractHttpFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractBootstrapHotSwappingFilter.class);
    private final AtomicReference<Filter> filterTarget = new AtomicReference<SwapOnBootstrapFilter>(new SwapOnBootstrapFilter());

    public abstract Filter getSwapTarget() throws ServletException;

    public final void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.filterTarget.get().doFilter((ServletRequest)servletRequest, (ServletResponse)servletResponse, filterChain);
    }

    private class SwapOnBootstrapFilter
    extends AbstractHttpFilter {
        private SwapOnBootstrapFilter() {
        }

        public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            if (SetupContext.isAvailable()) {
                filterChain.doFilter((ServletRequest)servletRequest, (ServletResponse)servletResponse);
                return;
            }
            try {
                AbstractBootstrapHotSwappingFilter.this.filterTarget.compareAndSet((Filter)this, AbstractBootstrapHotSwappingFilter.this.getSwapTarget());
                AbstractBootstrapHotSwappingFilter.this.doFilter(servletRequest, servletResponse, filterChain);
            }
            catch (RuntimeException | ServletException e) {
                LOG.debug("Could not get swap target filter", e);
                LOG.error("Could not get swap target filter", (Object)e.getMessage());
                filterChain.doFilter((ServletRequest)servletRequest, (ServletResponse)servletResponse);
            }
        }
    }
}

