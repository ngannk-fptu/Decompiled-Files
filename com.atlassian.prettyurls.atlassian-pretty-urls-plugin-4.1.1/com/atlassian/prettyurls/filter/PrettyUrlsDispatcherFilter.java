/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.prettyurls.filter;

import com.atlassian.prettyurls.filter.PrettyUrlsCommonFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrettyUrlsDispatcherFilter
extends PrettyUrlsCommonFilter {
    private static final Logger log = LoggerFactory.getLogger(PrettyUrlsDispatcherFilter.class);

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        if (httpServletRequest.getAttribute("pretty.urls.performRoute") != null) {
            String fromURI = (String)httpServletRequest.getAttribute("pretty.urls.fromURI");
            String toURI = (String)httpServletRequest.getAttribute("pretty.urls.toURI");
            httpServletRequest.removeAttribute("pretty.urls.performRoute");
            if (log.isDebugEnabled()) {
                log.debug("Routing {} ==> {}", (Object)fromURI, (Object)toURI);
            }
            httpServletRequest.getRequestDispatcher(toURI).forward(servletRequest, servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

