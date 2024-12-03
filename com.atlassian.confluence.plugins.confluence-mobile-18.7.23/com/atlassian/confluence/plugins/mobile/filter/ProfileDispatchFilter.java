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

public class ProfileDispatchFilter
extends AbstractHttpFilter {
    private String redirectPrefix;

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.redirectPrefix = filterConfig.getInitParameter("redirectPrefix");
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!MobileUtils.isMobileViewRequest(request)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String uri = request.getRequestURI();
        if (!ProfileDispatchFilter.isProfileRequestURI(uri)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String username = StringUtils.substringAfterLast((String)request.getRequestURI(), (String)"~");
        response.sendRedirect(request.getContextPath() + "/plugins/servlet/" + this.redirectPrefix + username);
    }

    static boolean isProfileRequestURI(String uri) {
        int pos = StringUtils.lastIndexOf((CharSequence)uri, (int)126);
        if (pos == -1 || pos == StringUtils.length((CharSequence)uri) - 1) {
            return false;
        }
        return StringUtils.indexOf((CharSequence)uri, (int)47, (int)pos) == -1;
    }
}

