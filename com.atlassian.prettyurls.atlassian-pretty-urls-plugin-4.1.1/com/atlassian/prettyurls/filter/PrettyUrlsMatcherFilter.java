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
import com.atlassian.prettyurls.internal.route.UrlRouter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrettyUrlsMatcherFilter
extends PrettyUrlsCommonFilter {
    private static final Logger log = LoggerFactory.getLogger(PrettyUrlsMatcherFilter.class);
    private final UrlRouter urlRouter;

    public PrettyUrlsMatcherFilter(UrlRouter urlRouter) {
        this.urlRouter = urlRouter;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        String fromURI = httpServletRequest.getRequestURI();
        UrlRouter.Result result = this.urlRouter.route(httpServletRequest, this.location);
        if (result.isRouted()) {
            String toURI = result.toURI();
            if (log.isDebugEnabled()) {
                log.debug("Will route from {} ==> {}", (Object)fromURI, (Object)toURI);
            }
            this.setInformationRequestVariables(httpServletRequest, fromURI, toURI);
            httpServletRequest.setAttribute("pretty.urls.performRoute", (Object)Boolean.TRUE);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

