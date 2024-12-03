/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenAuthBlockNotWhitelistedFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(TokenAuthBlockNotWhitelistedFilter.class);

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Object tokenAuthWhitelistOKAttribute = request.getAttribute("TokenAuthWhitelistOK");
        Boolean tokenAuthWhitelistOK = tokenAuthWhitelistOKAttribute instanceof Boolean && (Boolean)tokenAuthWhitelistOKAttribute != false;
        if (tokenAuthWhitelistOK.booleanValue()) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        } else {
            log.warn("TokenAuthBlockNotWhitelistedFilter: blocking request with url - {}", (Object)request.getRequestURI().replaceAll("[\r\n]", ""));
        }
    }
}

