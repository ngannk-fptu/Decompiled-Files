/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Named
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.crowd.plugin.rest.filter;

import java.io.IOException;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Named
public class SeraphDisablerFilter
implements Filter {
    private static final String DISABLE_SECURITY_FILTER_FLAG = "os_securityfilter_already_filtered";
    private static final String DISABLE_LOGIN_FILTER_FLAG = "loginfilter.already.filtered";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setAttribute(DISABLE_SECURITY_FILTER_FLAG, (Object)Boolean.TRUE);
        servletRequest.setAttribute(DISABLE_LOGIN_FILTER_FLAG, (Object)Boolean.TRUE);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}

