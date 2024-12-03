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
 */
package com.atlassian.plugin.servlet.cache.filter;

import com.atlassian.plugin.servlet.cache.model.CacheableRequest;
import com.atlassian.plugin.servlet.cache.model.CacheableResponse;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ETagCachingFilter
implements Filter {
    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        CacheableRequest wrappedRequest = new CacheableRequest(request);
        CacheableResponse wrappedResponse = new CacheableResponse(response);
        chain.doFilter((ServletRequest)wrappedRequest, (ServletResponse)wrappedResponse);
        wrappedResponse.setETagHeader();
        if (wrappedResponse.isCacheable(wrappedRequest)) {
            response.setStatus(304);
        } else {
            wrappedResponse.flushResponse();
        }
    }

    public void destroy() {
    }
}

