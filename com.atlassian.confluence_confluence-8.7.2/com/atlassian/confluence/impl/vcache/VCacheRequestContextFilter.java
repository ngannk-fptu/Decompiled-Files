/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.vcache.internal.VCacheLifecycleManager
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Throwables
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.confluence.impl.vcache.VCacheMetricsLogger;
import com.atlassian.confluence.impl.vcache.VCacheRequestContextManager;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.vcache.internal.VCacheLifecycleManager;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VCacheRequestContextFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(VCacheRequestContextFilter.class);
    private final Supplier<VCacheLifecycleManager> lifecycleManagerRef = Lazy.supplier(() -> (VCacheLifecycleManager)ContainerManager.getComponent((String)"vcacheFactory"));
    private final Supplier<VCacheRequestContextManager> requestContextManagerRef = Lazy.supplier(() -> (VCacheRequestContextManager)ContainerManager.getComponent((String)"vcacheRequestContextManager"));

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (ContainerManager.isContainerSetup()) {
            try {
                log.debug("Setting up vcache request context for {}", (Object)request.getRequestURI());
                VCacheRequestContextManager requestContextManager = (VCacheRequestContextManager)this.requestContextManagerRef.get();
                Stopwatch stopwatch = Stopwatch.createStarted();
                requestContextManager.doInRequestContext(() -> {
                    filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
                    VCacheMetricsLogger.logMetrics(request.getServletPath(), stopwatch.stop(), () -> ((VCacheLifecycleManager)this.lifecycleManagerRef.get()).metrics(requestContextManager.getCurrentRequestContext()));
                    return null;
                });
                log.debug("Cleared vcache request context for {}", (Object)request.getRequestURI());
            }
            catch (IOException | ServletException ex) {
                throw ex;
            }
            catch (Exception e) {
                throw Throwables.propagate((Throwable)e);
            }
        } else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
}

