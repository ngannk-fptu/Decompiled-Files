/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.prettyurls.filter;

import com.atlassian.prettyurls.filter.PrettyUrlsCommonFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PrettyUrlsSiteMeshFixupFilter
extends PrettyUrlsCommonFilter {
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = this.preventDoubleInvocation(servletRequest, servletResponse, filterChain);
        if (httpServletRequest == null) {
            return;
        }
        if (httpServletRequest.getAttribute("com.opensymphony.sitemesh.APPLIED_ONCE") != null) {
            servletResponse.setContentType(servletResponse.getContentType());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

