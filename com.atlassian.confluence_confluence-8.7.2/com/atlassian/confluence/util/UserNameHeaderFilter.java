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
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class UserNameHeaderFilter
extends AbstractHttpFilter {
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String username = request.getRemoteUser();
        if (StringUtils.isNotEmpty((CharSequence)username) && !UserNameHeaderFilter.isStaticResourcePath(request.getServletPath(), request.getPathInfo())) {
            response.setHeader("X-AUSERNAME", username);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private static boolean isStaticResourcePath(String servletPath, String pathInfo) {
        return "/s".equals(servletPath) || "/download".equals(servletPath) && (pathInfo.startsWith("/resources") || pathInfo.startsWith("/sources") || pathInfo.startsWith("/contextbatch") || pathInfo.startsWith("/batch"));
    }
}

