/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.scopes.web;

import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import com.atlassian.oauth2.scopes.request.basic.BasicScope;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadWriteScopeFilter
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ReadWriteScopeFilter.class);
    private final ScopesRequestCache scopesRequestCache;

    public ReadWriteScopeFilter(ScopesRequestCache scopesRequestCache) {
        this.scopesRequestCache = scopesRequestCache;
    }

    public void init(FilterConfig filterConfig) {
        logger.info("Initializing: [{}]", (Object)ReadWriteScopeFilter.class.getSimpleName());
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (this.isReadScope()) {
            HttpServletRequest httpServletRequest = (HttpServletRequest)request;
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            if (!"GET".equalsIgnoreCase(httpServletRequest.getMethod())) {
                logger.debug("Request does not have the required scope to complete request");
                httpServletResponse.sendError(403, "Request does not have write scope. Cannot complete " + httpServletRequest.getMethod() + " request.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isReadScope() {
        return this.scopesRequestCache.containsOnlyThisScope((Scope)BasicScope.READ);
    }

    public void destroy() {
        logger.info("Destroying: [{}]", (Object)ReadWriteScopeFilter.class.getSimpleName());
    }
}

