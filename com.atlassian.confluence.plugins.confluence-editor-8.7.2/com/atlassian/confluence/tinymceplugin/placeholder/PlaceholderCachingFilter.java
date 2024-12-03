/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.web.filter.CachingHeaders
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.web.filter.CachingHeaders;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class PlaceholderCachingFilter
implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletResponse instanceof HttpServletResponse && !ConfluenceSystemProperties.isDevMode()) {
            CachingHeaders.PUBLIC_LONG_TERM.apply((HttpServletResponse)servletResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}

