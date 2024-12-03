/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.core.filters;

import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.core.filters.cache.CachingStrategy;
import com.atlassian.core.filters.cache.JspCachingStrategy;
import com.atlassian.core.filters.encoding.FixedHtmlEncodingResponseWrapper;
import com.atlassian.core.filters.legacy.NoContentLocationHeaderResponseWrapper;
import com.atlassian.core.filters.legacy.WordCurlyQuotesRequestWrapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractEncodingFilter
extends AbstractHttpFilter {
    private final CachingStrategy jspCachingStrategy = new JspCachingStrategy();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setCharacterEncoding(this.getEncoding());
        response.setContentType(this.getContentType());
        if (this.isNonCachableUri(request)) {
            this.setNonCachingHeaders(response);
        }
        filterChain.doFilter((ServletRequest)new WordCurlyQuotesRequestWrapper(request, this.getEncoding()), (ServletResponse)new FixedHtmlEncodingResponseWrapper((HttpServletResponse)new NoContentLocationHeaderResponseWrapper(response)));
    }

    protected void setNonCachingHeaders(HttpServletResponse response) {
        this.jspCachingStrategy.setCachingHeaders(response);
    }

    protected boolean isNonCachableUri(HttpServletRequest request) {
        return this.jspCachingStrategy.matches(request);
    }

    protected abstract String getEncoding();

    protected abstract String getContentType();
}

