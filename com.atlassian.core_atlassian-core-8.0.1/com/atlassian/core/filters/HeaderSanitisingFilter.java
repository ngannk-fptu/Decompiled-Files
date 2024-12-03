/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.core.filters;

import com.atlassian.core.filters.AbstractFilter;
import com.atlassian.core.filters.HeaderSanitisingResponseWrapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HeaderSanitisingFilter
extends AbstractFilter {
    static final String ALREADY_FILTERED = HeaderSanitisingFilter.class.getName() + "_already_filtered";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getAttribute(ALREADY_FILTERED) != null) {
            chain.doFilter(req, res);
            return;
        }
        req.setAttribute(ALREADY_FILTERED, (Object)Boolean.TRUE);
        if (req instanceof HttpServletRequest) {
            res = new HeaderSanitisingResponseWrapper((HttpServletResponse)res);
        }
        chain.doFilter(req, res);
    }
}

