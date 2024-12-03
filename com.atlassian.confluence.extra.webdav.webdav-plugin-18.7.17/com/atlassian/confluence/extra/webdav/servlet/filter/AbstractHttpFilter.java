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
package com.atlassian.confluence.extra.webdav.servlet.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractHttpFilter
implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse;
        HttpServletRequest httpServletRequest;
        if (servletRequest instanceof HttpServletRequest && this.handles(httpServletRequest = (HttpServletRequest)servletRequest, httpServletResponse = (HttpServletResponse)servletResponse)) {
            this.doFilter((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse, filterChain);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public abstract void doFilter(HttpServletRequest var1, HttpServletResponse var2, FilterChain var3) throws IOException, ServletException;

    protected boolean handles(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        return true;
    }

    public void destroy() {
    }
}

