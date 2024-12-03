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

import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExpiresFilter
extends AbstractHttpFilter {
    private int expiryTimeInSeconds = 0;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        String str = filterConfig.getInitParameter("expiryTimeInSeconds");
        if (str != null) {
            try {
                this.expiryTimeInSeconds = Integer.parseInt(str);
            }
            catch (NumberFormatException nfe) {
                throw new ServletException("'" + str + "' is not a valid integer.", (Throwable)nfe);
            }
        }
        if (System.getProperty("atlassian.disable.caches", "false").equals("true")) {
            this.expiryTimeInSeconds = 0;
        }
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (this.expiryTimeInSeconds > 0) {
            response.setDateHeader("Expires", new Date().getTime() + (long)(this.expiryTimeInSeconds * 1000));
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

