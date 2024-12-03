/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mobile.filter;

import com.atlassian.confluence.plugins.mobile.MobileUtils;
import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class RedirectFilter
extends AbstractHttpFilter {
    private String redirectLocation;
    private String skipParameter;

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.redirectLocation = filterConfig.getInitParameter("redirectLocation");
        this.skipParameter = filterConfig.getInitParameter("skipFilteringParameterName");
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        boolean skipFilter;
        String skipParamValue = this.skipParameter != null ? request.getParameter(this.skipParameter) : null;
        boolean bl = skipFilter = StringUtils.isNotBlank((CharSequence)skipParamValue) && Boolean.valueOf(skipParamValue) != false;
        if (!skipFilter && MobileUtils.isMobileViewRequest(request)) {
            response.sendRedirect(request.getContextPath() + this.redirectLocation + (String)(StringUtils.isNotBlank((CharSequence)request.getQueryString()) ? "?" + request.getQueryString() : ""));
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

