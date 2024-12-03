/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.core.filters.cache;

import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.core.filters.cache.CachingStrategy;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractCachingFilter
extends AbstractHttpFilter {
    @Override
    public final void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CachingStrategy strategy = this.getFirstMatchingStrategy(request);
        if (strategy != null) {
            strategy.setCachingHeaders(response);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private CachingStrategy getFirstMatchingStrategy(HttpServletRequest request) {
        CachingStrategy[] strategies = this.getCachingStrategies();
        if (strategies == null) {
            return null;
        }
        for (CachingStrategy strategy : strategies) {
            if (!strategy.matches(request)) continue;
            return strategy;
        }
        return null;
    }

    protected abstract CachingStrategy[] getCachingStrategies();

    @Override
    public final void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    public final void destroy() {
        super.destroy();
    }
}

