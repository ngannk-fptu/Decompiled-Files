/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.prettyurls.filter;

import com.atlassian.plugin.servlet.filter.FilterLocation;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public abstract class PrettyUrlsCommonFilter
implements Filter {
    public static final String PRETTY_URLS_FROM_URI = "pretty.urls.fromURI";
    public static final String PRETTY_URLS_TO_URI = "pretty.urls.toURI";
    public static final String PRETTY_URLS_PERFORM_ROUTE = "pretty.urls.performRoute";
    protected FilterLocation location;

    public void init(FilterConfig filterConfig) throws ServletException {
        String parameter = filterConfig.getInitParameter("location");
        parameter = parameter == null || parameter.isEmpty() ? "before-dispatch" : parameter;
        this.location = FilterLocation.parse((String)parameter);
    }

    public void destroy() {
    }

    protected HttpServletRequest preventDoubleInvocation(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        if (httpServletRequest.getAttribute(this.getClass().getName()) != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return null;
        }
        httpServletRequest.setAttribute(this.getClass().getName(), (Object)true);
        return httpServletRequest;
    }

    protected void setInformationRequestVariables(HttpServletRequest httpServletRequest, String fromURI, String toURI) {
        httpServletRequest.setAttribute(PRETTY_URLS_FROM_URI, (Object)fromURI);
        httpServletRequest.setAttribute(PRETTY_URLS_TO_URI, (Object)toURI);
    }
}

