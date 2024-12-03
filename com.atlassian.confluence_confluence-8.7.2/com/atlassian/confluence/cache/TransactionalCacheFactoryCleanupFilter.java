/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.cache;

import com.atlassian.confluence.cache.TransactionalCacheFactoryCleaner;
import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TransactionalCacheFactoryCleanupFilter
extends AbstractHttpFilter {
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try (TransactionalCacheFactoryCleaner.Cleaner ignored = TransactionalCacheFactoryCleaner.cleaner(request.getRequestURI());){
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
}

