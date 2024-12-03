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
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.springframework.web.context.request.async.WebAsyncUtils
 */
package com.atlassian.confluence.internal.web.filter.spring;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.web.context.request.async.WebAsyncUtils;

public class IgnoreWebAsyncManagerFilter
implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            servletRequest = new HttpServletRequestWrapper((HttpServletRequest)servletRequest){

                public void setAttribute(String name, Object o) {
                    if (!WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE.equals(name)) {
                        super.setAttribute(name, o);
                    }
                }
            };
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }
}

