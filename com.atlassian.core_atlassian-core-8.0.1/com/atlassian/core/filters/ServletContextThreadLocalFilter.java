/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.core.filters;

import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.core.filters.ServletContextThreadLocal;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletContextThreadLocalFilter
extends AbstractHttpFilter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest originalRequest = ServletContextThreadLocal.getRequest();
        HttpServletResponse originalResponse = ServletContextThreadLocal.getResponse();
        try {
            ServletContextThreadLocal.setRequest(request);
            ServletContextThreadLocal.setResponse(response);
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            ServletContextThreadLocal.setRequest(originalRequest);
            ServletContextThreadLocal.setResponse(originalResponse);
        }
    }
}

