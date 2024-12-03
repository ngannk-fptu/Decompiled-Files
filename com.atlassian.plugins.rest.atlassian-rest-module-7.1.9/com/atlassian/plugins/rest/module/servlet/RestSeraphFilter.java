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
package com.atlassian.plugins.rest.module.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestSeraphFilter
implements Filter {
    public static final String DEFAULT_ATTRIBUTE = "os_authTypeDefault";

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        if (httpRequest.getServletPath().startsWith("/rest/")) {
            if (httpRequest.getAttribute(DEFAULT_ATTRIBUTE) == null) {
                httpRequest.setAttribute(DEFAULT_ATTRIBUTE, (Object)"any");
            }
            chain.doFilter(request, (ServletResponse)httpResponse);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}

